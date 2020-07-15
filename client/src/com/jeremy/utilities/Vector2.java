package com.jeremy.utilities;

import static java.lang.Math.*;

public class Vector2 implements Cloneable {

	public float x, y;

	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vector2() {
		this(0, 0);
	}

	public Vector2 set(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public Vector2 set(Vector2 vector) {
		return set(vector.x, vector.y);
	}

	public Vector2 set(float value) {
		return set(value, value);
	}

	public boolean isZero() {
		return x == 0 && y == 0;
	}

	public Vector2 add(float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public Vector2 add(Vector2 vector) {
		return add(vector.x, vector.y);
	}

	public Vector2 subtract(float x, float y) {
		return add(-x, -y);
	}

	public Vector2 subtract(Vector2 vector) {
		return subtract(vector.x, vector.y);
	}

	public Vector2 scale(Vector2 vector) {
		x *= vector.x;
		y *= vector.y;
		return this;
	}

	public Vector2 scale(float scale) {
		x *= scale;
		y *= scale;
		return this;
	}

	public float magnitude() {
		return (float) sqrt(x * x + y * y);
	}

	public float angle(Vector2 vector) {
		return (float) (atan2(y, x) - atan2(vector.y, vector.x));
	}

	public float dot(float x, float y) {
		return this.x * x + this.y * y;
	}

	public float dot(Vector2 vector) {
		return dot(vector.x, vector.y);
	}

	public Vector2 inverse() {
		return scale(-1);
	}

	public Vector2 normalize() {
		float magnitude = magnitude();
		if (magnitude == 0) {
			x = y = 0;
			return this;
		}
		x /= magnitude;
		y /= magnitude;
		return this;
	}

	@Override
	public Vector2 clone() {
		return new Vector2(x, y);
	}

	@Override
	public String toString() {
		return "[x=" + x + ", y=" + y + "]";
	}

}
