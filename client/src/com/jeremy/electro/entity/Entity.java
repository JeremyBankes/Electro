package com.jeremy.electro.entity;

import static java.lang.Math.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

import com.jeremy.electro.ClientMain;
import com.jeremy.utilities.Vector2;

public abstract class Entity {

	private static final int SHADOW_LENGTH = 20;

	public String uuid;
	public Vector2 position = new Vector2();
	public int width, height;
	public Color color;
	public boolean alive;

	private int[] xShadows = new int[4];
	private int[] yShadows = new int[4];

	public Entity(String uuid) {
		this.uuid = uuid;
		alive = true;
	}

	public void tick() {

	}

	public void render(Graphics2D g) {
		float halfWidth = ClientMain.WIDTH / 2;
		float halfHeight = ClientMain.HEIGHT / 2;
		float xShadowOffset = (position.x + width / 2 - halfWidth) * SHADOW_LENGTH / halfWidth;
		float yShadowOffset = (position.y + height / 2 - halfHeight) * SHADOW_LENGTH / halfHeight;
		xShadows[0] = round(position.x + xShadowOffset);
		xShadows[1] = round(position.x);
		xShadows[2] = round(position.x + width);
		xShadows[3] = round(position.x + width + xShadowOffset);
		yShadows[0] = round(position.y + yShadowOffset);
		yShadows[1] = round(position.y + height);
		yShadows[2] = round(position.y + height);
		yShadows[3] = round(position.y + yShadowOffset);
		g.setColor(Color.BLACK);
		g.fill(new Polygon(xShadows, yShadows, xShadows.length));

		g.setColor(color);
		g.fillRect(round(position.x), round(position.y), width, height);
		g.setColor(color.brighter());
		g.fillRect(round(position.x + width / 6), round(position.y + height / 6), width / 4, height / 4);
	}

}
