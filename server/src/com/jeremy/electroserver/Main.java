package com.jeremy.electroserver;

import java.util.Random;

import com.jeremy.electroserver.network.Network;
import com.jeremy.electroserver.world.World;

public class Main {

    private static World world;

    private static boolean running;

    public static void start() {
	Network.init();
	Network.start();

	world = new World("Branch");

	running = true;
	run();
    }

    private static void run() {
	long currentTime;
	final long second = 1000000000;
	int ticks = 0;
	long tickTime = second / 60;
	long tickTimer = System.nanoTime();
	long secondTimer = System.nanoTime();
	while (running) {
	    currentTime = System.nanoTime();
	    if (currentTime - tickTimer > tickTime) {
		if (currentTime - tickTimer > tickTime * 2) {
		    tickTimer = currentTime;
		}
		tickTimer += tickTime;
		ticks++;
		tick();
	    }
	    if (currentTime - secondTimer > second) {
		secondTimer += second;
		if (!running)
		    System.out.println("TPS: " + ticks);
		ticks = 0;
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
	    "[!] {1} ended {0}." //

    };

    public static String getMurderMessage(String victim, String attacker) {
	return MURDER_MESSAGES[new Random().nextInt(MURDER_MESSAGES.length)].replace("{0}", victim).replace("{1}", attacker);
    }

}
