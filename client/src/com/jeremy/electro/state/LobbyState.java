package com.jeremy.electro.state;

import static com.jeremy.utilities.Utilities.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;

import com.jeremy.electro.MainClient;
import com.jeremy.electro.audio.Sound;
import com.jeremy.electro.entity.Player;
import com.jeremy.electro.input.Input;
import com.sineshore.serialization.Batch;

public class LobbyState extends State {

	private boolean showButton;
	private boolean buttonHover;
	private boolean click;

	public String message = "Click the button to play! :D";

	public LobbyState() {
		showButton = true;
	}

	private void execute() {
		Sound.playLocalSound("click");
		showButton = false;
		MainClient.clearDialog();
		MainClient.setInputEnabled(true);
		message = "Connecting to server...";

		try {
			MainClient.networkClient.connect("localhost", 37427);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		if (MainClient.networkClient.isConnected()) {
			new Thread(() -> {
				message = "Choose a name!";
				while (Player.name == null) {
					MainClient.sendMessage("What is your name? (Your cursor is in the bottom left)");
					MainClient.focusChat();
					Player.name = MainClient.getInput();
					if (Player.name.length() < 3) {
						MainClient.clearDialog();
						MainClient.sendMessage("Please make your name longer than 2 characters.");
						Player.name = null;
					}
				}
				MainClient.clearDialog();
				Sound.playLocalSound("welcome");

				Batch connectBatch = new Batch("connect");
				connectBatch.add("uuid", Player.uuid.toString());
				connectBatch.add("name", Player.name);
				connectBatch.add("udp", -1);
				MainClient.networkClient.send(connectBatch);
				State.currentState = State.GAME_STATE;
			}).start();
		} else {
			new Thread(() -> {
				message = "Failed to connect to server.";
				showButton = true;
			}).start();
		}
	}

	@Override
	public void tick() {
		if (showButton) {
			buttonHover = inBoundary(Input.CURSOR.x, MainClient.WIDTH / 2 - 50, 100) && inBoundary(Input.CURSOR.y, MainClient.HEIGHT / 2 - 25, 50);
			if (buttonHover && Input.isButtonPressed(Input.MOUSE_LEFT)) {
				if (!click) {
					click = true;
					execute();
				}
			} else {
				click = false;
			}
		}
	}

	@Override
	public void render(Graphics2D g) {
		if (showButton) {
			if (buttonHover) g.setColor(new Color(0x383838));
			else g.setColor(new Color(0x1c1c1c));
			g.fillRect(MainClient.WIDTH / 2 - 50, MainClient.HEIGHT / 2 - 25, 100, 50);
			g.setColor(MainClient.HACKER_GREEN);
			g.drawString("Play", MainClient.WIDTH / 2 - g.getFontMetrics().stringWidth("Play") / 2,
					MainClient.HEIGHT / 2 + g.getFontMetrics().getAscent() / 2);
		}
		g.setColor(MainClient.HACKER_GREEN);
		g.drawString(message, MainClient.WIDTH / 2 - g.getFontMetrics().stringWidth(message) / 2, MainClient.HEIGHT * 3 / 4);
	}

}
