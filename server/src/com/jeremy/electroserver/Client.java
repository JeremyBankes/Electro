package com.jeremy.electroserver;

import java.io.IOException;
import java.io.OutputStream;

import com.jeremy.electroserver.network.Network;
import com.jeremy.electroserver.world.entity.Character;
import com.sineshore.serialization.Batch;

public class Client {

    private final String name;
    private final String uuid;
    private final OutputStream outputStream;
    private final String address;
    private final int udpPort;

    public Client(String name, String uuid, OutputStream outputStream, String address, int udpPort) {
	this.name = name;
	this.uuid = uuid;
	this.outputStream = outputStream;
	this.address = address;
	this.udpPort = udpPort;
    }

    public void sendFast(Batch batch) {
	try {
	    Network.sendFast(batch, address, udpPort);
	} catch (IOException e) {
	    System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
	}
    }

    public void sendSecure(Batch batch) {
	try {
	    Network.sendSecure(batch, outputStream);
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
