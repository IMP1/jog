package jog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class filesystem {

	public static URL load(String filename) {
		URL url = filesystem.class.getResource(File.separator + filename);
		if (url == null) {
			url = filesystem.class.getResource(File.separator + ".." + File.separator + filename);
		}
		if (url == null) {
			url = filesystem.class.getResource(File.separator + ".." + File.separator + "gfx" + File.separator + filename);
		}
		if (url == null) {
			url = filesystem.class.getResource(File.separator + ".." + File.separator + "sfx" + File.separator + filename);
		}
		return url;
	}
	
    public static String loadAsString(String filename) throws Exception {
        StringBuilder source = new StringBuilder();
        FileInputStream in = new FileInputStream(filename);
        BufferedReader reader;
        try{
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            for (String line = reader.readLine(); line != null;) {
                source.append(line).append('\n');
            }
        	reader.close();
        } catch(Exception e) {
        	e.printStackTrace();
        	System.exit(0);
        }
        in.close();
        return source.toString();
    }

}
