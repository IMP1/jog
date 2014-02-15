package jog;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import de.matthiasmann.twl.utils.PNGDecoder;

import jog.graphics.Colour;

public class image {
	
	/**
	 * <h1>jog.image.Image</h1>
	 * <p>Essentially an object-orientated wrapper for the slick Texture.</p>
	 * @author IMP1
	 * @see Texture
	 */
	public static class Image {
		
		private Texture texture;
		
		/**
		 * Constructor for an image.
		 * @param filepath the path to the image file.
		 */
		private Image(String filepath) {
			try {
				String format = filepath.split("\\.")[1].toUpperCase();
				InputStream in = ResourceLoader.getResourceAsStream(filepath);
				texture = TextureLoader.getTexture(format, in);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void bind() {
			texture.bind();
		}
		
		/**
		 * Allows access of the dimensions of the image.
		 * @return the width of the image in pixels.
		 */
		public double width() { 
			return texture.getTextureWidth();
		}
		
		/**
		 * Allows access of the dimensions of the image.
		 * @return the height of the image in pixels.
		 */
		public double height() { 
			return texture.getTextureHeight(); 
		}
		
		/**
		 * Allows access to the colours of the pixels in the image data of the texture.
		 * @param x the x coordinate on the image of the pixel.
		 * @param y the y coordinate on the image of the pixel.
		 * @return the colour at the specified pixel.
		 */
		public Colour pixelAt(int x, int y) {
			int r = texture.getTextureData()[y * (int)width() + x ] * -255;
			int g = texture.getTextureData()[y * (int)width() + x + 1] * -255;
			int b = texture.getTextureData()[y * (int)width() + x + 2] * -255;
			int a = texture.getTextureData()[y * (int)width() + x + 3] * -255;
			return new Colour(r, g, b, a);
		}
		
	}

	public static Image newImage(String filename) {
		return new Image(filename);
	}
	
}
