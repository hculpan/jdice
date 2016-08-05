package org.culpan.jdice.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * You are free to use this code however you like but I assume no responsibility
 * for it's correctness and provide no warranty of any kind.
 * 
 * @author Michael Connor
 */
public class Classpath {

    public static interface FileFilter {
        /**
         * All paths will be represented using forward slashes and no files will
         * begin with a slash
         */
        public boolean accept(String filename);
    }

    /**
     * Returns a list of the classes on the classpath. The names returned will
     * be appropriate for using Class.forName(String) in that the slashes will
     * be changed to dots and the .class file extension will be removed.
     */
    public static String[] getClasspathClassNames() throws ZipException, IOException {
        String[] classes = getClasspathFileNamesWithExtension(".class");
        for (int i = 0; i < classes.length; i++) {
            classes[i] = classes[i].substring(0, classes[i].length() - 6).replace('/', '.');
        }
        return classes;
    }

    public static String[] getClasspathFileNamesWithExtension(final String extension) throws ZipException, IOException {
        return getClasspathFileNames(new FileFilter() {
            public boolean accept(String filename) {
                return filename.endsWith(extension);
            }
        });
    }

    public static String[] getClasspathFileNames(FileFilter filter) throws ZipException, IOException {
        List<String> filenames = new ArrayList<String>();
        String[] cpFilenames = getClasspathFileNames();
        for (int i = 0; i < cpFilenames.length; i++) {
            String filename = cpFilenames[i];
            if (filter.accept(filename)) {
                filenames.add(filename);
            }
        }
        return filenames.toArray(new String[filenames.size()]);
    }

    /**
     * Returns the fully qualified class names of all the classes in the
     * classpath. Checks directories and zip files. The FilenameFilter will be
     * applied only to files that are in the zip files and the directories. In
     * other words, the filter will not be used to sort directories.
     */
	public static String[] getClasspathFileNames() throws ZipException, IOException {
        StringTokenizer tokenizer = new StringTokenizer(System.getProperty("java.class.path"), File.pathSeparator,
                false);
        Set<String> filenames = new LinkedHashSet<String>();

        while (tokenizer.hasMoreTokens()) {
            String classpathElement = tokenizer.nextToken();
            File classpathFile = new File(classpathElement);

            if (classpathFile.exists() && classpathFile.canRead()) {
                if (classpathElement.toLowerCase().endsWith(".jar")) {
                    ZipFile zip = new ZipFile(classpathFile);
                    Enumeration<?> entries = zip.entries();

                    while (entries.hasMoreElements()) {
                        ZipEntry entry = (ZipEntry) entries.nextElement();
                        if (!entry.isDirectory()) {
                            filenames.add(entry.getName());
                        }
                    }

                } else if (classpathFile.isDirectory()) {
                    // lets go through and find all of the subfolders
                    Set<File> directoriesToSearch = new HashSet<File>();
                    Set<File> newDirectories = new HashSet<File>();
                    directoriesToSearch.add(classpathFile);
                    String basePath = classpathFile.getAbsolutePath();

                    for (File searchDirectory : directoriesToSearch) {
                        File[] directoryFiles = searchDirectory.listFiles();
                        for (int cnt = 0; cnt < directoryFiles.length; cnt++) {
                            File directoryFile = directoryFiles[cnt];
                            if (directoryFile.isDirectory()) {
                                newDirectories.add(directoryFile);
                            } else {
                                filenames.add(directoryFile.getAbsolutePath().substring(basePath.length() + 1));
                            }
                        }
                    }
                    directoriesToSearch.clear();
                    directoriesToSearch.addAll(newDirectories);
                    newDirectories.clear();
                }
            }
        }

        String[] uniqueNames = new String[filenames.size()];
        int index = 0;

        for (String name : filenames) {
            uniqueNames[index++] = name.replace('\\', '/');
        }

        return uniqueNames;
    }

    public static void main(String[] args) throws Exception {
        String[] names = getClasspathClassNames();
        for (int i = 0; i < names.length; i++) {
            System.out.println(names[i]);
        }
    }
}