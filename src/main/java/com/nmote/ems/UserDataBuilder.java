/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.ems;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.nmote.io.IOUtils;

/**
 * UserDataBuilder is used to build EMS UserData objects. It handles text
 * formatting, pictures and sounds, as well as concatenation. UserDataBuilder is
 * a subclass of java.io.Writer.
 */
public class UserDataBuilder extends Writer {

	private static IEFactory IE_FACTORY = IEFactory.getInstance();

	private static final int INITIAL_SIZE = 256;

	private static Random RANDOM = new Random();

	public UserDataBuilder() {
		this(RANDOM.nextInt(255));
	}

	public UserDataBuilder(int reference) {
		userData = new UserData();
		concatenation = (ConcatenationIE) IE_FACTORY.createConcatenation(255, 1, reference);
		concatenations.add(concatenation);
		userData.addHeader(concatenation);
	}

	public UserDataBuilder appendPicture(int width, int height, byte[] bitmap) {
		newMedia((MediaIE) IE_FACTORY.createPicture(position, width, height, bitmap));
		assertLengthUnder140();
		return this;
	}

	public UserDataBuilder appendPredefinedSound(int sound) {
		newMedia((MediaIE) IE_FACTORY.createPredefinedSound(position, sound));
		assertLengthUnder140();
		return this;
	}

	public UserDataBuilder appendText(String s) {
		int m = s.length();
		int bytesLeft = getBytesLeft();
		for (int i = 0; i < m; ++i) {
			if (bytesLeft == 0) {
				newUserData();
				bytesLeft = getBytesLeft();
			}
			text[position] = (byte) s.charAt(i);
			++position;
			--bytesLeft;
		}
		assertLengthUnder140();
		return this;
	}

	/**
	 * This method has no effect.
	 *
	 * @see java.io.Writer#close()
	 */
	@Override
	public void close() {
	}

	/**
	 * This method has no effect.
	 *
	 * @see java.io.Writer#flush()
	 */
	@Override
	public void flush() {
	}

	/**
	 * Calculates minimum number of messages required to hold appended data.
	 * Actual number might be higher.
	 *
	 * @return number of messages.
	 */
	public int numberOfMessages() {
		return result.size() + 1;
	}

	public UserDataBuilder setAlignment(int alignment) {
		newFormatting(Style.setSize(getStyle(), alignment));
		assertLengthUnder140();
		return this;
	}

	public UserDataBuilder setBold(boolean on) {
		newFormatting(Style.setBold(getStyle(), on));
		assertLengthUnder140();
		return this;
	}

	public UserDataBuilder setItalic(boolean on) {
		newFormatting(Style.setItalic(getStyle(), on));
		assertLengthUnder140();
		return this;
	}

	public UserDataBuilder setSize(int size) {
		newFormatting(Style.setSize(getStyle(), size));
		assertLengthUnder140();
		return this;
	}

	public UserDataBuilder setStrikethrough(boolean on) {
		newFormatting(Style.setStrikethrough(getStyle(), on));
		assertLengthUnder140();
		return this;
	}

	public UserDataBuilder setUnderlined(boolean on) {
		newFormatting(Style.setUnderlined(getStyle(), on));
		assertLengthUnder140();
		return this;
	}

	public List<byte[]> toByteArrayList() {
		List<byte[]> result = new ArrayList<byte[]>();
		for (UserData ud : toUserDataList()) {
			try {
				result.add(IOUtils.save(ud));
			} catch (IOException e) {
				throw new RuntimeException("Impossible");
			}
		}
		return result;
	}

	/**
	 * Converts into a list of UserData instances.
	 *
	 * @return user data instances
	 */
	public List<UserData> toUserDataList() {
		int m = result.size() + 1;
		if (m > 1) {
			for (ConcatenationIE ie : concatenations) {
				ie.setMaximum(m);
			}
		} else {
			userData.getHeaders().remove(concatenation);
		}
		newUserData();
		return result;
	}

	/**
	 * Appends text. Equivalent to appendText(String).
	 *
	 * @see java.io.Writer#write(char[], int, int)
	 */
	@Override
	public void write(char[] cbuf, int off, int len) {
		appendText(new String(cbuf, off, len));
	}

	private void assertLengthUnder140() {
		if (userData.getHeaderLength() + 1 + position > 140) {
			throw new IllegalStateException();
		}
	}

	private int getBytesLeft() {
		return 140 - 1 - userData.getHeaderLength() - position;
	}

	private int getStyle() {
		return textFormat != null ? textFormat.getStyle() : 0;
	}

	private void newFormatting(int style) {
		if (style != getStyle()) {
			if (textFormat != null) {
				textFormat.setEnd(position);
				textFormat = null;
			}
			if (style != 0) {
				if (userData.getHeaderLength() + 1 + 5 >= 140) {
					newUserData();
				}
				textFormat = (TextFormattingIE) IE_FACTORY.createTextFormatting(style, position, position);
				userData.addHeader(textFormat);
			}
		}
	}

	private void newMedia(MediaIE ie) {
		int size = userData.getHeaderLength() + 1 + position;
		if (ie.getLength() + 2 + size > 140) {
			newUserData();
		}
		userData.addHeader(ie);
	}

	private void newUserData() {
		int currentStyle = getStyle();
		if (textFormat != null) {
			textFormat.setEnd(position);
			textFormat = null;
		}

		byte[] ud = new byte[position];
		System.arraycopy(text, 0, ud, 0, position);
		userData.setUserData(ud);

		result.add(userData);

		userData = new UserData();
		position = 0;
		concatenation = (ConcatenationIE) IE_FACTORY.createConcatenation(255, concatenation.getSequence() + 1,
				concatenation.getReference());
		concatenations.add(concatenation);
		userData.addHeader(concatenation);
		newFormatting(currentStyle);
	}

	private int position = 0;
	private UserData userData;
	private TextFormattingIE textFormat;
	private ConcatenationIE concatenation;

	private List<UserData> result = new ArrayList<UserData>();
	private List<ConcatenationIE> concatenations = new ArrayList<ConcatenationIE>();
	private byte[] text = new byte[INITIAL_SIZE];
}