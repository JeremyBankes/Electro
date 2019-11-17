package com.jeremy.electroserver.network;

import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.HashMap;

import com.jeremy.electroserver.Client;
import com.jeremy.electroserver.Main;
import com.sineshore.networking.Server;
import com.sineshore.serialization.Batch;

public class Network {

    public static final int PORT = 37427;

    protected static final HashMap<String, Client> CONNECTED_PLAYERS = new HashMap<>();

    private static Server server;

    public static void init() {
	try {
	    server = new Server(PORT);
	    server.setTCPRecieveCallback((address, port, input, output) -> {
		try {
		    Receiver.receive(address, port, new Batch(input), output);
		} catch (Exception e) {
		    server.disconnect(address, port);
		    Client client = CONNECTED_PLAYERS.get(address + ":" + port);
		    Main.getWorld().deleteEntity(client.getUuid());
		    CONNECTED_PLAYERS.remove(address + ":" + port);
		    System.out.println("Disconnected " + client + ". " + e.getMessage() + ".");
		    Network.broadcastMessage("[!] " + client.getName() + " has disconencted.");
		}
	    });
	    server.setUDPRecieveCallback((address, port, data) -> {
		Receiver.receive(address, -1, new Batch(data), null);
	    });
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public static void start() {
	System.out.printf("Starting server on %s:%s.%n", server.getAddress(), server.port);
	server.start(true, true);
    }

    public static boolean hasPlayer(String uuid) {
	return CONNECTED_PLAYERS.values().stream().filter(player -> player.getUuid().equals(uuid)).findFirst().isPresent();
    }

    public static Client getClient(String uuid) {
	return CONNECTED_PLAYERS.values().stream().filter(player -> player.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    public static void sendFast(Batch batch, String address, int port) throws UnknownHostException, IOException {
	server.sendUDP(batch.toBytes(), address, port);
    }

    public static void sendSecure(Batch batch, OutputStream outputStream) throws IOException {
	outputStream.write(batch.toBytes());
    }

    public static void broadcastFast(Batch batch, Client... ignored) {
	for (Client client : CONNECTED_PLAYERS.values()) {
	    boolean send = true;
	    for (Client ignore : ignored) {
		if (ignore == client) {
		    send = false;
		    break;
		}
	    }
	    if (send) {
		client.sendFast(batch);
	    }
	}
    }

    public static void broadcastSecure(Batch batch, Client... ignored) {
	for (Client client : CONNECTED_PLAYERS.values()) {
	    boolean send = true;
	    for (Client ignore : ignored) {
		if (ignore == client) {
		    send = false;
		    break;
		}
	    }
	    if (send) {
		client.sendSecure(batch);
	    }
	}
    }

    public static void broadcastMessage(String message) {
	Batch messageBatch = new Batch("message");
	messageBatch.add("message", message);
	Network.broadcastSecure(messageBatch);
    }

}
