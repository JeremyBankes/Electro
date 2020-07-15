package com.jeremy.electro.state.ui;

import static java.lang.Math.*;

import java.awt.Color;
import java.awt.Graphics2D;

import com.jeremy.electro.ClientMain;
import com.jeremy.electro.entity.Player;

public class GameInterface {

	public static final Color HEALTH_COLOR = new Color(200, 0, 0, 100);
	public static final Color ENERGY_COLOR = new Color(80, 220, 220, 100);

	public static final int BAR_HEIGHT = 25;

	public static void tick() {

	}

	public static void render(Graphics2D g) {
		g.setColor(HEALTH_COLOR);
		g.fillRect(0, ClientMain.HEIGHT - BAR_HEIGHT, round(Player.health * ClientMain.WIDTH / Player.maxHealth), BAR_HEIGHT);
		g.setColor(ENERGY_COLOR);
		g.fillRect(0, ClientMain.HEIGHT - BAR_HEIGHT * 2, round(Player.energy * ClientMain.WIDTH), BAR_HEIGHT);
	}

}
