package jog;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

/**
 * <h1>jog.window</h1>
 * <p>Provides a layer upon LWJGL and Slick. jog.window allows a window to be created and managed.</p>
 * @author IMP1
 */
public abstract class window {
	
	final private static int FPS = 60;
	
	private static int width;
	private static int height;
	private static boolean closed;
	
	/**
	 * Creates a new window.
	 * @param title the title of the window.
	 * @param width the width of the window.
	 * @param height the height of the window.
	 */
	public static void initialise(String title, int width, int height) {
		try {
			setSize(width, height);
			setTitle(title);
			Display.create();
			closed = false;
		} catch (LWJGLException e) {
			e.printStackTrace();
			closed = true;
		}
	}
	
	/** 
	 * Allows for changing the size of the window.
	 * <p>It does this by creating a new DisplayMode with a specified
	 * width and height, and sets the Display's DisplayMode to 
	 * that new DisplayMode.</p>
	 * @param width the new width for the window.
	 * @param height the new height for the window.
	 */
	public static void setSize(int width, int height) {
		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			window.width = width;
			window.height = height;
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Allows access to the width of the window.
	 * @return the width of the window.
	 */
	public static int width() {
		return width;
	}
	
	/**
	 * Allows access to the height of the window.
	 * @return the height of the window.
	 */
	public static int height() {
		return height;
	}
	
	/**
	 * Allows access to the closed status of the window.
	 * @return whether the window is closed.
	 */
	public static boolean isClosed() {
		return closed;
	}
	
	/**
	 * Allows for changing the title of the window.
	 * @param title the new title for the window.
	 */
	public static void setTitle(String title) {
		Display.setTitle(title);
	}
	
	/**
	 * Allows for changing the icon of the window.
	 * @param filepaths a list of filepaths of the icons. The names of 
	 * the icons must end with the size in pixels, for example 
	 * icon_filename16.png for the 16 by 16 icon.
	 */
	public static void setIcon(String[] filepaths) {
		try {
			ByteBuffer[] icons = new ByteBuffer[filepaths.length];
			for (int i = 0; i < icons.length ; i ++) {
				String name = filepaths[i].split("\\.")[0];
				int size = name.length();
				while (name.substring(size-1).matches("\\d+")) {
					size -= 1;
				}
				int width = Integer.parseInt(name.substring(size));
				icons[i] = loadIcon(filepaths[i], width);
			}
			Display.setIcon(icons);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static ByteBuffer loadIcon(String filename, int size) throws IOException {
		URL url = window.class.getResource(File.separator + filename);
		if (url == null) url = window.class.getResource(File.separator + ".." + File.separator + filename);
		if (url == null) url = window.class.getResource(File.separator + ".." + File.separator + ".." + File.separator + filename);
		BufferedImage img = ImageIO.read(url);
		byte[] imageBytes = new byte[size * size * 4];
	    for (int y = 0; y < size; y++) {
	        for (int x = 0; x < size; x++) {
	            int pixel = img.getRGB(y, x);
	            for (int k = 0; k < 3; k++) {
	                imageBytes[(x*size+y)*4 + k] = (byte)(((pixel >> (2-k) * 8)) & 255); // red, green, blue
	            	imageBytes[(x*size+y)*4 + 3] = (byte)(((pixel >> (3) * 8)) & 255); // alpha
	            }
	        }
	    }
		return ByteBuffer.wrap(imageBytes);
	}
	
	/**
	 * Updates the window and syncs it with the specified FPS. 
	 * It also updates whether the window has been closed or not.
	 */
	public static void update() {
		closed = closed || Display.isCloseRequested();
		if (closed) return;
		Display.update();
		Display.sync(FPS);
	}
	
	/**
	 * Disposes of the resources used by the window.
	 */
	public static void dispose() {
		Display.destroy();
	}

}