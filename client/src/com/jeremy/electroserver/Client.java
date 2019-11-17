package com.jeremy.electroserver;

import java.io.IOException;

import com.jeremy.electroserver.network.Network;
import com.jeremy.electroserver.world.entity.Character;
import com.jeremy.serialization.v3_0.Bundle;

public class Client {

	private final String name;
	private final String uuid;
	private final String tcpAddress;
	private final String udpAddress;

	public Client(String name, String uuid, String tcpAddress, String udpAddress) {
		this.name = name;
		this.uuid = uuid;
		this.tcpAddress = tcpAddress;
		this.udpAddress = udpAddress;
	}

	public void sendFast(Bundle batch) {		
		try {
			Network.sendFast(udpAddress, batch);
		} catch (IOException e) {
			System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
		}
	}

	public void sendSecure(Bundle batch) {
		try {
			Network.sendSecure(tcpAddress, batch);
		} catch (IOException e) {
			System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
		}
	}

	public String getName() {
		return name;
	}

	public String getUuid() {
		return uuid;
	}

	public Character getCharacter() {
		return (Character) Main.getWorld().getEntity(uuid);
	}

	@Override
	public String toString() {
		return name + " (" + uuid + ")";
	}

}
