package com.jeremy.electro;

import java.awt.Rectangle;
import java.util.List;
import java.util.UUID;

import com.jeremy.electro.audio.Sound;
import com.jeremy.electro.entity.Character;
import com.jeremy.electro.entity.Entity;
import com.jeremy.electro.input.Input;
import com.jeremy.electro.network.Network;
import com.jeremy.electro.state.State;
import com.sineshore.serialization.Batch;
import com.sineshore.utilities.Utilities;

public class Player {

    public static final String uuid = UUID.randomUUID().toString();
    public static final float HAND_DISTANCE = 25;
    public static final float HAND_EXTENDED = 35;
    public static final int HAND_SIZE = 10;
    private static final float ACCELERATION = 0.5f;
    private static final float DECELERATION = 0.5f;
    private static final float MAX_VELOCITY = 5.0f;

    public static String name;

    public static Character character;
    public static float xVelocity, yVelocity;
    public static float handDistance = HAND_DISTANCE;

    public static boolean click;
    public static float health, maxHealth;
    public static boolean alive;

    private static int footstepTimer;
    private static boolean footstep;

    public static void tick() {
	if (State.GAME_STATE.getWorld() == null) {
	    return;
	}
	if (character == null) {
	    return;
	}

	if (Input.isKeyPressed(Input.KEY_UP, Input.KEY_W)) {
	    if (yVelocity > -MAX_VELOCITY) {
		yVelocity -= ACCELERATION;
		if (yVelocity < -MAX_VELOCITY) {
		    yVelocity = -MAX_VELOCITY;
		}
	    }
	} else if (yVelocity < 0) {
	    yVelocity += DECELERATION;
	    if (yVelocity > 0) {
		yVelocity = 0;
	    }
	}

	if (Input.isKeyPressed(Input.KEY_DOWN, Input.KEY_S)) {
	    if (yVelocity < MAX_VELOCITY) {
		yVelocity += ACCELERATION;
		if (yVelocity > MAX_VELOCITY) {
		    yVelocity = MAX_VELOCITY;
		}
	    }
	} else if (yVelocity > 0) {
	    yVelocity -= DECELERATION;
	    if (yVelocity < 0) {
		yVelocity = 0;
	    }
	}

	if (Input.isKeyPressed(Input.KEY_LEFT, Input.KEY_A)) {
	    if (xVelocity > -MAX_VELOCITY) {
		xVelocity -= ACCELERATION;
		if (xVelocity < -MAX_VELOCITY) {
		    xVelocity = -MAX_VELOCITY;
		}
	    }
	} else if (xVelocity < 0) {
	    xVelocity += DECELERATION;
	    if (xVelocity > 0) {
		xVelocity = 0;
	    }
	}

	if (Input.isKeyPressed(Input.KEY_RIGHT, Input.KEY_D)) {
	    if (xVelocity < MAX_VELOCITY) {
		xVelocity += ACCELERATION;
		if (xVelocity > MAX_VELOCITY) {
		    xVelocity = MAX_VELOCITY;
		}
	    }
	} else if (xVelocity > 0) {
	    xVelocity -= DECELERATION;
	    if (xVelocity < 0) {
		xVelocity = 0;
	    }
	}

	boolean movementKeyDown = Input.isKeyPressed(Input.KEY_UP, Input.KEY_DOWN, Input.KEY_RIGHT, Input.KEY_LEFT, Input.KEY_W, Input.KEY_S,
		Input.KEY_A, Input.KEY_D);
	if (movementKeyDown) {
	    if (footstepTimer == 0) {
		footstep = true;
	    }
	    footstepTimer++;
	    if (footstepTimer > 15) {
		footstepTimer = 0;
	    }
	} else {
	    footstepTimer = 0;
	}

	if (footstep && (xVelocity != 0 || yVelocity != 0)) {
	    Sound.playPublicSound(character.x, character.y, "footstep1", "footstep2", "footstep3");
	    footstep = false;
	}

	double theta = Math.atan2(Input.CURSOR.y - (character.y + character.height / 2), Input.CURSOR.x - (character.x + character.width / 2));
	theta += theta < 0 ? Math.PI * 2 : 0;

	if (Input.isButtonPressed(Input.MOUSE_LEFT)) {
	    if (!click) {
		click = true;
		Sound.playPublicSound(character.x, character.y, "swing1", "swing2", "swing3");
	    }
	    if (handDistance < HAND_EXTENDED) {
		if (handDistance < HAND_EXTENDED) {
		    handDistance *= 1.2;
		}
		if (handDistance > HAND_EXTENDED) {
		    handDistance = HAND_EXTENDED;
		    List<Entity> entities = State.GAME_STATE.getWorld()
			    .getCollidedEntities(new Rectangle((int) character.getHandX(), (int) character.getHandY(), HAND_SIZE, HAND_SIZE));
		    for (Entity vic : entities) {
			if (vic instanceof Character && vic != character) {
			    Sound.playPublicSound(character.x, character.y, "hurt1", "hurt2", "hurt3");
			    Batch harmBatch = new Batch("harm");
			    harmBatch.add("uuid", uuid);
			    harmBatch.add("victim", vic.uuid.toString());
			    harmBatch.add("damage", (float) (Utilities.distance(character.x, character.y, vic.x, vic.y) / 2f + 5f));
			    harmBatch.add("xv", (float) ((vic.x + vic.width / 2) - (character.x + character.width / 2 + HAND_SIZE / 2)) / 3);
			    harmBatch.add("yv", (float) ((vic.y + vic.height / 2) - (character.y + character.height / 2 + HAND_SIZE / 2)) / 3);
			    Network.sendFast(harmBatch);
			    break;
			}
		    }
		}
	    }
	} else {
	    click = false;
	    if (handDistance > HAND_DISTANCE) {
		handDistance /= 1.1;
		if (handDistance < HAND_DISTANCE) {
		    handDistance = HAND_DISTANCE;
		}
	    }
	}

	character.setHandX((float) (character.x + character.width / 2 - HAND_SIZE / 2 + Math.cos(theta) * handDistance));
	character.setHandY((float) (character.y + character.height / 2 - HAND_SIZE / 2 + Math.sin(theta) * handDistance));

	character.x += xVelocity;
	character.y += yVelocity;

	character.x = Math.min(Main.WIDTH - character.width, Math.max(0, character.x));
	character.y = Math.min(Main.HEIGHT - character.height, Math.max(0, character.y));

	Batch updateBatch = new Batch("update");
	updateBatch.add("uuid", uuid.toString());
	updateBatch.add("x", character.x);
	updateBatch.add("y", character.y);
	updateBatch.add("hx", character.getHandX());
	updateBatch.add("hy", character.getHandY());
	Network.sendFast(updateBatch);
    }

}
