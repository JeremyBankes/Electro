package com.jeremy.electro.audio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import com.jeremy.electro.ClientMain;
import com.jeremy.electro.entity.Player;
import com.sineshore.serialization.Batch;

public class Sound {

	private static HashMap<String, AudioInputStream> sounds = new HashMap<>();

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
		AudioInputStream inputStream;
		if (sounds.containsKey(path)) {
			inputStream = sounds.get(path);
			inputStream.reset();
		} else {
			inputStream = AudioSystem.getAudioInputStream(Sound.class.getResource(path));

			byte[] buffer = new byte[1024 * 32];
			int read = 0;
			ByteArrayOutputStream baos = new ByteArrayOutputStream(buffer.length);
			while ((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
				baos.write(buffer, 0, read);
			}
			inputStream = new AudioInputStream(new ByteArrayInputStream(baos.toByteArray()), inputStream.getFormat(), AudioSystem.NOT_SPECIFIED);
			sounds.put(path, inputStream);
		}
		Clip clip = AudioSystem.getClip();
		clip.open(inputStream);
		FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		control.setValue((float) (20f * Math.log10(volume)));
		clip.start();
	}

}
