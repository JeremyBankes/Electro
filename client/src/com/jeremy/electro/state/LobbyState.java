package com.jeremy.electro.state;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;

import com.jeremy.electro.MainClient;
import com.jeremy.electro.audio.Sound;
import com.jeremy.electro.entity.Player;
import com.jeremy.electro.input.Input;
import com.jeremy.utilities.Utilities;
import com.sineshore.serialization.Batch;

public class LobbyState extends State {
	private boolean showButton;
	private boolean buttonHover;
	private boolean click;
	public String message = "Click the button to play!";

	public LobbyState() {
		this.showButton = true;
	}

	private void execute() {
		Sound.playLocalSound("click");
		this.showButton = false;
		MainClient.clearDialog();
		MainClient.setInputEnabled(true);

		setDisplayText("Waiting on server address...");
		while (!MainClient.networkClient.isConnected()) {
			try {
				MainClient.sendMessage("What is the server address you would like to connect to?");
				MainClient.setInput("jeremybankes.com");
				MainClient.focusChat();
				String address = MainClient.getInput();
				setDisplayText("Connecting to '" + address + "'...");
				MainClient.clearDialog();
				MainClient.networkClient.connect(address, 37427);
			} catch (IOException ioException) {
				setDisplayText("Failed to connect to server. " + ioException.getMessage() + ".");
			}
		}

		setDisplayText("Waiting on username...");
		while (Player.name == null) {
			MainClient.sendMessage("What name would you like to go by?");
			MainClient.focusChat();
			Player.name = MainClient.getInput();
			if (Player.name.length() < 3) {
				MainClient.clearDialog();
				MainClient.sendMessage("Please make your name longer than 2 characters.");
				Player.name = null;
				continue;
			}
			if (Player.name.length() > 11) {
				MainClient.clearDialog();
				MainClient.sendMessage("Please make your name shorter than 12 characters.");
				Player.name = null;
				continue;
			}
			if (!Player.name.matches("^[A-z]+$")) {
				MainClient.clearDialog();
				MainClient.sendMessage("Please only use letters in your name.");
				Player.name = null;
			}
		}
		MainClient.clearDialog();
		Sound.playLocalSound("welcome");

		Batch connectBatch = new Batch("connect");
		connectBatch.add("uuid", Player.uuid.toString());
		connectBatch.add("name", Player.name);
		MainClient.networkClient.send(connectBatch);
		State.currentState = State.GAME_STATE;
	}

	private final void setDisplayText(String text) {
		this.message = text;
		MainClient.tick();
		MainClient.render();
	}

	public void tick() {
		if (this.showButton) {
			this.buttonHover = (Utilities.inBoundary(Input.CURSOR.x, 400.0D, 100.0D) && Utilities.inBoundary(Input.CURSOR.y, 275.0D, 50.0D));
			if (this.buttonHover && Input.isButtonPressed(1)) {
				if (!this.click) {
					this.click = true;
					execute();
				}
			} else {
				this.click = false;
			}
		}
	}

	public void render(Graphics2D g) {
		if (this.showButton) {
			if (this.buttonHover) {
				g.setColor(new Color(3684408));
			} else {
				g.setColor(new Color(1842204));
			}
			g.fillRect(400, 275, 100, 50);
			g.setColor(MainClient.HACKER_GREEN);
			g.drawString("Play", 450 - g.getFontMetrics().stringWidth("Play") / 2, 300 + g.getFontMetrics().getAscent() / 2);
		}
		g.setColor(MainClient.HACKER_GREEN);
		g.drawString(this.message, 450 - g.getFontMetrics().stringWidth(this.message) / 2, 450);
	}
}
