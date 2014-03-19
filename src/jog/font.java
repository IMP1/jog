package jog;

import static org.lwjgl.opengl.GL11.*;

import java.util.HashMap;

import jog.image.Image;

/**
 * <h1>jog.font</h1>
 * <p>Handles loading and drawing fonts, including those generated
 * from bitmap images.</p>
 * @author IMP1
 */
public abstract class font {
	
	/**
	 * <h1>Font</h1>
	 * <p>Abstract font class that contains methods all different fonts must have.</p>
	 * @author IMP1
	 */
	public static abstract class Font {
		
		protected abstract void print(double x, double y, String text, double size);
		
		protected abstract void printCentred(double x, double y, double width, String text, double size);
		
		public abstract int getWidth(String text);
		
	}
	
	/**
	 * <h1>BitmapFont</h1>
	 * <p>A font generated from an image. Each glyph is as wide as the entire image as high.</p>
	 * @author IMP1
	 */
	protected static class BitmapFont extends Font {
		
		/**
		 * A string containing the characters in the same order that the image has them.
		 */
		private String glyphs;
		private Image image;
		private HashMap<Character, Integer> charWidths;
		
		/**
		 * Constructor for a bitmap font.
		 * @param filepath the path to the image file.
		 * @param chars a String containing the characters in the same order that the image has them.
		 */
		protected BitmapFont(String filepath, String chars) {
			image = jog.image.newImage(filepath);
			glyphs = chars;
			charWidths = new HashMap<Character, Integer>();
			for (char glyph : chars.toCharArray()) {
				charWidths.put(glyph, image.height);
			}
		}
		
		/**
		 * Constructor for a bitmap font.
		 * @param filepath the path to the image file.
		 * @param chars a String containing the characters in the same order that the image has them.
		 * @param widths an array containing the widths of the characters in the same order that the image has them.
		 */
		protected BitmapFont(String filepath, String chars, int[] widths) {
			image = jog.image.newImage(filepath);
			glyphs = chars;
			charWidths = new HashMap<Character, Integer>();
			for (int i = 0; i < chars.length(); i ++) {
				charWidths.put(chars.charAt(i), widths[i]);
			}
		}
		
		/**
		 * Prints the text to the display.
		 * @param x the x coordinate for the text to be drawn to.
		 * @param y the y coordinate for the text to be drawn to.
		 * @param text the text to be drawn.
		 * @param size the size of the drawn text.
		 */
		@Override
		protected void print(double x, double y, String text, double size) {
			double w = image.height;
			double h = image.height;
			
	    	glEnable(GL_TEXTURE_2D);
	    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	    	image.bind();
			glPushMatrix();
			glTranslated(x, y, 0);
			glScaled(size, size, 1);
			glBegin(GL_QUADS);
			
			double qx, qw;
			double qh = 1;
			for (int i = 0; i < text.length(); i ++) {
				qw = (charWidths.get(text.charAt(i))) / (double)image.width;
				qx = glyphs.indexOf(text.charAt(i)) * qw;
				glTexCoord2d(qx, 0);
				glVertex2d(w * i, 0);
				glTexCoord2d(qx + qw, 0);
				glVertex2d(w * (i+1), 0);
				glTexCoord2d(qx + qw, qh);
				glVertex2d(w * (i+1), h);
				glTexCoord2d(qx, qh);
				glVertex2d(w * i, h);
			}
			glEnd();
			glPopMatrix();
			glDisable(GL_TEXTURE_2D);
		}
		
		/**
		 * Prints the text to the display centred within specified boundaries.
		 * @param x the x coordinate for the text to be drawn to.
		 * @param y the y coordinate for the text to be drawn to.
		 * @param width the width the text should be centred around.
		 * @param text the text to be drawn.
		 * @param size the size of the drawn text.
		 */
		@Override
		protected void printCentred(double x, double y, double width, String text, double size) {
			double w = image.height;
			double h = image.height;
			x += (width - getWidth(text) * size) / 2;
			
			glEnable(GL_TEXTURE_2D);
	    	image.bind();
			glPushMatrix();
			glTranslated(x, y, 0);
			glScaled(size, size, 1);
			glBegin(GL_QUADS);
			
			double qw, qx;
			double qh = 1;
			for (int i = 0; i < text.length(); i ++) {
				qw = (charWidths.get(text.charAt(i))) / (double)image.width;
				qx = glyphs.indexOf(text.charAt(i)) * w / image.width;
				glTexCoord2d(qx, 0);
				glVertex2d(w * i, 0);
				glTexCoord2d(qx + qw, 0);
				glVertex2d(w * (i+1), 0);
				glTexCoord2d(qx + qw, qh);
				glVertex2d(w * (i+1), h);
				glTexCoord2d(qx, qh);
				glVertex2d(w * i, h);
			}
			glEnd();
			glPopMatrix();
			glDisable(GL_TEXTURE_2D);
		}

		@Override
		public int getWidth(String text) {
			int width = 0;
			for (char letter : text.toCharArray()) {
				width += charWidths.get(letter);
			}
			return width;
		}

	}

	/**
	 * Creates and returns a new BitmapFont.
	 * @param filepath the path to the image file.
	 * @param glyphs the String containing the characters the image represents.
	 * @return the created font.
	 */
	public static BitmapFont newBitmapFont(String filepath, String glyphs) {
		return new BitmapFont(filepath, glyphs);
	}
	
	public static BitmapFont newBitmapFont(String filepath, String glpyhs, int[] widths) {
		return new BitmapFont(filepath, glpyhs, widths);
	}
	
}
