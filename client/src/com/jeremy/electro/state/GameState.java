package com.jeremy.electro.state;

import java.awt.Graphics2D;

import com.jeremy.electro.MainClient;
import com.jeremy.electro.entity.Player;
import com.jeremy.electro.input.Input;
import com.jeremy.electro.state.ui.GameInterface;
import com.jeremy.electro.state.world.World;

public class GameState extends State {

	private World world;

	public GameState() {

	}

	@Override
	public void tick() {
		if (!MainClient.networkClient.isConnected()) {
			State.currentState = LOBBY_STATE;
			LOBBY_STATE.message = "Server closed.";
		}

		if (Input.isKeyPressed(Input.KEY_ENTER, Input.KEY_T, Input.KEY_SLASH)) {
			MainClient.focusChat();
		}

		Player.tick();
		if (world != null) {
			world.tick();
		}
		GameInterface.tick();
	}

	@Override
	public void render(Graphics2D g) {
		if (world != null) {
			world.render(g);
		}
		GameInterface.render(g);
	}

	public World getWorld() {
		return world;
	}

	public boolean hasWorld() {
		return world != null;
	}

	public void setWorld(World world) {
		this.world = world;
	}

}
