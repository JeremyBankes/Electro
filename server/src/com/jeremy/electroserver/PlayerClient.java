package com.jeremy.electroserver;

import java.io.IOException;
import java.io.OutputStream;

import com.jeremy.electroserver.world.entity.Character;
import com.jeremy.networking.Endpoint;
import com.sineshore.serialization.Batch;

public class PlayerClient {

	public final Endpoint endpoint;

	private final String name;
	private final String uuid;
	private final OutputStream outputStream;

	public PlayerClient(String name, String uuid, OutputStream outputStream, Endpoint endpoint) {
		this.name = name;
		this.uuid = uuid;
		this.outputStream = outputStream;
		this.endpoint = endpoint;
	}

	public void send(Batch batch) {
		try {
			outputStream.write(batch.toBytes());
		} catch (IOException exception) {
			try {
				ServerMain.networkServer.disconnect(endpoint);
			} catch (IOException disconncet) {
				disconncet.printStackTrace();
			}
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
