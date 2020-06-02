package com.jeremy.electroserver.world.entity;

public abstract class Creature extends Entity {

	protected String name;
	protected float health;
	protected float maxHealth;
	protected float defence;

	public Creature(String uuid, String name, float maxHealth) {
		super(uuid);
		this.name = name;
		this.maxHealth = health = maxHealth;
	}

	@Override
	public void tick() {
		super.tick();
		if (y < 0) {
			harm(-y);
		}
		if (health <= 0) {
			alive = false;
		}
	}

	public void harm(float damage) {
		health -= damage - (damage * defence);
		if (health <= 0) {
			alive = false;
		}
	}

	public void heal(float health) {
		this.health += health;
	}

	public String getName() {
		return name;
	}

	public float getHealth() {
		return health;
	}

	public void setHealth(float health) {
		this.health = Math.min(maxHealth, health);
	}

	public float getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(float maxHealth) {
		this.maxHealth = maxHealth;
		if (health > maxHealth) {
			health = maxHealth;
		}
	}

}
