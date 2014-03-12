package jog;

import static org.lwjgl.opengl.GL11.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.lwjgl.opengl.ARBFragmentShader.*;
import static org.lwjgl.opengl.ARBShaderObjects.*;
import static org.lwjgl.opengl.ARBVertexShader.*;

import jog.image.Image;

/**
 * <h1>jog.graphics</h1>
 * <p>Provides a layer upon OpenGL methods. jog.graphics allows drawing basic shapes to the screen,
 * as well as images and limited font capabilities. jog.graphics (unlike OpenGL) has the graphical origin to be the window's
 * upper-left corner.</p>
 * @author IMP1
 */
public abstract class graphics {
	
	/**
	 * The different blend modes available
	 * @author IMP1
	 */
	public enum BlendMode {
		ADDITIVE(GL_SRC_ALPHA, GL_ONE), 
		SUBTRACTIVE(GL_ZERO, GL_ONE_MINUS_SRC_COLOR), 
		MULTIPLICATIVE(GL_DST_COLOR, GL_ZERO), 
		ALPHA(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		protected final int arg1;
		protected final int arg2;
		BlendMode(int arg1, int arg2) { 
			this.arg1 = arg1;
			this.arg2 = arg2;
		}
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
		
		protected Colour(int red, int green, int blue, int alpha) {
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.alpha = alpha;
			r = (float)(Math.max(0, Math.min(255, red)) / 255);
			g = (float)(Math.max(0, Math.min(255, green)) / 255);
			b = (float)(Math.max(0, Math.min(255, blue)) / 255);
			a = (float)(Math.max(0, Math.min(255, alpha)) / 255);
		}
		
		protected Colour(int red, int green, int blue) {
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
	 * <h1>Quad</h1>
	 * <p>Represents a quad for drawing rectangular sections of images.</p>
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
	
	/**
	 * <h1>Shader</h1>
	 * <p>Represents GLSL shaders for altering the GL draw pipeline.</p>
	 * @author IMP1
	 */
	public static class Shader {
		
		/**
		 * The GL shader programme's ID.
		 */
		protected final int programmeID;
		
		private final int vertexShaderID;
		private final int fragmentShaderID;
		
		/**
		 * Creates a shader programme, assuming that only the filename without an extension is given, and that there exists
		 * both a .vert and a .frag for this filename.
		 * @param filepath the filepath without a file extension.
		 */
		private Shader(String filepath) {
			this(filepath.concat(".vert"), filepath.concat(".frag"));
		}
		
		/**
		 * Creates a shader programme from a file containing GLSL code for a vertex shader, and one for a fragment shader.
		 * @param vertexFilepath the filepath to the vertex shader.
		 * @param fragmentFilepath the filepath to the fragment shader.
		 */
		private Shader(String vertexFilepath, String fragmentFilepath) {
			// Get unique IDs that GL will recognise as this.
			programmeID = glCreateProgramObjectARB();
			vertexShaderID = glCreateShaderObjectARB(GL_VERTEX_SHADER_ARB);
			fragmentShaderID = glCreateShaderObjectARB(GL_FRAGMENT_SHADER_ARB);
			// Load vertex shader to a string.
			StringBuilder vertexSource = new StringBuilder();
			try {
				BufferedReader reader = new BufferedReader(new FileReader(filesystem.getPath(vertexFilepath)));
				for (String line = reader.readLine(); line != null; line = reader.readLine()) {
					vertexSource.append(line).append('\n');
				}
				reader.close();
			} catch(IOException e) {
				e.printStackTrace();
				System.err.println("Vertex shader source could not be read.");
				window.dispose();
				System.exit(1);
			}
			// Load fragment shader to a string.
			StringBuilder fragmentSource = new StringBuilder();
			try {
				BufferedReader reader = new BufferedReader(new FileReader(filesystem.getPath(fragmentFilepath)));
				for (String line = reader.readLine(); line != null; line = reader.readLine()) {
					fragmentSource.append(line).append('\n');
				}
				reader.close();
			} catch(IOException e) {
				e.printStackTrace();
				System.err.println("Fragment shader source could not be read.");
				window.dispose();
				System.exit(1);
			}
			// Link and compile the source to the vertex shader.
			glShaderSourceARB(vertexShaderID, vertexSource);
			glCompileShaderARB(vertexShaderID);
			if (glGetObjectParameteriARB(vertexShaderID, GL_OBJECT_COMPILE_STATUS_ARB) == GL_FALSE) {
				System.err.println("Error creating vertex shader");
			}
			// Link and compile the source to the fragment shader.
			glShaderSourceARB(fragmentShaderID, fragmentSource);
			glCompileShaderARB(fragmentShaderID);
			if (glGetObjectParameteriARB(fragmentShaderID, GL_OBJECT_COMPILE_STATUS_ARB) == GL_FALSE) {
				System.err.println("Error creating fragment shader");
			}
			// Attach the vertex and fragment shaders to this shader programme.
			glAttachObjectARB(programmeID, vertexShaderID);
			glAttachObjectARB(programmeID, fragmentShaderID);
			// Link the shader programme.
			glLinkProgramARB(programmeID);
			if (glGetObjectParameteriARB(programmeID, GL_OBJECT_LINK_STATUS_ARB) == GL_FALSE) {
	            System.err.println("Error with the link.");
	            String errorLog = glGetInfoLogARB(programmeID, glGetObjectParameteriARB(programmeID, GL_OBJECT_INFO_LOG_LENGTH_ARB));
	            System.err.println(errorLog);
	            return;
	        }
			// Validate the shader programme.
			glValidateProgramARB(programmeID);
	        glValidateProgramARB(programmeID);
	        if (glGetObjectParameteriARB(programmeID, GL_OBJECT_VALIDATE_STATUS_ARB) == GL_FALSE) {
	        	System.err.println("Error with the validation.");
	        	String errorLog = glGetInfoLogARB(programmeID, glGetObjectParameteriARB(programmeID, GL_OBJECT_INFO_LOG_LENGTH_ARB));
	            System.err.println(errorLog);
	        	return;
	        }
		}
		
		/**
		 * Sets the shader to affect following draw processes.
		 */
		private void set() {
			glUseProgramObjectARB(programmeID);
		}
		
		/**
		 * Unsets the shader, removing its affect on following draw processes.
		 */
		private void unset() {
			glUseProgramObjectARB(0);
		}
		
		public void setVariable(String variableName, int value) {
			int variableLocation = glGetUniformLocationARB(programmeID, variableName);
			glUniform1iARB(variableLocation, value);
		}
		
		public void setVariable(String variableName, float value) {
			set();
			int variableLocation = glGetUniformLocationARB(programmeID, variableName);
			glUniform1fARB(variableLocation, value);
			unset();
		}
		
		public void dispose() {
			unset();
			glDetachObjectARB(programmeID, vertexShaderID);
			glDetachObjectARB(programmeID, fragmentShaderID);
			glDeleteObjectARB(vertexShaderID);
			glDeleteObjectARB(fragmentShaderID);
			glDeleteObjectARB(programmeID);
		}
		
	}
	
	public static class Canvas {
		
		/**
		 * The GL shader programme's ID.
		 */
		protected final int id;
		
		private Canvas(int id) {
			this.id = id;
		}
		
		/**
		 * Sets this canvas to be drawn to, rather than the window.
		 */
		private void set() {
			
		}
		
		/**
		 * Unsets this canvas, reverting to the window to draw things to.
		 */
		private void unset() {
			
		}
		
		/**
		 * Sets the canvas for drawing.
		 * @see image.Image#bind()
		 */
		protected void bind() {
			
		}
		
	}
	
	private static Canvas currentCanvas = null;
	private static Shader currentShader = null;
	private static BlendMode currentBlendMode = null;
	private static font.Font currentFont = null;
	private static Colour currentColour = null;
	private static Colour backgroundColour = null;
	
	private static boolean initialised = false;
	
	/**
	 * Intialises OpenGL with the appropriate matrix modes and orthographic dimensions. 
	 */
	public static void initialise() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, window.width(), window.height(), 0, -1, 1);
		glViewport(0, 0, window.width(), window.height());
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_BLEND);
		if (currentBlendMode == null) setBlendMode();
		if (backgroundColour == null) setBackgroundColour(0, 0, 0);
		if (currentColour == null) setColour(255, 255, 255);
		initialised = true;
	}
	
	public static boolean isInitialised() {
		return initialised;
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
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
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
	public static void draw(Image drawable, double x, double y, double r, double ox, double oy, double sx, double sy) {
		double w = drawable.width;
		double h = drawable.height;
		
    	glEnable(GL_TEXTURE_2D);
    	drawable.bind();
		push();
	    translate(x, y);
	    translate(ox, oy);
	    rotate(r);
	    translate(-ox, -oy);
	    scale(sx, sy);
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
	 * @param x the horizontal pixel to draw at.
	 * @param y the vertical pixel to draw at.
	 */
	public static void draw(Image drawable, double x, double y) {
		double w = drawable.width;
		double h = drawable.height;
		
		glEnable(GL_TEXTURE_2D);
    	drawable.bind();
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
		drawable.bind();
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
	 * Creates and returns a new Shader.
	 * @param vertFilepath the filepath to the vertex shader to use.
	 * @param fragmentFilepath the filepath to the fragment shader to use.
	 * @return the created shader.
	 */
	public static Shader newShader(String vertFilepath, String fragmentFilepath) {
		return new Shader(vertFilepath, fragmentFilepath);
	}
	
	/**
	 * Accesses the current shader affecting draw functions.
	 * @return the current shader.
	 */
	public static Shader getShader() {
		return currentShader;
	}
	
	/**
	 * Sets the shader to affect all things drawn until the shader is changed again.
	 * @param shader
	 */
	public static void setShader(Shader shader) {
		shader.set();
		currentShader = shader;
	}
	
	/**
	 * Removes the current shader affecting
	 */
	public static void setShader() {
		if (currentShader != null) {
			currentShader.unset();
		}
		currentShader = null;
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
		glBlendFunc(mode.arg1, mode.arg2);
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
	public static void setFont(font.Font font) {
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
	 * rotates all drawn things by the angle.
	 * @param angle the angle, in radians, to rotates by.
	 */
	public static void rotate(double angle) {
		angle = Math.toDegrees(angle);
		glRotated(angle, 0, 0, 1);
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