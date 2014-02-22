package jog;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

import jog.graphics.Colour;

public class image {
	
	public enum WrapMode {
		CLAMP(GL_CLAMP),
		REPEAT(GL_REPEAT);
		
		protected final int glWrapMode;
		WrapMode(int glWrapMode) { this.glWrapMode = glWrapMode; }
	}
	
	public enum FilterMode {
		LINEAR(GL_LINEAR),
		NEAREST(GL_NEAREST);
		
		protected final int glFilterMode;
		FilterMode(int glFilterMode) { this.glFilterMode = glFilterMode; }
	}
	
	public static Image newImage(String filename) {
		return new Image(filesystem.getPath(filename));
	}
	
	public static class Image {
		
		public final int id;
		public final int width;
		public final int height;
		public final ByteBuffer bytes;
		
		private FilterMode currentFilterModeMin;
		private FilterMode currentFilterModeMag;
		private WrapMode currentWrapModeMin;
		private WrapMode currentWrapModeMag;
		
		private Image(String filepath) {
			InputStream in = null;
			try {
				in = new FileInputStream(filepath);
				PNGDecoder decoder = new PNGDecoder(in);
				width = decoder.getWidth();
				height = decoder.getHeight();
				System.out.println("Loaded image: \"" + filepath + "\" with the dimensions: (" + width + ", " + height + ").");
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
		
		public void bind() {
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, bytes);
		}
		
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
		
		public FilterMode getFilterModeMin() { return currentFilterModeMin; }
		public FilterMode getFilterModeMag() { return currentFilterModeMag; }
		
		public void setFilterMode(FilterMode filterModeMin, FilterMode filterModeMag) {
			glBindTexture(GL_TEXTURE_2D, id);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filterModeMin.glFilterMode);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filterModeMag.glFilterMode);
			currentFilterModeMin = filterModeMin;
			currentFilterModeMag = filterModeMag;
		}
		
		public WrapMode getWrapModeMin() { return currentWrapModeMin; }
		public WrapMode getWrapModeMag() { return currentWrapModeMag; }
		
		public void setWrapMode(WrapMode wrapModeX, WrapMode wrapModeY) {
			glBindTexture(GL_TEXTURE_2D, id);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapModeX.glWrapMode);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapModeY.glWrapMode);
			currentWrapModeMin = wrapModeX;
			currentWrapModeMag = wrapModeY;
		}
		
	}
	
}
