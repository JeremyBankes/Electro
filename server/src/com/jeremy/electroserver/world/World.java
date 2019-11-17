package com.jeremy.electroserver.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.jeremy.electroserver.world.entity.Entity;
import com.sineshore.serialization.Batch;

public class World {

    private final String name;

    private HashSet<Entity> toSpawn;
    private HashMap<String, Entity> entities;
    private HashSet<Entity> toDie;

    public World(String name) {
	this.name = name;
	toSpawn = new HashSet<>();
	entities = new HashMap<>();
	toDie = new HashSet<>();
    }

    public void tick() {	
	toSpawn.forEach(entity -> {
	    entity.onSpawn(this);
	    entities.put(entity.getUuid(), entity);
	});
	toSpawn.clear();
	entities.values().forEach(entity -> {
	    entity.tick();
	    if (!entity.isAlive()) {
		toDie.add(entity);
	    }
	});
	toDie.forEach(entity -> {
	    entity.onDeath(this);
	    entities.remove(entity.getUuid());
	});
	toDie.clear();
    }

    public Batch getWorldBatch() {
	Batch worldBatch = new Batch("world");
	worldBatch.add("name", name);
	return worldBatch;
    }

    public void spawnEntity(Entity entity) {
	toSpawn.add(entity);
    }

    public void deleteEntity(Entity entity) {
	toDie.add(entity);
    }

    public void deleteEntity(String uuid) {
	if (isEntity(uuid)) {
	    deleteEntity(entities.get(uuid));
	}
    }

    public boolean isEntity(String uuid) {
	return entities.containsKey(uuid);
    }

    public Entity getEntity(String uuid) {
	return entities.get(uuid);
    }

    public ArrayList<Entity> getEntities() {
	return new ArrayList<Entity>(entities.values());
    }

    public String getName() {
	return name;
    }

}
