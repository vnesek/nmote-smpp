/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.ems;

/**
 * TextFormatting style.
 */
public final class Style {

	public static final int LEFT = 0;
	public static final int CENTER = 1;
	public static final int RIGHT = 2;
	private static final int MASK_ALIGNEMENT = 3;

	public static final int NORMAL = 0 << 2;
	public static final int LARGE = 1 << 2;
	public static final int SMALL = 2 << 2;
	private static final int MASK_SIZE = 3 << 2;

	public static final int BOLD = 1 << 4;
	public static final int ITALIC = 1 << 5;
	public static final int UNDERLINED = 1 << 6;
	public static final int STRIKETHROUGH = 1 << 7;

	public static int getAlignment(int style) {
		return style & MASK_ALIGNEMENT;
	}

	public static int getSize(int style) {
		return style & MASK_SIZE;
	}

	public static boolean isBold(int style) {
		return (style & BOLD) != 0;
	}

	public static boolean isItalic(int style) {
		return (style & ITALIC) != 0;
	}

	public static boolean isStrikethrough(int style) {
		return (style & STRIKETHROUGH) != 0;
	}

	public static boolean isUnderlined(int style) {
		return (style & UNDERLINED) != 0;
	}

	public static int setAlignment(int style, int align) {
		return setStyleHelper(style, align, MASK_ALIGNEMENT);
	}

	public static int setBold(int style, boolean on) {
		return setStyleHelper(style, on ? BOLD : 0, BOLD);
	}

	public static int setItalic(int style, boolean on) {
		return setStyleHelper(style, on ? ITALIC : 0, ITALIC);
	}

	public static int setSize(int style, int size) {
		return setStyleHelper(style, size, MASK_SIZE);
	}

	public static int setStrikethrough(int style, boolean on) {
		return setStyleHelper(style, on ? STRIKETHROUGH : 0, STRIKETHROUGH);
	}

	public static int setUnderlined(int style, boolean on) {
		return setStyleHelper(style, on ? UNDERLINED : 0, UNDERLINED);
	}

	private static int setStyleHelper(int style, int value, int mask) {
		return (style & ~mask) | value;
	}
}