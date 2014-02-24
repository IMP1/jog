package jog;

import static org.lwjgl.opengl.GL11.*;
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
		
		/**
		 * Constructor for a bitmap font.
		 * @param filepath the path to the image file.
		 * @param chars a String containing the characters in the same order that the image has them.
		 */
		protected BitmapFont(String filepath, String chars) {
			image = jog.image.newImage(filepath);
			glyphs = chars;
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
			double qw = w / image.width;
			double qh = 1;
			
	    	glEnable(GL_TEXTURE_2D);
	    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	    	image.bind();
			glPushMatrix();
			glTranslated(x, y, 0);
			glScaled(size, size, 1);
			glBegin(GL_QUADS);
			for (int i = 0; i < text.length(); i ++) {
				double qx = glyphs.indexOf(text.charAt(i)) * w / image.width;
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
			double qw = w / image.width;
			double qh = 1;
			x += (width - (w * text.length() * size)) / 2;
			
			glEnable(GL_TEXTURE_2D);
	    	image.bind();
			glPushMatrix();
			glTranslated(x, y, 0);
			glScaled(size, size, 1);
			glBegin(GL_QUADS);
			for (int i = 0; i < text.length(); i ++) {
				double qx = glyphs.indexOf(text.charAt(i)) * w / image.width;
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
			// TODO Auto-generated method stub
			return 0;
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
	
}
