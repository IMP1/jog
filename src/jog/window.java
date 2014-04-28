package jog;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

/**
 * <h1>jog.window</h1>
 * <p>Provides a layer upon LWJGL and Slick. jog.window allows a window to be created and managed.</p>
 * @author IMP1
 */
public abstract class window {
	
	public enum WindowMode {
		WINDOWED,
		FULLSCREEN,
		BORDERLESS_WINDOWED,
		BORDERLESS_FULLSCREEN,
	}
	
	private static int targetFPS;
	private static int width;
	private static int height;
	private static boolean closed;
	private static double lastFrameTime;
	private static WindowMode currentWindowMode;
	
	/**
	 * Creates a new window.
	 * @param title the title of the window.
	 * @param width the width of the window.
	 * @param height the height of the window.
	 * @param targetFPS the frames per second to sync to if too fast.
	 * @param mode the {@link WindowMode} to use.
	 */
	public static void initialise(String title, int width, int height, int targetFPS, WindowMode mode, boolean resizable) {
		try {
			window.closed = false;
			window.targetFPS = targetFPS;
			window.lastFrameTime = 0;
			setSize(width, height);
			setTitle(title);
			setMode(mode);
			setResizable(resizable);
			Display.create();
			getDeltaTime();
		} catch (LWJGLException e) {
			e.printStackTrace();
			closed = true;
		}
	}

	/**
	 * Creates a new window with the windowed {@link WindowMode}
	 * @param title the title of the window.
	 * @param width the width of the window.
	 * @param height the height of the window.
	 * @param targetFPS the frames per second to sync to if too fast.
	 */
	public static void initialise(String title, int width, int height, int targetFPS) {
		window.initialise(title, width, height, targetFPS, WindowMode.WINDOWED, false);
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
	 * Allows access to the mode the window is in.
	 * @return the window mode.
	 */
	public static WindowMode getMode() {
		return currentWindowMode;
	}
	
	/**
	 * Allows access to the window's position.
	 * @return the x coordinate of the window.
	 */
	public static int getX() {
		return Display.getX();
	}
	
	/**
	 * Allows access to the window's position.
	 * @return the y coordinate of the window.
	 */
	public static int getY() {
		return Display.getY();
	}

	/** 
	 * Allows for changing the size of the window.
	 * <p>It does this by creating a new DisplayMode with a specified
	 * width and height, and sets the Display's DisplayMode to 
	 * that new DisplayMode.</p>
	 * @param newWidth the new width for the window.
	 * @param newHeight the new height for the window.
	 */
	public static void setSize(int newWidth, int newHeight) {
		try {
			Display.setDisplayMode(new DisplayMode(newWidth, newHeight));
			refreshWindowSize();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This handles the window's mode, setting the window to be borderless if need be, and to be fullscreen if need be.
	 * @param mode the window mode to use.
	 */
	public static void setMode(WindowMode mode) {
		currentWindowMode = mode;
		if (mode == WindowMode.BORDERLESS_FULLSCREEN) {
			System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
			setSize(Display.getDesktopDisplayMode().getWidth(), Display.getDesktopDisplayMode().getHeight());
		} else if (mode == WindowMode.BORDERLESS_WINDOWED) {
			System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
		} else if (mode == WindowMode.FULLSCREEN) {
			setSize(Display.getDesktopDisplayMode().getWidth(), Display.getDesktopDisplayMode().getHeight());
			try {
				Display.setDisplayMode(Display.getDesktopDisplayMode());
				Display.setFullscreen(true);
			} catch (LWJGLException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * Allows for the setting of the window's position on the user's monitor.
	 * @param x the x coordinate of the window.
	 * @param y the y coordinate of the window.
	 */
	public static void setPosition(int x, int y) {
		Display.setLocation(x, y);
	}
	
	/**
	 * Allows for letting the user resize the window.
	 * @param resizable whether the window can be resized.
	 */
	public static void setResizable(boolean resizable) {
		Display.setResizable(resizable);
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
	
	/**
	 * Loads an image into a byte buffer pixel by pixel.
	 * @param filename the name of the icon image file.
	 * @param size the width (and height) of the image in pixels.
	 * @return a byte buffer containing the image data.
	 * @throws IOException
	 */
	private static ByteBuffer loadIcon(String filename, int size) throws IOException {
		URL url = filesystem.getURL(filename);
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
	
	private static void refreshWindowSize() {
		width = Display.getWidth();
		height = Display.getHeight();
		if (graphics.isInitialised()) {
			graphics.initialise();
		}
	}
	
	/**
	 * Updates the window and syncs it with the specified FPS. 
	 * It also updates whether the window has been closed or not.
	 */
	public static void update() {
		closed = closed || Display.isCloseRequested();
		if (closed) return;
		if (Display.wasResized()) {
			refreshWindowSize();
		}
		Display.update();
		Display.sync(targetFPS);
	}
	
	/**
	 * Gets the change of time in seconds (delta time) since the last time this method was called.
	 * @return that delta time.
	 */
	public static double getDeltaTime() {
		double time = (double)(Sys.getTime()) / Sys.getTimerResolution();
		double delta = (time - lastFrameTime);
	    lastFrameTime = time;
	    return delta;
	}
	
	/**
	 * Disposes of the resources used by the window.
	 */
	public static void dispose() {
		Display.destroy();
	}

}