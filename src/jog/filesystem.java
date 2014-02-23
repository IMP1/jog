package jog;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * <h1>jog.filesystem</h1>
 * <p>Used for accessing files within the project.</p>
 * @author IMP1
 */
public abstract class filesystem {

	/**
	 * Holds locations to be searched for files.
	 */
	private static ArrayList<String> locations = new ArrayList<String>();
	
	/**
	 * Adds a location to be tracked.
	 * @param filepath the path to the directory to be added.
	 */
	public static void addLocation(String filepath) { addLocation(filepath, true, 0); }
	/**
	 * Adds a location to be tracked.
	 * @param filepath the path to the directory to be added.
	 * @param subFolders whether to recursively add sub-folders.
	 */
	public static void addLocation(String filepath, boolean subFolders) { addLocation(filepath, subFolders, 0); }
	private static void addLocation(String filepath, boolean subFolders, int depth) {
		// Create file
		File newLocation = new File(filepath);
		String path = newLocation.getAbsolutePath();
		// Warn if pre-existing.
		if (locations.contains(path)) {
			System.err.println("Location \"" + filepath + "\" is already included.");
			return;
		}
		// Add location
		locations.add(path);
		// TODO remove debugging 'println's
		for (int i = 0; i < depth; i ++) System.out.print("\t");
		// ~~~~~~
		// If we don't want to look any further
		if (!subFolders) {			
			return;
		}
		// If we _do_ want to look further, create array of files
		for (File child : newLocation.listFiles()) {
			// Make sure they're directories
			if (child.isDirectory()) {
				// And add them too (searching through recursively)
				addLocation(child.getAbsolutePath(), true, depth+1);
			}
		}
	}
	
	/**
	 * Gets a URL for the file, if it exists within the project.
	 * @param filename the name of the file.
	 * @return the URL for the file.
	 */
	public static URL getURL(String filename) {
		File f = new File(getPath(filename));
		try {
			return f.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException("Path exists to, but no URL can be created for \"" + filename + "\".");
		}
	}
	
	/**
	 * Searched through all tracked locations for the file.
	 * @param filename the file to search for.
	 * @return the path to the file.
	 */
	public static String getPath(String filename) {
		String path = "";
		File f;
		for (String loc : locations) {
			path = loc + File.separator + filename;
			f = (new File(path)).getAbsoluteFile();
			if (f.exists()) {
				return path;
			}
		}
		throw new RuntimeException("No path to file: " + filename);
	}

}
