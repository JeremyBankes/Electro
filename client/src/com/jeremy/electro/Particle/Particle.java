package com.jeremy.electro.Particle;

import static java.lang.Math.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

public class Particle {

	private static Random random = new Random();

	public float xVelocity, yVelocity;
	public float x, y;
	public int width = 8, height = 8;
	public Color color = Color.WHITE;

	private int age = 0;
	private int lifetime = 30;

	public Particle(float x, float y, Color color) {
		this.x = x;
		this.y = y;
		this.color = color;
		xVelocity = random.nextFloat() * 8.0f;
		yVelocity = random.nextFloat() * 8.0f;
	}

	public void tick() {
		x += xVelocity;
		y += yVelocity;
		if (isAlive()) age++;
	}

	public void render(Graphics2D g) {
		g.setColor(color);
		g.fillRect(round(x), round(y), width, height);
	}

	public boolean isAlive() {
		return age < lifetime;
	}

	public int getAge() {
		return age;
	}

}
