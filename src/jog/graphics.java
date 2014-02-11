package jog;

import java.io.IOException;
import java.io.InputStream;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

/**
 * <h1>jog.graphics</h1>
 * <p>Provides a layer upon OpenGL methods. jog.graphics allows drawing basic shapes to the screen,
 * as well as images and limited font capabilities. jog.graphics (unlike OpenGL) has the graphical origin to be the window's
 * upper-left corner.</p>
 * @author IMP1
 */
public abstract class graphics {
	
	public enum BlendMode {
		ADDITIVE, SUBTRACTIVE, MULTIPLICATIVE, ALPHA,
	}
	
	/**
	 * <h1>Colour</h1>
	 * <p>Class representing a colour and it's 4 components: reg, green, blue and alpha.</p>
	 * @author IMP1
	 */
	public static class Colour {
		
		final public float r;
		final public float g;
		final public float b;
		final public float a;
		
		final public int red;
		final public int green;
		final public int blue;
		final public int alpha;
		
		private Colour(int red, int green, int blue, int alpha) {
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.alpha = alpha;
			r = (float)(Math.max(0, Math.min(255, red)) / 255);
			g = (float)(Math.max(0, Math.min(255, green)) / 255);
			b = (float)(Math.max(0, Math.min(255, blue)) / 255);
			a = (float)(Math.max(0, Math.min(255, alpha)) / 255);
		}
		
		private Colour(int red, int green, int blue) {
			this(red, green, blue, 255);
		}
		 
		@Override
		public String toString() {
			return "Colour: (" + red + ", " + green + ", " + blue + ", " + alpha + ")";
		}
		
	}
	
	/**
	 * <h1>Color</h1>
	 * <p>For that one country which spells it without the u, because I'm nice and inclusive like that.</p>
	 * @author IMP1
	 * @see Colour
	 */
	public static class Color extends Colour {

		private Color(int red, int green, int blue, int alpha) {
			super(red, green, blue, alpha);
		}
		private Color(int red, int green, int blue) {
			super(red, green, blue);
		}
		
	}
	
	/**
	 * <h1>jog.graphics.Font</h1>
	 * <p>Abstract font class with print methods.</p>
	 * @author IMP1
	 */
	public static abstract class Font {
		
		protected abstract void print(double x, double y, String text, double size);

		protected abstract void printCentred(double x, double y, double width, String text, double size);
		
	}
	
	/**
	 * <h1>jog.graphics.BitmapFont</h1>
	 * <p>A font generated from an image. Each glyph is as wide as the entire image as high.</p>
	 * @author IMP1
	 */
	private static class BitmapFont extends Font {
		
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
		private BitmapFont(String filepath, String chars) {
			image = newImage(filepath);
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
			double w = image.height();
			double h = image.height();
			double qw = w / image.width();
			double qh = 1;
			
	    	glEnable(GL_TEXTURE_2D);
	    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	    	image.texture.bind();
			glPushMatrix();
			glTranslated(x, y, 0);
			glScaled(size, size, 1);
			glBegin(GL_QUADS);
			for (int i = 0; i < text.length(); i ++) {
				double qx = glyphs.indexOf(text.charAt(i)) * w / image.width();
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
			double w = image.height();
			double h = image.height();
			double qw = w / image.width();
			double qh = 1;
			x += (width - (w * text.length() * size)) / 2;
			
			glEnable(GL_TEXTURE_2D);
	    	image.texture.bind();
			glPushMatrix();
			glTranslated(x, y, 0);
			glScaled(size, size, 1);
			glBegin(GL_QUADS);
			for (int i = 0; i < text.length(); i ++) {
				double qx = glyphs.indexOf(text.charAt(i)) * w / image.width();
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

	}
	
	/**
	 * <h1>jog.graphics.SystemFont</h1>
	 * <p>A font that the exists within the default font folder in the OS. Essentially SystemFont is a wrapper for TrueTypeFont.</p>
	 * @author IMP1
	 * @see TrueTypeFont
	 */
	private static class SystemFont extends Font {

		private TrueTypeFont font;
		
		/**
		 * Constructor for a system font.
		 * @param name name of the system font.
		 * @param size the size of the created font.
		 */
		private SystemFont(String name, int size) {
			java.awt.Font awtFont = new java.awt.Font(name, java.awt.Font.PLAIN, size);
			font = new TrueTypeFont(awtFont, false);
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
			font.drawString((int)x, (int)y, text);
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
		public void printCentred(double x, double y, double width, String text, double size) {
			x += (width - font.getWidth(text)) / 2;
			font.drawString((int)x, (int)y, text);
		}
		
	}
	
	/**
	 * <h1>jog.graphics.Image</h1>
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
		public Color pixelAt(int x, int y) {
			int r = texture.getTextureData()[y * (int)width() + x ] * -255;
			int g = texture.getTextureData()[y * (int)width() + x + 1] * -255;
			int b = texture.getTextureData()[y * (int)width() + x + 2] * -255;
			int a = texture.getTextureData()[y * (int)width() + x + 3] * -255;
			return new Color(r, g, b, a);
		}
		
	}
	
	/**
	 * Represents a quad for drawing rectangular sections of images.
	 * @author IMP1
	 */
	public static class Quad {
		
		public final double x, y, width, height, quadWidth, quadHeight;
		
		/**
		 * Constructor for Quad.
		 * @param x the beginning horizontal coordinate of the quad in pixels.
		 * @param y the beginning vertical coordinate of the quad in pixels.
		 * @param w the width in pixels of the quad.
		 * @param h the height in pixels of the quad.
		 * @param imgWidth the width of the image the quad will be a part of.
		 * @param imgHeight the height of the image the quad will be a part of.
		 */
		private Quad(double x, double y, double w, double h, double imgWidth, double imgHeight) {
			this.x = x / imgWidth;
			this.y = y / imgHeight;
			this.width = w / imgWidth;
			this.height = h / imgHeight;
			this.quadWidth = w;
			this.quadHeight = h;
		}		
		
	}
	
	private static BlendMode currentBlendMode;
	private static Font currentFont;
	private static Colour currentColour;
	private static Colour backgroundColour;
	
	/**
	 * Intialises OpenGL with the appropriate matrix modes and orthographic dimensions. 
	 */
	public static void initialise() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, window.width(), window.height(), 0, -1, 1);
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_BLEND);
		setBlendMode();
		setBackgroundColour(0, 0, 0);
		setColour(255, 255, 255);
	}

	/**
	 * Clears the screen ready for another draw process.
	 */
	public static void clear() {
//		try {
//			org.lwjgl.opengl.Display.swapBuffers();
//		} catch (org.lwjgl.LWJGLException e) {
//			e.printStackTrace();
//		}
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	/**
	 * Draws an arc. That is, a portion of a circle. A curve.
	 * @param fill whether to fill with colour (false just draws a curved line).
	 * @param x the x coordinate of where the centre of circle would be if an arc of 2pi radians were drawn.
	 * @param y the y coordinate of where the centre of circle would be if an arc of 2pi radians were drawn.
	 * @param r the radius of the circle.
	 * @param startAngle the angle the arc begins at.
	 * @param angle the angle of the arc.
	 * @param segments how many lines segments to draw to approximate the curve. 
	 */
	public static void arc(boolean fill, double x, double y, double r, double startAngle, double angle, double segments) {
		startAngle = -startAngle;
		angle = -angle;
		
		push();
		translate(x, y);
		glScaled(r, r, 1);
		if (fill) {
			glBegin(GL_TRIANGLE_FAN);
			glVertex2d(0, 0);
	    } else {
	    	glBegin(GL_LINE_STRIP);
	    }
		for (int i = 0; i <= segments; i++) {
		    double theta = startAngle + (angle * i / segments);
		    glVertex2d(Math.cos(theta), Math.sin(theta));
		}
		glEnd();
		pop();
	}
	public static void arc(boolean fill, double x, double y, double r, double startAngle, double angle) {
		arc(fill, x, y, r, startAngle, angle, 20);
	}
	
	/**
	 * Draws a circle.
	 * @param fill whether to fill with colour.
	 * @param x the x coordinate of the centre of the circle.
	 * @param y the y coordinate of the centre of the circle.
	 * @param r the radius of the circle.
	 * @param segments how many lines segments to draw to approximate the curve.
	 */
	public static void circle(boolean fill, double x, double y, double r, double segments) {
		push();
		translate(x, y);
		glScaled(r, r, 1);
		if (fill) {
			glBegin(GL_TRIANGLE_FAN);
			glVertex2d(0, 0);
	    } else {
	    	glBegin(GL_LINE_STRIP);
	    }
		for (int i = 0; i <= segments; i++) {
		    double angle = Math.PI * 2 * i / segments;
		    glVertex2d(Math.cos(angle), Math.sin(angle));
		}
		glEnd();
		pop();
	}
	public static void circle(boolean fill, double x, double y, double r) {
		circle(fill, x, y, r, 20);
	}

	/**
	 * Draws the texture image at the specified coordinates.
	 * @param drawable the image to be drawn.
	 * @param x the horizontal pixel to draw at.
	 * @param y the vertical pixel to draw at.
	 * @param r the angle in radians to draw the image at.
	 * @param ox the x coordinate of the origin of the image around which it is rotated.
	 * @param oy the y coordinate of the origin of the image around which it is rotated.
	 */
	public static void draw(Image drawable, double x, double y, double r, double ox, double oy) {
		r = -Math.toDegrees(r);
		
    	glEnable(GL_TEXTURE_2D);
    	drawable.texture.bind();
		push();
	    translate(x, y);
	    glRotated(r, 0, 0, 1);
	    glScaled(2, 2, 1);
		glBegin(GL_QUADS);
			glTexCoord2d(0, 0);
			glVertex2d(-ox/2, -oy/2);
			glTexCoord2d(1, 0);
			glVertex2d(ox/2, -oy/2);
			glTexCoord2d(1, 1);
			glVertex2d(ox/2, oy/2);
			glTexCoord2d(0, 1);
			glVertex2d(-ox/2, oy/2);
		glEnd();
		pop();
		glDisable(GL_TEXTURE_2D);
	}
	
	/**
	 * Draws the texture image at the specified coordinates.
	 * @param drawable the image to be drawn.
	 * @param x the horizontal pixel to draw at.
	 * @param y the vertical pixel to draw at.
	 */
	public static void draw(Image drawable, double x, double y) {
		double w = drawable.width();
		double h = drawable.height();
		
		glEnable(GL_TEXTURE_2D);
    	drawable.texture.bind();
		push();
	    translate(x, y);
		glBegin(GL_QUADS);
			glTexCoord2d(0, 0);
			glVertex2d(0, 0);
			glTexCoord2d(1, 0);
			glVertex2d(w, 0);
			glTexCoord2d(1, 1);
			glVertex2d(w, h);
			glTexCoord2d(0, 1);
			glVertex2d(0, h);
		glEnd();
		pop();
		glDisable(GL_TEXTURE_2D);
	}
	
	/**
	 * Draws the texture image at the specified coordinates.
	 * @param drawable the image to be drawn.
	 * @param quad the quad of the image to be drawn.
	 * @param x the horizontal pixel to draw at.
	 * @param y the vertical pixel to draw at.
	 */
	public static void drawq(Image drawable, Quad quad, double x, double y) {
		double w = quad.quadWidth;
		double h = quad.quadHeight;
		
    	glEnable(GL_TEXTURE_2D);
		drawable.texture.bind();
		push();
		translate(x, y);
		glBegin(GL_QUADS);
			glTexCoord2d(quad.x, quad.y);
			glVertex2d(0, 0);
			glTexCoord2d(quad.x + quad.width, quad.y);
			glVertex2d(w, 0);
			glTexCoord2d(quad.x + quad.width, quad.y + quad.height);
			glVertex2d(w, h);
			glTexCoord2d(quad.x, quad.y + quad.height);
			glVertex2d(0, h);
		glEnd();
		pop();
		glDisable(GL_TEXTURE_2D);
	}

	/**
	 * Draws a line from one point to another.
	 * @param x1 the x coordinate of the first point.
	 * @param y1 the y coordinate of the first point.
	 * @param x2 the x coordinate of the second point.
	 * @param y2 the y coordinate of the second point.
	 */
	public static void line(double x1, double y1, double x2, double y2) {
		glBegin(GL_LINE_STRIP);
		glVertex2d(x1, y1);
		glVertex2d(x2, y2);
		glEnd();
	}
	
	/**
	 * Draws a polygon
	 * @param fill whether to fill with colour (false just draws the lines).
	 * @param points the points of the vertices with x and y coordinates alternating.
	 */
	public static void polygon(boolean fill, double... points) {
		if (fill) {
			glBegin(GL_TRIANGLE_FAN);
	    } else {
	    	glBegin(GL_LINE_STRIP);
	    }
		for (int i = 0; i < points.length; i += 2) {
		    glVertex2d(points[i], points[i+1]);
		}
		if (!fill) {
			glVertex2d(points[0], points[1]);
		}
		glEnd();
	}

	/**
	 * Draws text to the screen using the current font. If no font has yet been made, it creates a default.
	 * @param text the characters to be drawn.
	 * @param x the x coordinate to draw the text at.
	 * @param y the y coordinate to draw the text at.
	 * @param size the size to draw the text at.
	 */
	public static void print(String text, double x, double y, double size) {
		if (currentFont == null) currentFont = newSystemFont("Times New Roman");
		currentFont.print(x, y, text, size);
	}
	public static void print(String text, double x, double y){ print(text, x, y, 1); }
	
	/**
	 * Draws text to the screen using the current font. If no font has yet been made, it creates a default.
	 * @param text the characters to be drawn.
	 * @param x the x coordinate to draw the text at.
	 * @param y the y coordinate to draw the text at.
	 * @param size the size to draw the text at.
	 * @param width the width the text is centred around.
	 */
	public static void printCentred(String text, double x, double y, double size, double width) {
		if (currentFont == null) currentFont = newSystemFont("Times New Roman");
		currentFont.printCentred(x, y, width, text, size);
	}

	/**
	 * Draws a rectangle.
	 * @param fill whether to fill with colour (false just draws the lines).
	 * @param x the x coordinate of the rectangle.
	 * @param y the y coordinate of the rectangle.
	 * @param width the width of the rectangle.
	 * @param height the height of the rectangle.
	 * @see #polygon(boolean, double...)
	 */
	public static void rectangle(boolean fill, double x, double y, double width, double height) {
	    polygon(fill, x, y, x + width, y, x + width, y + height, x, y + height);
	}
	
	/**
	 * Draws a triangle.
	 * @param fill whether to fill with colour (false just draws the lines).
	 * @param x1 the x coordinate of the first point of the triangle.
	 * @param y1 the y coordinate of the first point of the triangle.
	 * @param x2 the x coordinate of the second point of the triangle.
	 * @param y2 the y coordinate of the second point of the triangle.
	 * @param x3 the x coordinate of the third point of the triangle.
	 * @param y3 the y coordinate of the third point of the triangle.
	 * @see #polygon(boolean, double...)
	 */
	public static void triangle(boolean fill, double x1, double y1, double x2, double y2, double x3, double y3) {
		polygon(fill, x1, y1, x2, y2, x3, y3);
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
	
	/**
	 * Creates and returns a new SystemFont.
	 * @param fontName the name of the font.
	 * @param size the size of the font to be created.
	 * @return the created font.
	 */
	public static SystemFont newSystemFont(String fontName, int size) {
		return new SystemFont(fontName, size);
	}
	public static SystemFont newSystemFont(String fontName) { return newSystemFont(fontName, 24); }
		
	/**
	 * Creates and returns a new Image.
	 * @param filepath
	 * @return the created image.
	 */
	public static Image newImage(String filepath) {
		return new Image(filepath);
	}
	
	/**
	 * Creates and returns a new Quad.
	 * @param x the beginning horizontal coordinate of the quad in pixels.
	 * @param y the beginning vertical coordinate of the quad in pixels.
	 * @param quadWidth the width in pixels of the quad.
	 * @param quadHeight the height in pixels of the quad.
	 * @param imageWidth the width of the image the quad will be a part of.
	 * @param imageHeight the height of the image the quad will be a part of.
	 * @return the created quad.
	 */
	public static Quad newQuad(double x, double y, double quadWidth, double quadHeight, double imageWidth, double imageHeight) {
		return new Quad(x, y, quadWidth, quadHeight, imageWidth, imageHeight);
	}
	
	/**
	 * Accesses the current blend mode affecting draw functions.
	 * @return the current blend mode.
	 */
	public static BlendMode getBlendMode() {
		return currentBlendMode;
	}
	
	/**
	 * Sets the blend mode of all things drawn until either the blend mode is changed again.
	 * @param mode the blend mode to draw things.
	 */
	public static void setBlendMode(BlendMode mode) {
		currentBlendMode = mode;
		if (mode == BlendMode.ADDITIVE) {
			glBlendFunc(GL_SRC_ALPHA, GL_ONE);
		} else if (mode == BlendMode.SUBTRACTIVE) {
			glBlendFunc(GL_ZERO, GL_ONE_MINUS_SRC_COLOR); 
		} else if (mode == BlendMode.MULTIPLICATIVE) {
			glBlendFunc(GL_DST_COLOR, GL_ZERO);
		} else if (mode == BlendMode.ALPHA) {
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		}
	}
	
	/**
	 * Reverts the blend mode to the default.
	 */
	public static void setBlendMode() {
		setBlendMode(BlendMode.ALPHA);
	}
	
	/**
	 * Accesses the colour things are currently being drawn.
	 * @return the current colour.
	 */
	public static Colour getColour() {
		return currentColour;
	}
	
	/**
	 * Sets the colour of all things drawn until either the colour is changed again.
	 * @param colour the colour to draw things.
	 */
	public static void setColour(Colour colour) {
		setColour(colour.red, colour.green, colour.blue, colour.alpha);
	}

	/**
	 * Sets the colour of all things drawn until either the colour is changed again
	 * or the end of the draw phase is reached. 
	 * @param r the red component of the colour to draw things.
	 * @param g the green component of the colour to draw things.
	 * @param b the blue component of the colour to draw things.
	 * @param a the alpha component of the colour to draw things.
	 */
	public static void setColour(double r, double g, double b, double a) {
		currentColour = new Colour((int)r, (int)g, (int)b, (int)a);
		double red   = Math.max(0, Math.min(255, r)) / 255;
		double green = Math.max(0, Math.min(255, g)) / 255;
		double blue  = Math.max(0, Math.min(255, b)) / 255;
		double alpha = Math.max(0, Math.min(255, a)) / 255;
		glColor4d(red, green, blue, alpha);
	}
	public static void setColour(int r, int g, int b) { setColour(r, g, b, 255); }

	/**
	 * Accesses the current colour the window clears to.
	 * @return the background colour.
	 */
	public static Colour getBackgroundColour() {
		return backgroundColour;
	}
	
	/**
	 * Sets the colour that the window clears to, and that the window will be at 
	 * the beginning of a draw phase.
	 * @param r the red component of the colour.
	 * @param g the green component of the colour.
	 * @param b the blue component of the colour.
	 * @param a the alpha component of the colour.
	 */
	public static void setBackgroundColour(double r, double g, double b, double a) {
		backgroundColour = new Colour((int)r, (int)g, (int)b, (int)a);
		float red   = (float)(Math.max(0, Math.min(255, r)) / 255);
		float green = (float)(Math.max(0, Math.min(255, g)) / 255);
		float blue  = (float)(Math.max(0, Math.min(255, b)) / 255);
		float alpha = (float)(Math.max(0, Math.min(255, a)) / 255);
		glClearColor(red, green, blue, alpha);
	}
	public static void setBackgroundColour(double r, double g, double b) { setBackgroundColour(r, g, b, 255); }
	
	/**
	 * Sets the font to print text with.
	 * @param font the new font to be active.
	 */
	public static void setFont(Font font) {
		currentFont = font;
	}

	/**
	 * Pushes the current state of the transformations and scaling onto a stack.
	 */
	public static void push() {
		glPushMatrix();
	}
	
	/**
	 * Pops the top off the stack, reverting to the state before the latest push.
	 * @see #push()
	 */
	public static void pop() {
		glPopMatrix();
	}
	
	/**
	 * translates all drawn things by the horizontal and vertical amounts.
	 * @param x the horizontal amount to translate by.
	 * @param y the vertical amount to translate by.
	 */
	public static void translate(double x, double y) {
		glTranslated(x, y, 0);
	}
	
	/**
	 * scales all drawn things by the horizontal and vertical amounts.
	 * @param scaleX the horizontal amount to scale by.
	 * @param scaleY the vertical amount to scale by.
	 */
	public static void scale(double scaleX, double scaleY) {
		glScaled(scaleX, scaleY, 1);
	}

}