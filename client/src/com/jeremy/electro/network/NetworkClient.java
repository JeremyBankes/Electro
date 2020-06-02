package com.jeremy.electro.network;

import java.io.IOException;
import java.io.InputStream;

import com.jeremy.networking.Client;
import com.sineshore.serialization.Batch;

public class NetworkClient extends Client {

	@Override
	protected void onServerContact(InputStream inputStream) {
		try {
			while (isConnected()) {
				Receiver.receive(new Batch(inputStream));
			}
		} catch (IOException exception) {
			exception.printStackTrace();
			try {
				disconnect();
			} catch (IOException disconnect) {
				disconnect.printStackTrace();
			}
		}
	}

	public void send(Batch batch) {
		try {
			send(batch.toBytes());
		} catch (IOException exception) {
			exception.printStackTrace();
			try {
				disconnect();
			} catch (IOException disconnect) {
				disconnect.printStackTrace();
			}
		}
	}

	@Override
	protected void onException(Exception exception) {
		exception.printStackTrace();
	}

}
