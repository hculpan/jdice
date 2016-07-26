/*
 * Created on Oct 7, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.culpan.jdice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Random;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * @author culpanh
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Utils {
    public static Random random = new Random();
    
    public static ErrorListener errorListener = null;
    
    public static void notifyError(Throwable exc) {
        if (errorListener != null) {
            errorListener.error(exc);
        } else {
            exc.printStackTrace(System.err);
        }
    }
    
	public static Element loadXml(File file) {
		Element result = null;
		try {
			SAXBuilder builder = new SAXBuilder();

			Document doc = builder.build(file);
			result = doc.getRootElement();
		} catch (Exception e) {
			e.printStackTrace();
			result = null;
		}
		
		return result;
	}
	
    public static Element loadXml(InputStream file) {
        Element result = null;
        try {
            SAXBuilder builder = new SAXBuilder();

            Document doc = builder.build(file);
            result = doc.getRootElement();
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        }
        
        return result;
    }
    
	public static void saveXml(Element root, String outputFilename) {
        XMLOutputter serializer = new XMLOutputter();
        serializer.setFormat(Format.getPrettyFormat());

        Document doc = root.getDocument();
        if (doc != null) {
            doc.detachRootElement();
        }

        try {
            PrintStream output = null;
            if (outputFilename == null) {
                output = System.out;
            } else {
                output = new PrintStream(new FileOutputStream(outputFilename));
            }

            serializer.output(new Document(root), output);
            output.close();
        } catch (IOException e) {
            notifyError(e);
        }
	}
	
	public static boolean equals(String s1, String s2) {
		if (s1 == null && s2 == null) {
			return true;
		} else if (s1 == null || s2 == null) {
			return false;
		} else {
			return s1.equals(s2);
		}
	}

	public static int rnd(int range) {
        return random.nextInt(range);
    }
    
    public static double rnd() {
        return random.nextDouble();
    }
}
