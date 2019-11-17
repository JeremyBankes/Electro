package com.jeremy.electro.state;

import static com.sineshore.utilities.Utilities.*;

import java.awt.Color;
import java.awt.Graphics2D;

import com.jeremy.electro.Main;
import com.jeremy.electro.Player;
import com.jeremy.electro.audio.Sound;
import com.jeremy.electro.input.Input;
import com.jeremy.electro.network.Network;
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
		Main.clearDialog();
		Main.setInputEnabled(true);
		message = "Connecting to server...";

		Network.start("76.11.68.118", 37427);
		Network.runIfConnected(() -> {
			message = "Choose a name!";
			while (Player.name == null) {
				Main.sendMessage("What is your name? (Your cursor is in the bottom left)");
				Main.focusChat();
				Player.name = Main.getInput();
				if (Player.name.length() < 3) {
					Main.clearDialog();
					Main.sendMessage("Please make your name longer than 2 characters.");
					Player.name = null;
				}
			}
			Main.clearDialog();
			Sound.playLocalSound("welcome");

			Batch connectBatch = new Batch("connect");
			connectBatch.add("uuid", Player.uuid.toString());
			connectBatch.add("name", Player.name);
			connectBatch.add("udp", Network.getUdpPort());
			Network.sendSecure(connectBatch);
			State.currentState = State.GAME_STATE;
		}, () -> {
			message = "Failed to connect to server.";
			showButton = true;
		});
	}

	@Override
	public void tick() {
		if (showButton) {
			buttonHover = inBoundary(Input.CURSOR.x, Main.WIDTH / 2 - 50, 100) && inBoundary(Input.CURSOR.y, Main.HEIGHT / 2 - 25, 50);
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
			if (buttonHover)
				g.setColor(new Color(0x383838));
			else
				g.setColor(new Color(0x1c1c1c));
			g.fillRect(Main.WIDTH / 2 - 50, Main.HEIGHT / 2 - 25, 100, 50);
			g.setColor(Main.HACKER_GREEN);
			g.drawString("Play", Main.WIDTH / 2 - g.getFontMetrics().stringWidth("Play") / 2, Main.HEIGHT / 2 + g.getFontMetrics().getAscent() / 2);
		}
		g.setColor(Main.HACKER_GREEN);
		g.drawString(message, Main.WIDTH / 2 - g.getFontMetrics().stringWidth(message) / 2, Main.HEIGHT * 3 / 4);
	}

}
