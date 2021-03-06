package com.jeremy.electro.network;

import java.awt.Color;

import com.jeremy.electro.ClientMain;
import com.jeremy.electro.audio.Sound;
import com.jeremy.electro.entity.Character;
import com.jeremy.electro.entity.Entity;
import com.jeremy.electro.entity.Player;
import com.jeremy.electro.state.State;
import com.jeremy.electro.state.world.World;
import com.sineshore.serialization.Batch;

public class Receiver {

	public static void receive(Batch batch) {
		String type = batch.getName();
		if (type.equals("world")) {
			String name = (String) batch.get("name");
			State.GAME_STATE.setWorld(new World(name));
		} else if (type.equals("spawn")) {
			String uuid = (String) batch.get("uuid");
			Character character = new Character(uuid);
			character.name = (String) batch.get("name");
			character.position.x = (int) ((float) batch.get("x"));
			character.position.y = (int) ((float) batch.get("y"));
			character.width = (int) batch.get("width");
			character.height = (int) batch.get("height");
			character.color = new Color((int) batch.get("color"));
			Player.maxHealth = Player.health = (float) batch.get("maxHealth");
			State.GAME_STATE.getWorld().spawnEntity(character);
			if (Player.uuid.equals(uuid)) {
				Player.character = character;
				Player.alive = true;
			}
			Sound.playPublicSound(character.position.x, character.position.y, "respawn");
		} else if (type.equals("update")) {
			String uuid = (String) batch.get("uuid");
			if (State.GAME_STATE.hasWorld()) {
				Character character = (Character) State.GAME_STATE.getWorld().getEntity(uuid);
				if (character != null) {
					character.position.x = (int) ((float) batch.get("x"));
					character.position.y = (int) ((float) batch.get("y"));
					character.hand.position.x = (float) batch.get("hx");
					character.hand.position.y = (float) batch.get("hy");
				}
			}
		} else if (type.equals("info")) {
			if (State.GAME_STATE.hasWorld()) {
				Player.health = (float) batch.get("health");
				Player.velocity.x += (float) batch.get("addXv");
				Player.velocity.y += (float) batch.get("addYv");
			}
		} else if (type.equals("death")) {
			String uuid = (String) batch.get("uuid");
			if (State.GAME_STATE.getWorld().hasEntity(uuid)) {
				Entity entity = State.GAME_STATE.getWorld().getEntity(uuid);
				Sound.playPublicSound(entity.position.x, entity.position.y, "death");
				State.GAME_STATE.getWorld().deleteEntity(uuid);
			}
			if (Player.uuid.equals(uuid)) {
				Player.alive = false;
				ClientMain.sendMessage("You have died! Type /respawn to come back into the world.");
			}
		} else if (type.equals("message")) {
			ClientMain.sendMessage((String) batch.get("message"));
		} else if (type.equals("sound")) {
			Sound.playLocalSound((String) batch.get("name"), (float) batch.get("volume"));
		} else {
			System.out.println("Unhandled: " + batch);
		}
	}

}
