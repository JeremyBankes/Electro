package com.jeremy.electro.entity;

import static com.jeremy.electro.input.Input.*;
import static java.lang.Math.*;

import java.awt.Rectangle;
import java.util.UUID;

import com.jeremy.electro.ClientMain;
import com.jeremy.electro.audio.Sound;
import com.jeremy.electro.input.Input;
import com.jeremy.electro.state.State;
import com.jeremy.utilities.Vector2;
import com.sineshore.serialization.Batch;

public class Player {

	public static final String uuid = UUID.randomUUID().toString();
	private static final float MAX_SPEED = 5.0f;
	public static final float HAND_DISTANCE = 25.0f;
	public static final float HAND_MAX_EXTEND = 20.0f;
	public static final float ACCELERATION = 0.75f;
	public static final int HAND_SIZE = 10;
	public static final int SWING_LIFETIME = 20;
	public static final int SWING_APEX = 10;
	public static final int FOOTSTEP_INTERVAL = 20;
	public static final float KNOCKBACK = 15.0f;
	public static final float DASH_ENERGY = 0.15f;
	public static final float DASH_POWER = 10f;

	public static String name;

	public static Character character;
	public static Vector2 velocity = new Vector2();

	public static boolean dash;
	public static float health, maxHealth;
	public static float energy = 1.0f;
	public static boolean alive;

	private static Vector2 input = new Vector2();

	public static int swingTimer = -1;
	public static float handExtend;

	private static int footstepTimer = -1;

	public static void tick() {
		if (State.GAME_STATE.getWorld() == null || character == null) return;

		input.set(0.0f);
		if (isKeyPressed(KEY_UP, KEY_W)) input.y -= 1.0f;
		if (isKeyPressed(KEY_DOWN, KEY_S)) input.y += 1.0f;
		if (isKeyPressed(KEY_RIGHT, KEY_D)) input.x += 1.0f;
		if (isKeyPressed(KEY_LEFT, KEY_A)) input.x -= 1.0f;
		input.normalize();

		float acceleration = ACCELERATION;
		Vector2 want = input.scale(MAX_SPEED);
		if (Input.isButtonPressed(MOUSE_RIGHT) && !input.isZero()) {
			if (!dash) {
				if (energy > DASH_ENERGY) {
					want.scale(DASH_POWER);
					acceleration *= DASH_POWER;
					Sound.playPublicSound(character.position.x, character.position.y, "dash");
					energy -= DASH_ENERGY;
					dash = true;
				}
			}
		} else dash = false;

		if (velocity.x < want.x) {
			velocity.x += acceleration;
			if (velocity.x > want.x) velocity.x = want.x;
		} else if (velocity.x > want.x) {
			velocity.x -= acceleration;
			if (velocity.x < want.x) velocity.x = want.x;
		}
		if (velocity.y < want.y) {
			velocity.y += acceleration;
			if (velocity.y > want.y) velocity.y = want.y;
		} else if (velocity.y > want.y) {
			velocity.y -= acceleration;
			if (velocity.y < want.y) velocity.y = want.y;
		}

		character.position.add(velocity);
		character.position.x = Math.min(ClientMain.WIDTH - character.width, Math.max(0, character.position.x));
		character.position.y = Math.min(ClientMain.HEIGHT - character.height, Math.max(0, character.position.y));

		if (isButtonPressed(MOUSE_LEFT) && swingTimer == -1) {
			Sound.playPublicSound(character.position.x, character.position.y, "swing1", "swing2", "swing3");
			swingTimer = 0;
		}
		if (swingTimer != -1) {
			float progress = (float) (swingTimer >= SWING_LIFETIME ? 0 : swingTimer) / SWING_LIFETIME;
			handExtend = (float) (HAND_MAX_EXTEND * (log10(progress + 1.0 / 100.0) - 2.0 * progress + 2.0));
			if (swingTimer == SWING_APEX) {
				Rectangle handBounds = new Rectangle(round(character.hand.position.x), round(character.hand.position.y), HAND_SIZE, HAND_SIZE);
				State.GAME_STATE.getWorld().getCollidedEntities(handBounds).forEach(victim -> {
					if (!(victim instanceof Character)) return;
					if (victim == character) return;
					Sound.playPublicSound(character.position.x, character.position.y, "hurt1", "hurt2", "hurt3");
					Vector2 knockback = victim.position.clone().subtract(character.position).normalize().scale(KNOCKBACK);
					Batch harmBatch = new Batch("harm");
					harmBatch.add("uuid", uuid);
					harmBatch.add("victim", victim.uuid.toString());
					harmBatch.add("damage", 25.0f + (float) Math.random() * 10.0f);
					harmBatch.add("xv", knockback.x);
					harmBatch.add("yv", knockback.y);
					ClientMain.networkClient.send(harmBatch);
				});
			}
			if (swingTimer >= SWING_LIFETIME) swingTimer = -1;
			else swingTimer++;
		}

		if (footstepTimer == -1) {
			if (!input.isZero()) {
				footstepTimer = 0;
			}
		} else {
			if (!input.isZero()) {
				if (footstepTimer == 0) {
					Sound.playPublicSound(character.position.x, character.position.y, "footstep1", "footstep2", "footstep3");
				}
				if (footstepTimer < FOOTSTEP_INTERVAL) footstepTimer++;
				else footstepTimer = 0;
			} else {
				footstepTimer = -1;
			}
		}

		Vector2 center = character.position.clone().add(character.width / 2, character.height / 2);
		Vector2 handDirection = new Vector2(CURSOR.x, CURSOR.y).subtract(center).normalize();
		character.hand.position.set(center.add(handDirection.scale(HAND_DISTANCE + handExtend)));

		Batch updateBatch = new Batch("update");
		updateBatch.add("uuid", uuid.toString());
		updateBatch.add("x", character.position.x);
		updateBatch.add("y", character.position.y);
		updateBatch.add("hx", character.hand.position.x);
		updateBatch.add("hy", character.hand.position.y);
		ClientMain.networkClient.send(updateBatch);

		if (energy < 1.0f) {
			energy += 0.001f;
			if (energy > 1.0f) energy = 1.0f;
		}
	}

}
