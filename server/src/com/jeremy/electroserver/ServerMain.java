package com.jeremy.electroserver;

import java.io.IOException;
import java.util.Random;

import com.jeremy.electroserver.network.NetworkServer;
import com.jeremy.electroserver.world.World;

public class ServerMain {

	private static World world;

	private static boolean running;

	public static NetworkServer networkServer;

	public static void start() {
		try {
			networkServer = new NetworkServer();
			System.out.println("Starting network...");
			networkServer.start();

			System.out.println("Creating world...");
			world = new World("Branch");

			System.out.println("Starting server logic...");
			running = true;
			run();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	private static void run() {
		System.out.println("The server has successfully initialized.");
		long currentTime;
		final long second = 1000000000;
		long tickTime = second / 60;
		long tickTimer = System.nanoTime();
		while (running) {
			currentTime = System.nanoTime();
			if (currentTime - tickTimer > tickTime) {
				if (currentTime - tickTimer > tickTime * 2) {
					tickTimer = currentTime;
				}
				tickTimer += tickTime;
				tick();
			}
		}
	}

	private static void tick() {
		world.tick();
	}

	public static World getWorld() {
		return world;
	}

	private static final String[] MURDER_MESSAGES = {

			"[!] {0} was brutally murdered by {1}.", //
			"[!] {0} was killed by {1}.", //
			"[!] {0}'s life was taken by {1}.", //
			"[!] {0}'s existence was ended by {1}.", //
			"[!] {1} tore {0}'s life from them.", //
			"[!] {1} brought {0} to an end.", //
			"[!] {1} stole {0}'s life.", //
			"[!] {1} obliterated {0}.", //
			"[!] {1} ended {0}." //

	};

	public static String getMurderMessage(String victim, String attacker) {
		return MURDER_MESSAGES[new Random().nextInt(MURDER_MESSAGES.length)].replace("{0}", victim).replace("{1}", attacker);
	}

	public static void main(String[] args) {
		ServerMain.start();
	}

}
