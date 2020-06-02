package com.jeremy.electroserver.network;

import java.io.OutputStream;

import com.jeremy.electroserver.PlayerClient;
import com.jeremy.electroserver.ServerMain;
import com.jeremy.electroserver.world.entity.Character;
import com.sineshore.serialization.Batch;

public class Receiver {

	public static void receive(OutputStream outputStream, Batch batch, String address, int port) {
		try {
			String type = batch.getName();
			String uuid = (String) batch.get("uuid");
			if (type.equals("connect")) {
				String name = (String) batch.get("name");
				// int udpPort = (int) batch.get("udp");
				PlayerClient client = new PlayerClient(name, uuid, outputStream, address, port);
				ServerMain.networkServer.registered.put(uuid, client);
				client.send(ServerMain.getWorld().getWorldBatch());
				ServerMain.getWorld().getEntities().forEach(entity -> client.send(entity.getSpawnBatch()));
				ServerMain.getWorld().spawnEntity(new Character(client));
				System.out.printf("New player connected %s.%n", client);
			}
			PlayerClient client = ServerMain.networkServer.getRegistered(uuid);
			if (type.equals("connect")) {
				ServerMain.networkServer.broadcastMessage("[!] '" + client.getName() + "' has joined the game!");
			} else if (type.equals("update")) {
				Character character = (Character) ServerMain.getWorld().getEntity(uuid);
				if (character != null) {
					character.setX((float) batch.get("x"));
					character.setY((float) batch.get("y"));
					character.setHandX((float) batch.get("hx"));
					character.setHandY((float) batch.get("hy"));
				}
			} else if (type.equals("harm")) {
				PlayerClient victim = ServerMain.networkServer.getRegistered((String) batch.get("victim"));
				if (victim != null && victim.getCharacter() != null) {
					victim.getCharacter().harm((float) batch.get("damage"));
					victim.getCharacter().setAddXv((float) batch.get("xv"));
					victim.getCharacter().setAddYv((float) batch.get("yv"));
					if (!victim.getCharacter().isAlive()) {
						ServerMain.networkServer.broadcastMessage(ServerMain.getMurderMessage(victim.getName(), client.getName()));
						if (client.getCharacter().killStreak++ > 1) {
							ServerMain.networkServer
									.broadcastMessage(client.getName() + " is on a " + client.getCharacter().killStreak + " player killstreak!");
						}
					}
				}
			} else if (type.equals("sound")) {
				Batch soundBatch = new Batch("sound");
				soundBatch.add("name", batch.get("name"));
				soundBatch.add("name", batch.get("name"));
				for (PlayerClient c : ServerMain.networkServer.registered.values()) {
					if (c.getCharacter() != null && client.getCharacter() != null) {
						float volume = Math.max(0, 1f - client.getCharacter().distance(c.getCharacter()) / 400);
						if (volume > 0) {
							soundBatch.add("volume", volume);
							c.send(soundBatch);
						}
					}
				}
			} else if (type.equals("respawn")) {
				ServerMain.getWorld().spawnEntity(new Character(client));
				ServerMain.networkServer.broadcastMessage("[!] " + client.getName() + " has respawned!");
			} else if (type.equals("message")) {
				ServerMain.networkServer.broadcastMessage("<" + client.getName() + "> " + batch.get("message"));
			} else {
				System.out.println("Unhandled batch for player " + client + ": " + batch);
			}
		} catch (NullPointerException e) {
			System.out.println("Failed to interpret batch. " + e.getMessage() + ".\n" + batch);
			e.printStackTrace();
		}
	}

}
