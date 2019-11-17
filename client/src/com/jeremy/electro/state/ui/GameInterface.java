package com.jeremy.electro.state.ui;

import java.awt.Color;
import java.awt.Graphics2D;

import com.jeremy.electro.Main;
import com.jeremy.electro.Player;

public class GameInterface {

    public static final Color HEALTH_COLOR = new Color(200, 0, 0, 100);

    public static void tick() {
	
    }

    public static void render(Graphics2D g) {
	g.setColor(HEALTH_COLOR);
	g.fillRect(0, Main.HEIGHT - 50, (int) (Player.health * Main.WIDTH / Player.maxHealth), 50);
	g.setColor(Color.WHITE);
	String text = "Health: " + Math.round(Player.health * 100 / Player.maxHealth) + "%";
	g.drawString(text, Main.WIDTH / 2 - g.getFontMetrics().stringWidth(text) / 2, Main.HEIGHT - 30 + g.getFontMetrics().getHeight() / 2);
    }

}
