package com.jeremy.electro.state;

import java.awt.Graphics2D;

public abstract class State {

	public static final GameState GAME_STATE = new GameState();
	public static final LobbyState LOBBY_STATE = new LobbyState();

	public static State currentState = LOBBY_STATE;

	public abstract void tick();

	public abstract void render(Graphics2D g);

	public void enter() {
		currentState = this;
	}

}
