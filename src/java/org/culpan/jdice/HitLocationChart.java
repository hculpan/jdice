package org.culpan.jdice;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.jdom.Element;

public class HitLocationChart {
	final static Logger logger = Logger.getLogger(HitLocationChart.class);
	
    public static class Location {
        public int number;
        public String name;
        public float stunx;
        public float nstun;
        public float bodyx;
        public int toHit;
        
        public Location() {
            
        }
        
        public Location(int number, String name, float stunx, float nstun, float bodyx, int toHit) {
            this.number = number;
            this.name = name;
            this.stunx = stunx;
            this.nstun = nstun;
            this.bodyx = bodyx;
            this.toHit = toHit;
        }
        
        public Location(Element loc) {
            number = Integer.parseInt(loc.getAttributeValue("id"));
            name = loc.getAttributeValue("name");
            stunx = Float.parseFloat(loc.getChildText("stunx"));
            nstun = Float.parseFloat(loc.getChildText("nstun"));
            bodyx = Float.parseFloat(loc.getChildText("bodyx"));
            toHit = Integer.parseInt(loc.getChildText("to-hit"));
        }
        
        public boolean equals(Object o) {
        	if (o != null && o instanceof Location) {
        		Location l = (Location)o;
        		return (l.name.equals(name));
        	} else {
        		return false;
        	}
        }
        
        public String toString() {
        	return name;
        }
    }
    
    protected String name;

    protected String filename;
    
    protected boolean fromClasspath = true;

    protected String ref;
    
    protected Location [] locations = new Location[19];

    private HitLocationChart() {
        
    }
    
    public static HitLocationChart createInstanceFromClasspath(String filename) {
        HitLocationChart result = null;
        InputStream input = HitLocationChart.class.getClassLoader().getResourceAsStream(filename);
        if (input != null) {
        	result = createInstanceFromStream(input);
            result.setFilename(filename);
        }
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
	protected static HitLocationChart createInstanceFromStream(InputStream input) {
        HitLocationChart result = createInstance();
    	
        Element root = Utils.loadXml(input);
        if (root.getName().equalsIgnoreCase("hit-location-chart")) {
            result.setName(root.getAttributeValue("name"));
            result.setRef(root.getAttributeValue("ref"));
            
            for (Iterator i = root.getChildren("location").iterator(); i.hasNext();) {
                Element loc = (Element)i.next();
                Location l = new Location(loc);
                result.locations[l.number] = l;
            }
        }
        
        return result;
    }
    
    public static HitLocationChart createInstanceFromFile(File file) {
    	HitLocationChart result = null;

    	try {
    		InputStream input = new FileInputStream(file);
    		result = createInstanceFromStream(input);
    		result.setFilename(file.getCanonicalPath());
    	} catch (Exception e) {
    		logger.error(e.getLocalizedMessage());
    	}
    	
        return result; 
    }
    
    public static HitLocationChart createInstance() {
        return new HitLocationChart();
    }

    /**
     * @return Returns the filename.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename
     *            The filename to set.
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the ref.
     */
    public String getRef() {
        return ref;
    }

    /**
     * @param ref
     *            The ref to set.
     */
    public void setRef(String ref) {
        this.ref = ref;
    }
    
    public Location getLocation(int num) {
        return locations[num]; 
    }

	public boolean isFromClasspath() {
		return fromClasspath;
	}

	public void setFromClasspath(boolean fromClasspath) {
		this.fromClasspath = fromClasspath;
	}
}
