package com.jeremy.electro.audio;

import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;

import com.jeremy.electro.ClientMain;
import com.jeremy.electro.entity.Player;
import com.sineshore.serialization.Batch;

public class Sound {

	public static void playLocalSound(String name, float volume) {
		Thread thread = new Thread(() -> {
			try {
				playClip("/" + name + ".wav", volume);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, "sound");
		thread.setDaemon(true);
		thread.start();
	}

	public static void playLocalSound(String name) {
		playLocalSound(name, 1f);
	}

	public static void playPublicSound(float x, float y, String... names) {
		String name = names[new Random().nextInt(names.length)];
		Batch soundBatch = new Batch("sound");
		soundBatch.add("uuid", Player.uuid);
		soundBatch.add("name", name);
		soundBatch.add("x", x);
		soundBatch.add("y", y);
		ClientMain.networkClient.send(soundBatch);
	}

	private static void playClip(String path, float volume) throws Exception {
		class AudioListener implements LineListener {
			private boolean done = false;

			@Override
			public synchronized void update(LineEvent event) {
				Type eventType = event.getType();
				if (eventType == Type.STOP || eventType == Type.CLOSE) {
					done = true;
					notifyAll();
				}
			}

			public synchronized void waitUntilDone() throws InterruptedException {
				while (!done) {
					wait();
				}
			}
		}

		AudioListener listener = new AudioListener();
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Sound.class.getResource(path));
		try {
			Clip clip = AudioSystem.getClip();
			clip.addLineListener(listener);
			clip.open(audioInputStream);
			FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			control.setValue((float) (20f * Math.log10(volume)));
			try {
				clip.start();
				listener.waitUntilDone();
			} finally {
				clip.close();
			}
		} finally {
			audioInputStream.close();
		}
	}

}
