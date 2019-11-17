package com.jeremy.electroserver.world;

import com.jeremy.electroserver.world.entity.Entity;

public class Physics {

	public static void doGravity(Entity entity) {
		// entity.addVelocity(0, 0.25f);
	}

	public static void doMovement(Entity entity) {
		entity.addPosition(entity.getXVelocity(), entity.getYVelocity());
	}

}
