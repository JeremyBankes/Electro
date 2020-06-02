package com.jeremy.electroserver.world.entity;

import java.awt.Color;
import java.util.Random;

import com.jeremy.electroserver.PlayerClient;
import com.jeremy.electroserver.ServerMain;
import com.jeremy.electroserver.world.World;
import com.sineshore.serialization.Batch;

public class Character extends Creature {

	private static final float MAX_HEALTH = 100.0f;
	private static final int WIDTH = 30;
	private static final int HEIGHT = 30;

	private PlayerClient client;
	private Color color;

	private float handX, handY;
	private float addXv, addYv;

	public int killStreak = 0;

	public Character(PlayerClient client) {
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
		ServerMain.networkServer.broadcast(getUpdateBatch(), client);
		client.send(getInfoBatch());
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
		ServerMain.networkServer.broadcast(getSpawnBatch());
	}

	@Override
	public void onDeath(World world) {
		ServerMain.networkServer.broadcast(getDeathBatch());
		this.world = null;
	}

	@Override
	public Batch getSpawnBatch() {
		Batch spawnBatch = new Batch("spawn");
		spawnBatch.add("uuid", uuid);
		spawnBatch.add("name", name);
		spawnBatch.add("x", x);
		spawnBatch.add("y", y);
		spawnBatch.add("width", width);
		spawnBatch.add("height", height);
		spawnBatch.add("color", color.getRGB());
		spawnBatch.add("maxHealth", maxHealth);
		spawnBatch.add("world", world.getName());
		return spawnBatch;
	}

	@Override
	public Batch getUpdateBatch() {
		Batch updateBatch = new Batch("update");
		updateBatch.add("uuid", uuid);
		updateBatch.add("x", x);
		updateBatch.add("y", y);
		updateBatch.add("hx", handX);
		updateBatch.add("hy", handY);
		updateBatch.add("health", health);
		return updateBatch;
	}

	@Override
	public Batch getDeathBatch() {
		Batch deathBatch = new Batch("death");
		deathBatch.add("uuid", uuid);
		return deathBatch;
	}

	public Batch getInfoBatch() {
		Batch infoBatch = new Batch("info");
		infoBatch.add("health", health);
		infoBatch.add("addXv", addXv);
		infoBatch.add("addYv", addYv);
		addYv = addXv = 0;
		return infoBatch;
	}

	public PlayerClient getPlayer() {
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
