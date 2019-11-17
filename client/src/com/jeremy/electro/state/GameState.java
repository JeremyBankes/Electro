package com.jeremy.electro.state;

import java.awt.Graphics2D;

import com.jeremy.electro.Main;
import com.jeremy.electro.Player;
import com.jeremy.electro.input.Input;
import com.jeremy.electro.network.Network;
import com.jeremy.electro.state.ui.GameInterface;
import com.jeremy.electro.state.world.World;

public class GameState extends State {

	private World world;

	public GameState() {

	}

	@Override
	public void tick() {
		if (!Network.isConnected()) {
			State.currentState = LOBBY_STATE;
			LOBBY_STATE.message = "Server closed.";
		}

		if (Input.isKeyPressed(Input.KEY_ENTER, Input.KEY_T, Input.KEY_SLASH)) {
			Main.focusChat();
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
