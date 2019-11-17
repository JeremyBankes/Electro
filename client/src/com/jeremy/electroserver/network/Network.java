package com.jeremy.electroserver.network;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;

import com.jeremy.electroserver.Client;
import com.jeremy.electroserver.Main;
import com.jeremy.networking.v1_0.TcpServer;
import com.jeremy.networking.v1_0.UdpServer;
import com.jeremy.serialization.v3_0.Bundle;

public class Network {

	public static final int PORT = 37427;

	protected static final HashMap<String, Client> CONNECTED_PLAYERS = new HashMap<>();

	private static TcpServer tcpServer;
	private static UdpServer udpServer;

	public static void init() {
		tcpServer = new TcpServer();
		tcpServer.setReceiveCallback((address, bundle) -> {
			try {
				Receiver.receive(address, bundle);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		});

		tcpServer.setDisconnectCallback((address, exception) -> {
			Client client = CONNECTED_PLAYERS.get(address);
			Main.getWorld().deleteEntity(client.getUuid());
			CONNECTED_PLAYERS.remove(address);
			System.out.println("Disconnected " + client + ". " + ((SocketException) exception).getMessage() + ".");
			Network.broadcastMessage("[!] " + client.getName() + " has disconencted.");
		});

		udpServer = new UdpServer();
		udpServer.setReceiveCallback((address, bundle) -> {
			System.out.println("Receive UDP: " + bundle);
			Receiver.receive(address, bundle);
		});
	}

	public static void start() {
		System.out.printf("Starting server on port %s.%n", PORT);
		try {
			tcpServer.start(PORT);
			udpServer.start(PORT);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public static boolean hasPlayer(String uuid) {
		return CONNECTED_PLAYERS.values().stream().filter(player -> player.getUuid().equals(uuid)).findFirst().isPresent();
	}

	public static Client getClient(String uuid) {
		return CONNECTED_PLAYERS.values().stream().filter(player -> player.getUuid().equals(uuid)).findFirst().orElse(null);
	}

	public static boolean isClient(String address) {
		return tcpServer.isClient(address);
	}

	public static void sendFast(String address, Bundle batch) throws UnknownHostException, IOException {
		udpServer.send(address, batch);
	}

	public static void sendSecure(String address, Bundle batch) throws IOException {
		tcpServer.send(address, batch);
	}

	public static void broadcastFast(Bundle batch, Client... ignored) {
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

	public static void broadcastSecure(Bundle batch, Client... ignored) {
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
		Bundle messageBatch = new Bundle("message");
		messageBatch.put("message", message);
		Network.broadcastSecure(messageBatch);
	}

}
