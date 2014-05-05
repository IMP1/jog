package jog;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

import jog.graphics.Colour;

/**
 * <h1>jog.image</h1>
 * <p>Used for loading images, and handling the access of their data, as well as
 * binding them for OpenGL</p>
 * @author IMP1
 */
public abstract class image {
	
	/**
	 * The different wrap modes available.
	 * @author IMP1
	 */
	public enum WrapMode {
		CLAMP(GL_CLAMP),
		REPEAT(GL_REPEAT);
		
		protected final int glWrapMode;
		WrapMode(int glWrapMode) { this.glWrapMode = glWrapMode; }
	}
	
	/**
	 * The different filter modes available.
	 * @author IMP1
	 */
	public enum FilterMode {
		LINEAR(GL_LINEAR),
		NEAREST(GL_NEAREST);
		
		protected final int glFilterMode;
		FilterMode(int glFilterMode) { this.glFilterMode = glFilterMode; }
	}
	
	/**
	 * Loads and returns a new image, letting openGL know about its existence.
	 * @param filename the path to the image (can be relative, as uses jog.filesystem).
	 * @return
	 */
	public static Image newImage(String filename) {
		return new Image(filesystem.getPath(filename));
	}
	
	/**
	 * <h1>Image</h1>
	 * Image class that serves as an openGL texture.
	 * @author IMP1
	 */
	public static class Image {
		
		/** unique id for this image used by openGL */
		public final int id;
		/** width of image in pixels */
		public final int width;
		/** height of image in pixels */
		public final int height;
		/** image data */
		public final ByteBuffer bytes;
		
		private FilterMode currentFilterModeMin;
		private FilterMode currentFilterModeMag;
		private WrapMode currentWrapModeX;
		private WrapMode currentWrapModeY;
		
		/**
		 * Loads in an image and lets openGL know about it.
		 * @param filepath the path to the image.
		 */
		private Image(String filepath) {
			InputStream in = null;
			try {
				in = new FileInputStream(filepath);
				PNGDecoder decoder = new PNGDecoder(in);
				width = decoder.getWidth();
				height = decoder.getHeight();
				System.out.println("[jog.image] Loaded image: \"" + filepath + "\" with the dimensions: (" + width + ", " + height + ").");
				bytes = ByteBuffer.allocateDirect(4 * width * height);
				decoder.decode(bytes, width * 4, Format.RGBA);
				bytes.flip();
				in.close();
				glEnable(GL_TEXTURE_2D);
				id = glGenTextures();
				setFilterMode(FilterMode.NEAREST, FilterMode.NEAREST);
				setWrapMode(WrapMode.CLAMP, WrapMode.CLAMP);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Image \"" + filepath + "\" failed to load.");
			}
		}
		
		/**
		 * Binds the image for openGL use. 
		 */
		protected void bind() {
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, bytes);
		}
		
		/**
		 * Allows access to the colours making up the image.
		 * @param x the horizontal coordinate of the pixel.
		 * @param y the vertical coordinate of the pixel.
		 * @return the colour of the pixel.
		 */
		public graphics.Colour pixelAt(int x, int y) {
			if (x < 0 || x > width) {
				throw new IndexOutOfBoundsException("Invalid image pixel coordinates: x = " + x);
			}
			if (y < 0 || y > height) {
				throw new IndexOutOfBoundsException("Invalid image pixel coordinates: y = " + y);
			}
			int r, g, b, a;
			int i = (y * width + x) * 4;
			r = bytes.get(i);
			g = bytes.get(i+1);
			b = bytes.get(i+2);
			a = bytes.get(i+3);
			if (r < 0) r += 256;
			if (g < 0) g += 256;
			if (b < 0) b += 256;
			if (a < 0) a += 256;
			return new Colour(r, g, b, a);
		}
		
		/**
		 * Allows access to the {@link FilterMode} used when scaling down.
		 * @return the FilterMode.
		 */
		public FilterMode getFilterModeMin() { return currentFilterModeMin; }
		/**
		 * Allows access to the {@link FilterMode} used when scaling up.
		 * @return the FilterMode.
		 */
		public FilterMode getFilterModeMag() { return currentFilterModeMag; }
		
		/**
		 * Sets the {@link FilterMode} to be used when scaling the image down and up, respectively.
		 * @param filterModeMin
		 * @param filterModeMag
		 */
		public void setFilterMode(FilterMode filterModeMin, FilterMode filterModeMag) {
			glBindTexture(GL_TEXTURE_2D, id);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filterModeMin.glFilterMode);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filterModeMag.glFilterMode);
			currentFilterModeMin = filterModeMin;
			currentFilterModeMag = filterModeMag;
		}
		
		/**
		 * Allows access to the {@link WrapMode} used when overflowing horizontally.
		 * @return the WrapMode.
		 */
		public WrapMode getWrapModeX() { return currentWrapModeX; }
		/**
		 * Allows access to the {@link WrapMode} used when overflowing vertically.
		 * @return the WrapMode.
		 */
		public WrapMode getWrapModeY() { return currentWrapModeY; }
		
		/**
		 * Sets the {@link WrapMode} to be used when the image overflows horizontally and vertically, respectively.
		 * @param wrapModeX how to deal with overflow horizontally.
		 * @param wrapModeY how to deal with overflow vertically.
		 */
		public void setWrapMode(WrapMode wrapModeX, WrapMode wrapModeY) {
			glBindTexture(GL_TEXTURE_2D, id);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapModeX.glWrapMode);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapModeY.glWrapMode);
			currentWrapModeX = wrapModeX;
			currentWrapModeY = wrapModeY;
		}
		
	}
	
}
