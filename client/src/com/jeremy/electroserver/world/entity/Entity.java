package com.jeremy.electroserver.world.entity;

import com.jeremy.electroserver.world.World;
import com.jeremy.serialization.v3_0.Bundle;

public abstract class Entity {

	protected String uuid;
	protected float x, y;
	protected float xv, yv;
	protected int width, height;
	protected World world;
	protected int age;
	protected boolean alive;

	public Entity(String uuid) {
		this.uuid = uuid;
		alive = true;
	}

	public void tick() {
		age++;
	}

	public abstract void onSpawn(World world);

	public abstract void onDeath(World world);

	public abstract Bundle getSpawnBatch();

	public abstract Bundle getUpdateBatch();

	public abstract Bundle getDeathBatch();

	public World getWorld() {
		return world;
	}

	public String getUuid() {
		return uuid;
	}

	public boolean isAlive() {
		return alive;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getXVelocity() {
		return xv;
	}

	public float getYVelocity() {
		return yv;
	}

	public void setxVelocity(float xVelocity) {
		this.xv = xVelocity;
	}

	public void setyVelocity(float yVelocity) {
		this.yv = yVelocity;
	}

	public void addVelocity(float x, float y) {
		this.xv += x;
		this.yv += y;
	}

	public void addPosition(float x, float y) {
		this.x += x;
		this.y += y;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + uuid + ")";
	}

}
