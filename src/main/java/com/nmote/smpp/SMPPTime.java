/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Represents SMPP Time. Two variations of SMPP Time are possible, absolute and
 * relative.
 */
public final class SMPPTime implements Serializable, Cloneable {

	private static final long serialVersionUID = About.SERIAL_VERSION_UID;

	private static final int DUR_SECOND = 1000;

	private static final int DUR_MINUTE = DUR_SECOND * 600;

	private static final int DUR_HOUR = DUR_MINUTE * 60;

	private static final int DUR_DAY = DUR_HOUR * 24;

	private static final int DUR_MONTH = DUR_DAY * 31;

	private static final int DUR_YEAR = DUR_DAY * 365;

	private static TimeZone UTC = TimeZone.getTimeZone("UTC");

	private static void addTimeField(Calendar c, int field, String t, int start, int len) {
		c.add(field, Integer.parseInt(t.substring(start, len)));
	}

	private static void append(char[] buffer, int start, int end, long value) {
		for (int i = end; i >= start; --i) {
			buffer[i] = (char) ('0' + value % 10);
			value /= 10;
		}
	}

	private static void setTimeField(Calendar c, int field, String t, int start, int len, int delta) {
		c.set(field, Integer.parseInt(t.substring(start, len)) + delta);
	}

	/**
	 * Creates new relative SMPPTime instance.
	 *
	 * @param year
	 *            Year
	 * @param month
	 *            Month
	 * @param day
	 *            Day
	 * @param hour
	 *            Hour
	 * @param minute
	 *            Minute
	 * @param second
	 *            Second
	 */
	public SMPPTime(int year, int month, int day, int hour, int minute, int second) {
		time = DUR_YEAR * year + DUR_MONTH * month + DUR_DAY * day + DUR_HOUR * hour + DUR_MINUTE * minute + DUR_SECOND
				* second;
	}

	/**
	 * Creates a new absolute SMPPTime instance.
	 *
	 * @param time
	 *            in miliseconds since Epoch (absolute)
	 */
	public SMPPTime(long time) {
		this.time = time;
	}

	/**
	 * Creates a new SMPPTime instance. If absolute is true instance will be a
	 * absolute, otherwise it will be relative.
	 *
	 * @param time
	 *            in miliseconds since Epoch (absolute) or milliseconds relative
	 *            to current time if relative.
	 * @param absolute
	 *            true if absolute time instance.
	 */
	public SMPPTime(long time, boolean absolute) {
		if (absolute) {
			this.time = time;
		} else {
			this.time = -time;
		}
	}

	public SMPPTime(String t) {
		Calendar c = new GregorianCalendar();
		c.setTimeZone(UTC);
		if (t.charAt(15) == 'R') {
			// Relative time
			c.setTimeInMillis(System.currentTimeMillis());
			addTimeField(c, Calendar.YEAR, t, 0, 2);
			addTimeField(c, Calendar.MONTH, t, 2, 4);
			addTimeField(c, Calendar.DAY_OF_MONTH, t, 4, 6);
			addTimeField(c, Calendar.HOUR_OF_DAY, t, 6, 8);
			addTimeField(c, Calendar.MINUTE, t, 8, 10);
			addTimeField(c, Calendar.SECOND, t, 10, 12);
			time = c.getTimeInMillis();
		} else {
			// Absolute time
			setTimeField(c, Calendar.YEAR, t, 0, 2, 2000);
			setTimeField(c, Calendar.MONTH, t, 2, 4, -1);
			setTimeField(c, Calendar.DAY_OF_MONTH, t, 4, 6, -1);
			setTimeField(c, Calendar.HOUR_OF_DAY, t, 6, 8, 0);
			setTimeField(c, Calendar.MINUTE, t, 8, 10, 0);
			setTimeField(c, Calendar.SECOND, t, 10, 12, 0);
			time = c.getTimeInMillis();

			String delta = t.substring(13, 15);
			int offset = Integer.parseInt(delta);
			if (offset != 0) {
				char direction = t.charAt(15);
				if (direction == '-') {
					offset = -offset;
				}
				time += offset * (15 * 60 * 1000);
			}
		}
	}

	public long getDelta() {
		long result;
		if (isRelative()) {
			result = -time;
		} else {
			result = System.currentTimeMillis() - time;
		}
		return result;
	}

	public long getTime() {
		long result;
		if (isRelative()) {
			result = System.currentTimeMillis() - time;
		} else {
			result = time;
		}
		return result;
	}

	public boolean isRelative() {
		return time <= 0;
	}

	/**
	 * Returns java time for given SMPP date time
	 */
	@Override
	public String toString() {
		char[] b = new char[16];
		Calendar c = new GregorianCalendar();
		c.setTimeZone(UTC);
		c.setTimeInMillis(time);
		if (isRelative()) {
			long t = time;
			append(b, 0, 1, (t / DUR_YEAR) % 100);
			t %= DUR_YEAR;
			append(b, 2, 3, t / DUR_MONTH + 1);
			t %= DUR_MONTH;
			append(b, 4, 5, t / DUR_DAY + 1);
			t %= DUR_DAY;
			append(b, 6, 7, t / DUR_HOUR);
			t %= DUR_HOUR;
			append(b, 8, 9, t / DUR_MINUTE);
			t %= DUR_MINUTE;
			append(b, 10, 11, t / DUR_SECOND);
			b[12] = '0';
			b[13] = '0';
			b[14] = '0';
			b[15] = 'R';
		} else {
			append(b, 0, 1, c.get(Calendar.YEAR));
			append(b, 2, 3, c.get(Calendar.MONTH) + 1);
			append(b, 4, 5, c.get(Calendar.DAY_OF_MONTH));
			append(b, 6, 7, c.get(Calendar.HOUR_OF_DAY));
			append(b, 8, 9, c.get(Calendar.MINUTE));
			append(b, 10, 12, c.get(Calendar.SECOND) * 10);
			b[13] = '0';
			b[14] = '0';
			b[15] = '+';
		}
		return new String(b);
	}

	private long time;

	// public static void main(String[] args) throws Exception {
	// Date now = new Date();
	// System.out.println("Now: " + now);
	// System.out.println("getAbsoluteTime(now): " + getAbsoluteTime(now));
	// System.out.println("getXXXXJavaTime(...): " +
	// getAbsoluteTime(getJavaTime(getRelativeTime(0, 0, 0, 0, 0, 0))));
	// System.out.println("getRelativeTime(...): " + getRelativeTime(1, 1, 1, 1,
	// 1, 1));
	// System.out.println("getJavaTime(...): " + getJavaTime(getRelativeTime(1,
	// 2, 1, 1, 1, 1)));
	// }
}