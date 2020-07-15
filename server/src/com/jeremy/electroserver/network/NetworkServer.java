package com.jeremy.electroserver.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.jeremy.electroserver.PlayerClient;
import com.jeremy.electroserver.ServerMain;
import com.jeremy.networking.Endpoint;
import com.jeremy.networking.TCPServer;
import com.sineshore.serialization.Batch;

public class NetworkServer extends TCPServer {

	public static final int PORT = 37427;

	public HashMap<String, PlayerClient> registered = new HashMap<>();

	public NetworkServer() throws IOException {
		super(PORT);
	}

	@Override
	protected void onReceiveClient(Endpoint endpoint, InputStream inputStream, OutputStream outputStream) {
		while (isConnected(endpoint)) {
			try {
				Receiver.receive(outputStream, new Batch(inputStream), endpoint);
			} catch (IOException exception) {
				try {
					disconnect(endpoint);
				} catch (IOException disconnect) {
					disconnect.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void onDisconnect(Endpoint endpoint) {
		registered.entrySet().removeIf(entry -> {
			PlayerClient client = entry.getValue();
			if (client.endpoint.equals(endpoint)) {
				System.out.printf("Player disconnected %s.%n", client);
				ServerMain.networkServer.broadcastMessage("[!] '" + client.getName() + "' has left the game!");
				ServerMain.getWorld().deleteEntity(client.getUuid());
				return true;
			}
			return false;
		});
	}

	public void broadcast(Batch batch, PlayerClient... blacklist) {
		new ArrayList<PlayerClient>(registered.values()).forEach(client -> {
			if (!isBlacklisted(blacklist, client)) client.send(batch);
		});
	}

	private static boolean isBlacklisted(PlayerClient[] blacklist, PlayerClient client) {
		for (PlayerClient blackedlistedClient : blacklist) if (client == blackedlistedClient) return true;
		return false;
	}

	public PlayerClient getRegistered(String uuid) {
		return registered.get(uuid);
	}

	public void broadcastMessage(String message) {
		Batch messageBatch = new Batch("message");
		messageBatch.add("message", message);
		broadcast(messageBatch);
	}

	@Override
	protected void onException(Exception exception) {
		super.onException(exception);
	}

}
