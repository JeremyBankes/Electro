package com.jeremy.electro.state.world;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.jeremy.electro.MainClient;
import com.jeremy.electro.entity.Entity;

public class World {

	private String name;

	private HashSet<Entity> toSpawn;
	private HashMap<String, Entity> entities;
	private HashSet<Entity> toDie;

	private BufferedImage background;

	public World(String name) {
		toSpawn = new HashSet<>();
		entities = new HashMap<>();
		toDie = new HashSet<>();
		this.name = name;

		try {
			background = ImageIO.read(MainClient.class.getResourceAsStream("/background.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void tick() {
		toSpawn.forEach(entity -> entities.put(entity.uuid, entity));
		toSpawn.clear();
		entities.values().forEach(entity -> entity.tick());
		toDie.forEach(entity -> entities.remove(entity.uuid));
		toDie.clear();
	}

	public void render(Graphics2D g) {
		g.drawImage(background, 0, 0, null);
		entities.values().forEach(entity -> entity.render(g));
	}

	public List<Entity> getCollidedEntities(Rectangle rectangle) {
		return entities.values().stream().filter(entity -> rectangle.intersects(entity.x, entity.y, entity.width, entity.height))
				.collect(Collectors.toList());
	}

	public void spawnEntity(Entity entity) {
		toSpawn.add(entity);
	}

	public void deleteEntity(Entity entity) {
		toDie.add(entity);
	}

	public void deleteEntity(String uuid) {
		toDie.add(getEntity(uuid));
	}

	public Entity getEntity(String uuid) {
		return entities.get(uuid);
	}

	public boolean hasEntity(String uuid) {
		return entities.containsKey(uuid);
	}

	@Deprecated
	public HashMap<String, Entity> getEntities() {
		return entities;
	}

	public String getName() {
		return name;
	}

}
