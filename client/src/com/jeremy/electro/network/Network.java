package com.jeremy.electro.network;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.sineshore.networking.TCPClient;
import com.sineshore.networking.UDPClient;
import com.sineshore.serialization.Batch;

public class Network {

	private static TCPClient tcpClient;
	private static UDPClient udpClient;

	public static void init() {
		tcpClient = new TCPClient();
		udpClient = new UDPClient();

		tcpClient.setReceiver((input) -> {
			try {
				Receiver.receive(new Batch(input));
			} catch (IOException e) {
				System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
				try {
					tcpClient.stop();
				} catch (IOException e1) {
					System.out.println(e1.getClass().getSimpleName() + ": " + e1.getMessage());
				}
			}
		});

		udpClient.setReciever((data) -> {
			Receiver.receive(new Batch(data));
		});
	}

	public static void start(String address, int port) {
		new Thread(() -> {
			try {
				tcpClient.connect(address, port);
			} catch (IOException e) {
				System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
			}
		}, "tcp").start();

		new Thread(() -> {
			try {
				udpClient.connect(address, port);
			} catch (UnknownHostException | SocketException e) {
				e.printStackTrace();
			}
		}, "udp").start();
	}

	public static void runIfConnected(Runnable success, Runnable failure) {
		new Thread(() -> {
			long start = System.currentTimeMillis();
			while (System.currentTimeMillis() - start < 5000) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (isConnected()) {
					success.run();
					break;
				}
			}
			failure.run();
		}, "run-if-connected").start();
	}

	public static boolean isConnected() {
		return tcpClient.isActive();
	}

	public static int getUdpPort() {
		return udpClient.getPort();
	}

	public static void sendFast(Batch batch) {
		try {
			udpClient.send(batch.toBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void sendSecure(Batch batch) {
		try {
			tcpClient.send(batch.toBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
