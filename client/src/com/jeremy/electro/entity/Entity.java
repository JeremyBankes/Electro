package com.jeremy.electro.entity;

import java.awt.Color;
import java.awt.Graphics2D;

public abstract class Entity {

	public String uuid;
	public float x, y;
	public int width, height;
	public Color color;
	public boolean alive;

	public Entity(String uuid) {
		this.uuid = uuid;
		alive = true;
	}

	public void tick() {

	}

	public void render(Graphics2D g) {
		g.setColor(color);
		g.fillRect((int) x, (int) y, width, height);
		g.setColor(Color.WHITE);
		g.drawRect((int) x, (int) y, width, height);
	}

}
