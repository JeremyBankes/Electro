package com.jeremy.electroserver.world.entity;

import java.awt.Color;
import java.util.Random;

import com.jeremy.electroserver.Client;
import com.jeremy.electroserver.network.Network;
import com.jeremy.electroserver.world.World;
import com.jeremy.serialization.v3_0.Bundle;

public class Character extends Creature {

	private static final float MAX_HEALTH = 100.0f;
	private static final int WIDTH = 30;
	private static final int HEIGHT = 30;

	private Client client;
	private Color color;

	private float handX, handY;
	private float addXv, addYv;

	public int killStreak = 0;

	public Character(Client client) {
		super(client.getUuid(), client.getName(), MAX_HEALTH);
		this.client = client;
		this.width = WIDTH;
		this.height = HEIGHT;
		Random random = new Random();
		color = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1f).darker();
		x = random.nextInt(960 - 150 + 1) + 150;
		y = random.nextInt(720 - 150 + 1) + 150;
	}

	@Override
	public void tick() {
		Network.broadcastFast(getUpdateBatch(), client);
		client.sendFast(getInfoBatch());
		super.tick();
		if (health < maxHealth) {
			health += 0.025f;
			if (health > maxHealth) {
				health = maxHealth;
			}
		}
	}

	@Override
	public void onSpawn(World world) {
		this.world = world;
		Network.broadcastSecure(getSpawnBatch());
	}

	@Override
	public void onDeath(World world) {
		Network.broadcastSecure(getDeathBatch());
		this.world = null;
	}

	@Override
	public Bundle getSpawnBatch() {
		Bundle spawnBatch = new Bundle("spawn");
		spawnBatch.put("uuid", uuid);
		spawnBatch.put("name", name);
		spawnBatch.put("x", x);
		spawnBatch.put("y", y);
		spawnBatch.put("width", width);
		spawnBatch.put("height", height);
		spawnBatch.put("color", color.getRGB());
		spawnBatch.put("maxHealth", maxHealth);
		spawnBatch.put("world", world.getName());
		return spawnBatch;
	}

	@Override
	public Bundle getUpdateBatch() {
		Bundle updateBatch = new Bundle("update");
		updateBatch.put("uuid", uuid);
		updateBatch.put("x", x);
		updateBatch.put("y", y);
		updateBatch.put("hx", handX);
		updateBatch.put("hy", handY);
		updateBatch.put("health", health);
		return updateBatch;
	}

	@Override
	public Bundle getDeathBatch() {
		Bundle deathBatch = new Bundle("death");
		deathBatch.put("uuid", uuid);
		return deathBatch;
	}

	public Bundle getInfoBatch() {
		Bundle infoBatch = new Bundle("info");
		infoBatch.put("health", health);
		infoBatch.put("addXv", addXv);
		infoBatch.put("addYv", addYv);
		addYv = addXv = 0;
		return infoBatch;
	}

	public Client getPlayer() {
		return client;
	}

	public float getHandX() {
		return handX;
	}

	public void setHandX(float handX) {
		this.handX = handX;
	}

	public float getHandY() {
		return handY;
	}

	public void setHandY(float handY) {
		this.handY = handY;
	}

	public float getAddXv() {
		return addXv;
	}

	public void setAddXv(float addXv) {
		this.addXv = addXv;
	}

	public float getAddYv() {
		return addYv;
	}

	public void setAddYv(float addYv) {
		this.addYv = addYv;
	}

	public float distance(Character character) {
		return (float) Math.sqrt((x - character.x) * (x - character.y) + (y - character.y) * (y - character.y));
	}

}
