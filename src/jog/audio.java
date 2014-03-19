package jog;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;

/**
 * <h1>jog.audio</h1>
 * <p>Provides loading and playing of various image filetypes.</p>
 * @author IMP1
 */
public abstract class audio {
	
	protected static FloatBuffer listenerPosition = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });
	protected static FloatBuffer listenerVelocity = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });
	protected static FloatBuffer listenerOrientation = BufferUtils.createFloatBuffer(6).put(new float[] { 0.0f, 0.0f, -1.0f,  0.0f, 1.0f, 0.0f });
	
	public static abstract class Source {
		
		protected FloatBuffer position;
		protected FloatBuffer velocity;
		
		public abstract void play();
		public abstract void stop();
		public abstract void pause();
		
	}
	
	public static void initialise() {
		
	}
	
	public static void update() {
		
	}
	
	public static void dispose() {
		
	}
	
}
