package jog;

import java.io.FileInputStream;
import java.io.IOException;

import org.newdawn.easyogg.OggClip;

/**
 * <h1>jog.audio</h1>
 * <p>Provides loading and playing of various image filetypes.</p>
 * @author IMP1
 */
public abstract class audio {
	
	public static class Source {
		
		OggClip clip;
		
		private Source(String filename) {
			try {
				FileInputStream in = new FileInputStream(filename);
				clip = new OggClip(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void play() {
			clip.play();
		}
		
		public void pause() {
			clip.pause();
		}
		
		public void stop() {
			clip.stop();
		}
		
		public void resume() {
			clip.resume();
		}
		
	}
	
	public static void initialise() {
		
	}
	
	public static void update() {
		
	}
	
	public static void dispose() {
		
	}
	
	public static Source newSource(String filename) {
		return new Source(filesystem.getPath(filename));
	}
	
}
