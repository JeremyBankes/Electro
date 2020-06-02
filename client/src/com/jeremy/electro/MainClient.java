package com.jeremy.electro;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.DefaultCaret;

import com.jeremy.electro.entity.Player;
import com.jeremy.electro.input.Input;
import com.jeremy.electro.network.NetworkClient;
import com.jeremy.electro.state.State;
import com.sineshore.serialization.Batch;

public class MainClient {

	public static final int TPS = 60;
	public static final int WIDTH = 900;
	public static final int HEIGHT = 600;
	public static int maxFps = 100;

	private static JFrame frame;
	private static JPanel panel;
	private static JTextArea dialog;
	private static JTextField input;
	public static Canvas canvas;

	private static BufferStrategy strategy;

	private static Thread thread;
	private static boolean running;
	public static int age;

	private static boolean waitingOnInput;

	public static NetworkClient networkClient;

	public static Font font = new Font("Consolas", Font.BOLD, 16);
	public static final Color HACKER_GREEN = new Color(0x04b700);

	public static void main(String[] args) {
		frame = new JFrame("Electro");
		panel = new JPanel(new BorderLayout(0, 0), true);

		try {
			frame.setIconImage(ImageIO.read(MainClient.class.getResourceAsStream("/icon.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		canvas.setBackground(Color.BLACK);

		JPanel chatPanel = new JPanel(new BorderLayout(0, 0), true);
		chatPanel.setPreferredSize(new Dimension(300, HEIGHT));
		chatPanel.setBackground(Color.WHITE);

		dialog = new JTextArea();
		input = new JTextField();
		JScrollPane scollPane = new JScrollPane(dialog);
		scollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		JScrollBar scrollbar = new JScrollBar(Adjustable.VERTICAL);
		scrollbar.setUI(new BasicScrollBarUI() {
			@Override
			protected JButton createIncreaseButton(int orientation) {
				JButton button = new JButton();
				button.setPreferredSize(new Dimension(0, 0));
				return button;
			}

			@Override
			protected JButton createDecreaseButton(int orientation) {
				JButton button = new JButton();
				button.setPreferredSize(new Dimension(0, 0));
				return button;
			}

			@Override
			protected void paintTrack(Graphics g, JComponent c, Rectangle bounds) {
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, bounds.width, bounds.height);
			}

			@Override
			protected void paintThumb(Graphics g, JComponent c, Rectangle bounds) {
				g.setColor(new Color(0x1c1c1c));
				g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
			}
		});
		scollPane.setVerticalScrollBar(scrollbar);
		scollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		dialog.setEditable(false);
		dialog.setLineWrap(true);
		dialog.setWrapStyleWord(true);
		dialog.setFont(font);
		input.setFont(font);
		input.setBorder(new CompoundBorder(new MatteBorder(2, 0, 0, 2, new Color(0x383838)), new EmptyBorder(5, 5, 5, 5)));
		dialog.setBorder(new EmptyBorder(0, 5, 0, 5));

		input.setBackground(Color.BLACK);
		input.setForeground(HACKER_GREEN);
		input.setCaretColor(HACKER_GREEN);
		input.setSelectedTextColor(Color.BLACK);
		input.setSelectionColor(HACKER_GREEN);
		dialog.setBackground(Color.BLACK);
		dialog.setForeground(HACKER_GREEN);
		dialog.setCaretColor(HACKER_GREEN);
		dialog.setSelectedTextColor(Color.BLACK);
		dialog.setSelectionColor(HACKER_GREEN);
		((DefaultCaret) dialog.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		chatPanel.add(scollPane, BorderLayout.CENTER);
		chatPanel.add(input, BorderLayout.SOUTH);

		input.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (waitingOnInput) {
					waitingOnInput = false;
					return;
				}
				if (input.getText().startsWith("/")) {
					if (input.getText().equalsIgnoreCase("/respawn")) {
						if (Player.alive) {
							MainClient.sendMessage("[!] You are already alive.");
						} else {
							new Timer().schedule(new TimerTask() {
								int i = 10;

								@Override
								public void run() {
									if (i <= 0) {
										Batch respawnBatch = new Batch("respawn");
										respawnBatch.add("uuid", Player.uuid);
										MainClient.networkClient.send(respawnBatch);
										this.cancel();
									} else {
										MainClient.sendMessage("[!] Respawning in " + i + "...");
									}
									i--;
								}
							}, 0, 1000);
						}
					} else if (input.getText().equalsIgnoreCase("/die")) {
						Batch harmBatch = new Batch("harm");
						harmBatch.add("uuid", Player.uuid.toString());
						harmBatch.add("victim", Player.uuid.toString());
						harmBatch.add("damage", Player.maxHealth);
						harmBatch.add("xv", 0f);
						harmBatch.add("yv", 0f);
						MainClient.networkClient.send(harmBatch);
					} else if (input.getText().equalsIgnoreCase("/exit")) {
						State.currentState = State.LOBBY_STATE;
					} else {
						sendMessage("[!] Command Help:");
						sendMessage("[!]   /Respawn - Revives you!");
						sendMessage("[!]   /Die     - Kills yourself");
					}
				} else {
					if (!networkClient.isConnected()) {
						sendMessage("[!] Please connect to the game before attempting to chat.\n");
					} else {
						Batch messageBatch = new Batch("message");
						messageBatch.add("uuid", Player.uuid);
						messageBatch.add("message", input.getText());
						MainClient.networkClient.send(messageBatch);
					}
				}
				input.setText("");
			}
		});

		panel.add(canvas, BorderLayout.CENTER);
		panel.add(chatPanel, BorderLayout.WEST);

		thread = new Thread(MainClient::run, "main");

		frame.setContentPane(panel);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		setInputEnabled(false);

		start();
	}

	private static void run() {
		networkClient = new NetworkClient();
		Input.init();

		System.out.println("Your UUID: " + Player.uuid);
		sendMessage("   Welcome to Electro, A simple game written from scratch in Java over 4 days for Electro-Tech 11.\n\nHave Fun!\n   ~ Jeremy");

		long currentTime;
		final long second = 1000000000;
		long tickTime = second / TPS;
		long tickTimer = System.nanoTime();
		long secondTimer = System.nanoTime();
		long frameTime = maxFps == 0 ? 0 : second / maxFps;
		long frameTimer = System.nanoTime();
		while (running) {
			currentTime = System.nanoTime();
			if (currentTime - tickTimer > tickTime) {
				if (currentTime - tickTimer > tickTime * 2) {
					tickTimer = currentTime;
				}
				tickTimer += tickTime;
				tick();
			}
			if (currentTime - frameTimer > frameTime) {
				frameTimer += frameTime;
				render();
			}
			if (currentTime - secondTimer > second) {
				secondTimer += second;
			}
		}
	}

	public static void tick() {
		State.currentState.tick();
		age++;
	}

	public static void render() {
		if (strategy == null) {
			canvas.createBufferStrategy(2);
			strategy = canvas.getBufferStrategy();
		}
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		g.setRenderingHints((Map<?, ?>) Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints"));
		g.setFont(MainClient.font);
		g.setColor(canvas.getBackground());
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		State.currentState.render(g);
		g.dispose();
		strategy.show();
	}

	public static void start() {
		running = true;
		thread.start();
	}

	public static void stop() {
		running = false;
	}

	public static void sendMessage(String message) {
		dialog.setText(dialog.getText() + message + "\n");
	}

	public static String getInput() {
		waitingOnInput = true;
		while (waitingOnInput) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		String text = input.getText();
		input.setText("");
		return text;
	}

	public static void setInput(String text) {
		input.setText(text);
	}

	public static void setInputEnabled(boolean enabled) {
		input.setEnabled(enabled);
	}

	public static void clearDialog() {
		dialog.setText("");
	}

	public static void focusChat() {
		input.requestFocus();
	}

}
