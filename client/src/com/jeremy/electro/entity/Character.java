package com.jeremy.electro.entity;

import static java.lang.Math.*;

import java.awt.Color;
import java.awt.Graphics2D;

import com.jeremy.utilities.Vector2;

public class Character extends Entity {

	public String name;
	public Hand hand;

	public Character(String uuid) {
		super(uuid);
		hand = new Hand();
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public void render(Graphics2D g) {
		super.render(g);
		hand.render(g);
		g.setColor(Color.WHITE);
		g.drawString(name, position.x + width / 2 - g.getFontMetrics().stringWidth(name) / 2, position.y - 5);
	}

	public class Hand {

		private Hand() {}

		public Vector2 position = new Vector2();

		private void render(Graphics2D g) {
			g.setColor(color.darker());
			g.fillRect(round(position.x - Player.HAND_SIZE / 2), round(position.y - Player.HAND_SIZE / 2), Player.HAND_SIZE, Player.HAND_SIZE);
		}

	}

}
