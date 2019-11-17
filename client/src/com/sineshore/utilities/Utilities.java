package com.sineshore.utilities;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Utilities {

    public static final char[] WHITESPACE_CHARACTERS = { ' ', '_' };

    // Text

    public static final String join(String seperator, Object... objects) {
	CharSequence[] sequences = new CharSequence[objects.length];
	for (int i = 0; i < objects.length; i++) {
	    sequences[i] = objects[i] == null ? "null" : objects[i].toString();
	}
	return String.join(seperator, sequences);
    }

    public static final String join(Object... objects) {
	return join(null, objects);
    }

    public static final String beautify(String message) {
	char[] characters = message.toLowerCase().replaceAll(" +", " ").toCharArray();
	characters[0] = Character.toUpperCase(characters[0]);
	for (int i = 0, len = characters.length; i < len; i++)
	    if (i != 0)
		if (arrayContains(WHITESPACE_CHARACTERS, characters[i - 1]))
		    characters[i] = Character.toUpperCase(characters[i]);
	return new String(characters);
    }

    public static final String beautify(Enum<?> enumeration) {
	return beautify(enumeration.name().replace('_', ' '));
    }

    // Number

    public static final boolean isShort(Object object) {
	try {
	    Short.parseShort(object.toString());
	    return true;
	} catch (NumberFormatException e) {
	    return false;
	}
    }

    public static final boolean isInt(Object object) {
	try {
	    Integer.parseInt(object.toString());
	    return true;
	} catch (NumberFormatException e) {
	    return false;
	}
    }

    public static final boolean isLong(Object object) {
	try {
	    Long.parseLong(object.toString());
	    return true;
	} catch (NumberFormatException e) {
	    return false;
	}
    }

    public static final boolean isFloat(Object object) {
	try {
	    Float.parseFloat(object.toString());
	    return true;
	} catch (NumberFormatException e) {
	    return false;
	}
    }

    public static final boolean isDouble(Object object) {
	try {
	    Double.parseDouble(object.toString());
	    return true;
	} catch (NumberFormatException e) {
	    return false;
	}
    }

    public static final short asShort(Object object) {
	return isShort(object) ? Short.parseShort(object.toString()) : -1;
    }

    public static final int asInt(Object object) {
	return isInt(object) ? Integer.parseInt(object.toString()) : -1;
    }

    public static final long asLong(Object object) {
	return isLong(object) ? Long.parseLong(object.toString()) : -1;
    }

    public static final float asFloat(Object object) {
	return isFloat(object) ? Float.parseFloat(object.toString()) : -1;
    }

    public static final double asDouble(Object object) {
	return isDouble(object) ? Double.parseDouble(object.toString()) : -1;
    }

    public static final double clamp(double min, double max, double value) {
	return Math.min(max, Math.max(min, value));
    }

    public static final int clamp(int min, int max, int value) {
	return Math.min(max, Math.max(min, value));
    }

    public static final double max(double a, double b) {
	return a > b ? a : b;
    }

    public static final double min(double a, double b) {
	return a > b ? b : a;
    }

    public static final int max(int a, int b) {
	return a > b ? a : b;
    }

    public static final int min(int a, int b) {
	return a > b ? b : a;
    }

    public static final boolean inBoundary(double value, double start, double range, boolean equals) {
	if (equals)
	    return value >= start && value <= start + range;
	return value > start & value < start + range;
    }

    public static final boolean inBoundary(double value, double start, double range) {
	return inBoundary(value, start, range, false);
    }

    public static final double distance(double x1, double y1, double x2, double y2) {
	return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    // Array

    public static final Object[] convert(ArrayList<Object> arrayList) {
	return arrayList.toArray();
    }

    public static final <T> ArrayList<T> convert(T[] array) {
	return new ArrayList<>(Arrays.asList(array));
    }

    public static final <T> boolean arrayContains(T[] array, T value) {
	for (T object : array)
	    if (object == value || object.equals(value))
		return true;
	return false;
    }

    public static final boolean arrayContains(char[] array, char value) {
	for (int object : array)
	    if (object == value)
		return true;
	return false;
    }

    public static final boolean arrayContains(int[] array, int value) {
	for (int object : array)
	    if (object == value)
		return true;
	return false;
    }

    public static final boolean arrayContains(float[] array, float value) {
	for (float object : array)
	    if (object == value)
		return true;
	return false;
    }

    public static final boolean arrayContains(double[] array, double value) {
	for (double object : array)
	    if (object == value)
		return true;
	return false;
    }

    // Random

    public static final Random RANDOM = new Random();

    public static final int randInt(int min, int max) {
	if (min > max) {
	    int temp = max;
	    max = min;
	    min = temp;
	}
	return min + RANDOM.nextInt(max - min + 1);
    }

    public static float randFloat(float min, float max) {
	if (min > max) {
	    float temp = max;
	    max = min;
	    min = temp;
	}
	return RANDOM.nextFloat() * (max - min) + min;
    }

    public static double randDouble(double min, double max) {
	if (min > max) {
	    double temp = max;
	    max = min;
	    min = temp;
	}
	return RANDOM.nextDouble() * (max - min) + min;
    }

    public static boolean randBool() {
	return RANDOM.nextBoolean();
    }

    public static final Color randColor(boolean alpha) {
	return new Color(RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat(), alpha ? RANDOM.nextFloat() : 1f);
    }

    // Color

    public static final int alpha(int color) {
	return (color >> 24) & 0xFF;
    }

    public static final int red(int color) {
	return (color >> 16) & 0xFF;
    }

    public static final int green(int color) {
	return (color >> 8) & 0xFF;
    }

    public static final int blue(int color) {
	return (color >> 0) & 0xFF;
    }

    public static final Color mix(Color source, float red, float green, float blue, float factor) {
	float r = asFloat(source.getRed()) / 255;
	float g = asFloat(source.getGreen()) / 255;
	float b = asFloat(source.getBlue()) / 255;

	r += (red - r) * factor;
	g += (green - g) * factor;
	b += (blue - b) * factor;

	r = (float) clamp(0, 1, r);
	g = (float) clamp(0, 1, g);
	b = (float) clamp(0, 1, b);

	return new Color(r, g, b);
    }

    public static final int mix(int source, int red, int green, int blue, float factor) {
	int r = red(source);
	int g = green(source);
	int b = blue(source);

	r += (red - r) * factor;
	g += (green - g) * factor;
	b += (blue - b) * factor;

	r = (int) clamp(0, 255, r);
	g = (int) clamp(0, 255, g);
	b = (int) clamp(0, 255, b);

	return b | (g << 8) | (r << 16);
    }

    public static final Color mix(Color source, Color blend, float factor) {
	return mix(source, (float) blend.getRed() / 255, (float) blend.getGreen() / 255, (float) blend.getBlue() / 255, factor);
    }

    public static final Color intensify(Color source, float factor) {
	return new Color(//
		clamp(0, 255, (int) (source.getRed() * factor)), //
		clamp(0, 255, (int) (source.getGreen() * factor)), //
		clamp(0, 255, (int) (source.getBlue() * factor)) //
	);
    }

    public static final Color invert(Color color) {
	return new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
    }

    public static final Color setOpacity(Color color, float opacity) {
	return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (opacity * 255));
    }

}
