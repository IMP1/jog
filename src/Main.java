import org.lwjgl.input.Mouse;

import jog.*;

public class Main implements jog.input.EventHandler {
	
	public static void main(String[] args) {
		new Main();
	}
	
	final private String TITLE = "JOG";
	final private int WIDTH = 640;
	final private int HEIGHT = 480;
	
	private graphics.Image img;
	private graphics.Quad corner;
	private graphics.Font font;
	private audio.Sound beep;
	
	public Main() {
		start();
		while(!window.isClosed()) {
			update();
			draw();
		}
		quit();
	}
	
	private void start() {
		window.initialise(TITLE, WIDTH, HEIGHT);
		graphics.initialise();
		img = graphics.newImage("ship.png");
		corner = graphics.newQuad(0, 0, 32, 32, img.width(), img.height());
		font = graphics.newBitmapFont("font.png", "0123456789().,- ");
		graphics.setFont(font);
		graphics.setBackgroundColour(0, 128, 128);
		String[] icons = {"icon16.png", "icon32.png", "icon64.png"}; 
		window.setIcon(icons);
//		beep = audio.newSoundEffect("beep.ogg");
	}
	
	private void update() {
		audio.update();
		input.update(this);
		window.update();
	}
	
	private void draw() {
		graphics.clear();
		graphics.setColour(255, 255, 255);
		//--------------------------------\\		
		graphics.drawq(img, corner, 544, 416);

		int length = 32;
		graphics.setColour(0, 0, 0);
		graphics.circle(true, 128, 64 + Math.sin(Math.PI / 3) * length / 1.5, 64);
		graphics.setBlendMode(graphics.BlendMode.ADDITIVE);
		graphics.setColour(255, 0, 0);
		graphics.circle(true, 128, 64, 32);
		graphics.setColour(0, 255, 0);
		graphics.circle(true, 128 - Math.cos(Math.PI / 3) * length, 64 + Math.sin(Math.PI / 3) * length, 32);
		graphics.setColour(0, 0, 255);
		graphics.circle(true, 128 + Math.cos(Math.PI / 3) * length, 64 + Math.sin(Math.PI / 3) * length, 32);
		graphics.setBlendMode();
		
		graphics.setColour(255, 255, 255);
		graphics.circle(true, 344, 64 + Math.sin(Math.PI / 3) * length / 1.5, 64);
		graphics.setBlendMode(graphics.BlendMode.SUBTRACTIVE);
		graphics.setColour(255, 0, 0);
		graphics.circle(true, 344, 64, 32);
		graphics.setColour(0, 255, 0);
		graphics.circle(true, 344 - Math.cos(Math.PI / 3) * length, 64 + Math.sin(Math.PI / 3) * length, 32);
		graphics.setColour(0, 0, 255);
		graphics.circle(true, 344 + Math.cos(Math.PI / 3) * length, 64 + Math.sin(Math.PI / 3) * length, 32);
		graphics.setBlendMode();
		
		graphics.setColour(255, 255, 255);
		
		graphics.draw(img, 460, 128);
		
		int triangleX = 192;
		int triangleY = 256;
		length = 256;
		double radius;
		int direction;
		for (int i = 0; i < 5; i ++) {
			graphics.setColour(255 / (i+1), 255 / (i+1), 255 / (i+1));
			direction = (i % 2) * 2 - 1;
			radius = length * (Math.sqrt(3) / 7.0);
			int x1 = triangleX;
			int x2 = triangleX - length / 2;
			int x3 = triangleX + length / 2;
			int y1 = (int) (triangleY - direction * (Math.sin(Math.PI / 3) * length - radius));
			int y2 = (int) (triangleY + direction * Math.sin(Math.PI / 6) * radius);
			int y3 = (int) (triangleY + direction * Math.sin(Math.PI / 6) * radius);
			graphics.triangle(true, x1, y1, x2, y2, x3, y3);
			triangleY -= direction * radius * 6 / 8;
			length = (int) (radius * 2);
		}
		
		length = 64;
		graphics.push();
		graphics.translate(256, -224);
		graphics.polygon(true, -length, 0, -length * Math.sin(Math.PI/6), -length * Math.cos(Math.PI/6), length * Math.sin(Math.PI/6), -length * Math.cos(Math.PI/6), 
								length, 0,  length * Math.sin(Math.PI/6), length * Math.cos(Math.PI/6), -length * Math.sin(Math.PI/6), length * Math.cos(Math.PI/6));
		graphics.pop();
		
		graphics.rectangle(false, 8, 32, 4, 128);
		
		//--------------------------------\\
		graphics.setColour(255, 255, 255);
		int x = Mouse.getX();
		int y = window.height() - Mouse.getY();
		graphics.print("(" + x + ", " + y + ")", 0, 0);
		graphics.setColour(0, 64, 128, 128);
		int r = 10;
		graphics.line(x, 0, x, y-r);
		graphics.line(0, y, x-r, y);
		graphics.line(x, y+r, x, window.height());
		graphics.line(x+r, y, window.width(), y);
		graphics.circle(false, x, y, r);
	}
	
	private void quit() {
		window.dispose();
		audio.dispose();
	}

	@Override
	public void mousePressed(int key, int x, int y) {
		
	}

	@Override
	public void mouseReleased(int key, int x, int y) {
		
	}

	@Override
	public void keyPressed(int key) {
		if (key == input.KEY_SPACE) beep.play();
	}

	@Override
	public void keyReleased(int key) {
		
	}

}
