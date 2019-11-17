package com.jeremy.electroserver.network;

import java.io.OutputStream;

import com.jeremy.electroserver.Client;
import com.jeremy.electroserver.Main;
import com.jeremy.electroserver.world.entity.Character;
import com.sineshore.serialization.Batch;

public class Receiver {

    public static void receive(String address, int tcpPort, Batch batch, OutputStream outputStream) {
	try {
	    String type = batch.getName();
	    String uuid = (String) batch.get("uuid");
	    if (type.equals("connect")) {
		String name = (String) batch.get("name");
		int udpPort = (int) batch.get("udp");
		Client client = new Client(name, uuid, outputStream, address, udpPort);
		Network.CONNECTED_PLAYERS.put(address + ":" + tcpPort, client);
		client.sendSecure(Main.getWorld().getWorldBatch());
		Main.getWorld().getEntities().forEach(entity -> client.sendSecure(entity.getSpawnBatch()));
		Main.getWorld().spawnEntity(new Character(client));
		System.out.printf("New player connected %s.%n", client);
	    }
	    Client client = Network.getClient(uuid);
	    if (type.equals("connect")) {
		Network.broadcastMessage("[!] '" + client.getName() + "' has joined the game!");
	    } else if (type.equals("update")) {
		Character character = (Character) Main.getWorld().getEntity(uuid);
		if (character != null) {
		    character.setX((float) batch.get("x"));
		    character.setY((float) batch.get("y"));
		    character.setHandX((float) batch.get("hx"));
		    character.setHandY((float) batch.get("hy"));
		}
	    } else if (type.equals("harm")) {
		Client victim = Network.getClient((String) batch.get("victim"));
		if (victim != null && victim.getCharacter() != null) {
		    victim.getCharacter().harm((float) batch.get("damage"));
		    victim.getCharacter().setAddXv((float) batch.get("xv"));
		    victim.getCharacter().setAddYv((float) batch.get("yv"));
		    if (!victim.getCharacter().isAlive()) {
			Network.broadcastMessage(Main.getMurderMessage(victim.getName(), client.getName()));
			if (client.getCharacter().killStreak++ > 1) {
			    Network.broadcastMessage(client.getName() + " is on a " + client.getCharacter().killStreak + " player killstreak!");
			}
		    }
		}
	    } else if (type.equals("sound")) {
		Batch soundBatch = new Batch("sound");
		soundBatch.add("name", batch.get("name"));
		soundBatch.add("name", batch.get("name"));
		for (Client c : Network.CONNECTED_PLAYERS.values()) {
		    if (c.getCharacter() != null && client.getCharacter() != null) {
			float volume = Math.max(0, 1f - client.getCharacter().distance(c.getCharacter()) / 400);
			if (volume > 0) {
			    soundBatch.add("volume", volume);
			    c.sendFast(soundBatch);
			}
		    }
		}
	    } else if (type.equals("respawn")) {
		Main.getWorld().spawnEntity(new Character(client));
		Network.broadcastMessage("[!] " + client.getName() + " has respawned!");
	    } else if (type.equals("message")) {
		Network.broadcastMessage("<" + client.getName() + "> " + batch.get("message"));
	    } else {
		System.out.println("Unhandled batch for player " + client + ": " + batch);
	    }
	} catch (NullPointerException e) {
	    System.out.println("Failed to interpret batch. " + e.getMessage() + ".\n" + batch);
	    e.printStackTrace();
	}
    }

}
