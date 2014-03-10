import java.io.File;

import jog.input;
import jog.window;

import org.lwjgl.Sys;

public class Main implements jog.input.InputEventHandler, jog.network.NetworkEventHandler {
	
	public static void main(String[] args) {
		new Main();
	}
	
	final private String TITLE = "JOG";
	final private int WIDTH = 640;
	final private int HEIGHT = 480;
	
	private double lastFrameTime;
	private double dt;
	
	private jog.image.Image img;
	private jog.graphics.Quad corner;
	private jog.font.Font font;
	private jog.graphics.Shader[] shaders;
	private double timer;
	private double rotation;
	private int shaderToDraw;
	
	private String multiplayerRole = "";
	private jog.network.Server server = null;
	private jog.network.Client client = null;
	
	public Main() {
		start();
		getDeltaTime();
		while(!jog.window.isClosed()) {
			dt = getDeltaTime();
			update(dt);
			draw();
		}
		quit();
	}
	
	private void start() {
		jog.window.initialise(TITLE, WIDTH, HEIGHT);
		jog.graphics.initialise();
		jog.filesystem.addLocation("src" + File.separator + "gfx");
		jog.filesystem.addLocation("src" + File.separator + "sfx");
		img = jog.image.newImage("ship.png");
		System.out.println(img.pixelAt(35, 40));
		System.out.println(img.pixelAt(35, 41));
		corner = jog.graphics.newQuad(0, 0, 32, 32, img.width, img.height);
		font = jog.font.newBitmapFont("font.png", "ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz1234567890.,_-!?()[]><#~:;/\\^'\"{}£$@@@@@@@@");
		jog.graphics.setFont(font);
		jog.graphics.setBackgroundColour(0, 128, 128);
		String[] icons = {"icon16.png", "icon32.png", "icon64.png"}; 
		jog.window.setIcon(icons);
//		beep = audio.newSoundEffect("beep.ogg");
		rotation = 0;
		timer = 0;
		shaderToDraw = 0;
		jog.graphics.Shader[] s = {
			jog.graphics.newShader("test1.vert", "test1.frag"),
			jog.graphics.newShader("test1.vert", "test2.frag"),
			jog.graphics.newShader("test1.vert", "test3.frag"),
			jog.graphics.newShader("test1.vert", "test4.frag"),
		};
		shaders = s;
	}
	
	/**
	 * Calculates the time taken since the last tick in seconds as a double-precision floating point number.
	 * @return the time in seconds since the last frame.
	 */
	private double getDeltaTime() {
		double time = (double)(Sys.getTime()) / Sys.getTimerResolution();
	    double delta = (time - lastFrameTime);
	    lastFrameTime = time;
	    return delta;
	}
	
	private void update(double dt) {
		jog.audio.update();
		jog.input.update(this);
		jog.window.update();
		rotation += dt * Math.PI;
		timer += dt;
		for (jog.graphics.Shader s : shaders) s.setVariable("timeElapsed", (float)timer);
	}
	
	private void draw() {
		jog.graphics.clear();
		jog.graphics.setColour(255, 255, 255);
		//--------------------------------\\		
		jog.graphics.drawq(img, corner, 544, 416);
		
		int length = 32;
		jog.graphics.setColour(0, 0, 0);
		jog.graphics.circle(true, 128, 64 + Math.sin(Math.PI / 3) * length / 1.5, 64);
		drawCircles(0, length, jog.graphics.BlendMode.ADDITIVE);
		
		jog.graphics.setColour(255, 255, 255);
		jog.graphics.circle(true, 344, 64 + Math.sin(Math.PI / 3) * length / 1.5, 64);
		drawCircles(344 - 128, length, jog.graphics.BlendMode.SUBTRACTIVE);
		
		jog.graphics.setColour(255, 255, 255);
		jog.graphics.push();
		jog.graphics.translate(544, 192);
		jog.graphics.scale(2, 2);
		jog.graphics.rotate(Math.PI / 2);
		jog.graphics.draw(img, 0, 0);
		jog.graphics.pop();
		
		jog.graphics.draw(img, 460, 344, rotation, 35, 48, 1, 1);
		jog.graphics.draw(img, 352, 344, -rotation, 35, 48, 1, 1);
		
		int triangleX = 192;
		int triangleY = 256;
		length = 256;
		double radius;
		int direction;
		for (int i = 0; i < 5; i ++) {
			jog.graphics.setColour(255 / (i+1), 255 / (i+1), 255 / (i+1));
			direction = (i % 2) * 2 - 1;
			radius = length * (Math.sqrt(3) / 7.0);
			int x1 = triangleX;
			int x2 = triangleX - length / 2;
			int x3 = triangleX + length / 2;
			int y1 = (int) (triangleY - direction * (Math.sin(Math.PI / 3) * length - radius));
			int y2 = (int) (triangleY + direction * Math.sin(Math.PI / 6) * radius);
			int y3 = (int) (triangleY + direction * Math.sin(Math.PI / 6) * radius);
			jog.graphics.triangle(true, x1, y1, x2, y2, x3, y3);
			triangleY -= direction * radius * 6 / 8;
			length = (int) (radius * 2);
		}
		
		length = 64;
		jog.graphics.push();
		jog.graphics.translate(256, -224);
		jog.graphics.polygon(true, -length, 0, -length * Math.sin(Math.PI/6), -length * Math.cos(Math.PI/6), length * Math.sin(Math.PI/6), -length * Math.cos(Math.PI/6), 
								length, 0,  length * Math.sin(Math.PI/6), length * Math.cos(Math.PI/6), -length * Math.sin(Math.PI/6), length * Math.cos(Math.PI/6));
		jog.graphics.pop();
		
		jog.graphics.rectangle(false, 8, 32, 4, 128);
		
		jog.graphics.setColour(255, 255, 255);
		jog.graphics.print(multiplayerRole, 0, 12);
		
		jog.graphics.rectangle(true, 640, 480, 128, 64);
		jog.graphics.rectangle(true, 700, 32, 8, 256);
		
		//--------------------------------\\
		jog.graphics.setColour(255, 255, 255);
		int x = jog.input.mouseX();
		int y = jog.input.mouseY();
		jog.graphics.print("(" + x + ", " + y + ")", 0, 0);
		jog.graphics.setColour(0, 64, 128, 128);
		int r = 10;
		jog.graphics.line(x, 0, x, y-r);
		jog.graphics.line(0, y, x-r, y);
		jog.graphics.line(x, y+r, x, jog.window.height());
		jog.graphics.line(x+r, y, jog.window.width(), y);
		jog.graphics.circle(false, x, y, r);
		//--------------------------------\\		
		
		if (shaderToDraw > 0) {
			jog.graphics.setShader(shaders[shaderToDraw-1]);
			jog.graphics.rectangle(true, 0, 0, window.width(), window.height());
		}
		jog.graphics.setShader();
		
	}
	
	private void drawCircles(int x, int size, jog.graphics.BlendMode blendMode) {
		jog.graphics.setBlendMode(blendMode);
		jog.graphics.setColour(255, 0, 0);
		jog.graphics.circle(true, 128 + x, 64, 32);
		jog.graphics.setColour(0, 255, 0);
		jog.graphics.circle(true, 128 + x - Math.cos(Math.PI / 3) * size, 64 + Math.sin(Math.PI / 3) * size, 32);
		jog.graphics.setColour(0, 0, 255);
		jog.graphics.circle(true, 128 + x + Math.cos(Math.PI / 3) * size, 64 + Math.sin(Math.PI / 3) * size, 32);
		jog.graphics.setBlendMode();
	}
	
	private void quit() {
		jog.window.dispose();
		jog.audio.dispose();
	}

	@Override
	public void mousePressed(int key, int x, int y) {
		
	}

	@Override
	public void mouseReleased(int key, int x, int y) {
		
	}

	@Override
	public void keyPressed(int key) {
		if (key == jog.input.KEY_1) {
			System.out.println("Creating Server...");
			multiplayerRole = "Server";
		}
		if (key == jog.input.KEY_2) {
			System.out.println("Creating Client...");
			multiplayerRole = "Client";
		}
		if (key == jog.input.KEY_TAB) {
			
			if (jog.window.width() == 640) {
				jog.window.setSize(960, 640);
			} else {
				jog.window.setSize(640, 480);
			}
			
		}
		if (key == jog.input.KEY_SPACE) {
			shaderToDraw += 1;
			shaderToDraw %= (shaders.length + 1);
		}
	}

	@Override
	public void keyReleased(int key) {
		
	}

}
