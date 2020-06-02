package com.jeremy.electroserver.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import com.jeremy.electroserver.PlayerClient;
import com.jeremy.networking.Server;
import com.sineshore.serialization.Batch;

public class NetworkServer extends Server {

	public static final int PORT = 37427;

	public HashMap<String, PlayerClient> registered = new HashMap<>();

	public NetworkServer() throws IOException {
		super(PORT);
	}

	@Override
	protected void onReceiveClient(String address, int port, InputStream inputStream, OutputStream outputStream) {
		while (isConnected(address, port)) {
			try {
				Receiver.receive(outputStream, new Batch(inputStream), address, port);
			} catch (IOException exception) {
				exception.printStackTrace();
				disconnect(address, port);
			}
		}
	}

	public void broadcast(Batch batch, PlayerClient... blacklist) {
		registered.values().forEach(client -> {
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
