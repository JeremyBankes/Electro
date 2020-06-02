package com.jeremy.electroserver;

import java.io.IOException;
import java.io.OutputStream;

import com.jeremy.electroserver.world.entity.Character;
import com.sineshore.serialization.Batch;

public class PlayerClient {

	public final String address;
	public final int port;

	private final String name;
	private final String uuid;
	private final OutputStream outputStream;

	public PlayerClient(String name, String uuid, OutputStream outputStream, String address, int port) {
		this.name = name;
		this.uuid = uuid;
		this.outputStream = outputStream;
		this.address = address;
		this.port = port;
	}

	public void send(Batch batch) {
		try {
			outputStream.write(batch.toBytes());
		} catch (IOException exception) {
			ServerMain.networkServer.disconnect(address, port);
		}
	}

	public String getName() {
		return name;
	}

	public String getUuid() {
		return uuid;
	}

	public Character getCharacter() {
		return (Character) ServerMain.getWorld().getEntity(uuid);
	}

	@Override
	public String toString() {
		return name + " (" + uuid + ")";
	}

}
