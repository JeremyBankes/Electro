package com.jeremy.electro.entity;

import java.awt.Color;
import java.awt.Graphics2D;

import com.jeremy.electro.Player;

public class Character extends Entity {

    public String name;
    private Hand hand;

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
	g.drawString(name, x + width / 2 - g.getFontMetrics().stringWidth(name) / 2, y - 5);
    }

    public float getHandX() {
	return hand.x;
    }

    public float getHandY() {
	return hand.y;
    }

    public void setHandX(float x) {
	hand.x = x;
    }

    public void setHandY(float y) {
	hand.y = y;
    }

    private class Hand {

	private float x, y;

	private void render(Graphics2D g) {
	    g.setColor(color.darker());
	    g.fillRect((int) x, (int) y, Player.HAND_SIZE, Player.HAND_SIZE);
	    g.setColor(Color.WHITE);
	    g.drawRect((int) x, (int) y, Player.HAND_SIZE, Player.HAND_SIZE);
	}

    }

}
