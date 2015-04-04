/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.ems;

/**
 * IEFactory is used to create instances of IE elements
 */
public class IEFactory {

	private static final IEFactory INSTANCE = new IEFactory();

	public static IEFactory getInstance() {
		return INSTANCE;
	}

	public IE create(int identifier) {
		IE ie;
		switch (identifier) {
		case 0:
		case 8:
			ie = new ConcatenationIE(identifier);
			break;
		case 4:
		case 5:
			ie = new PortAddressingIE(identifier);
			break;
		case 0x0a:
			ie = new TextFormattingIE(identifier);
			break;
		case 0x0b:
			ie = new PredefinedSoundIE(identifier);
			break;
		case 0x10:
		case 0x11:
		case 0x12:
			ie = new PictureIE(identifier);
			break;
		default:
			ie = null;
		}
		return ie;
	}

	public IE createConcatenation(int maximum, int sequence, int reference) {
		ConcatenationIE ie = new ConcatenationIE(0);
		ie.setMaximum(maximum);
		ie.setSequence(sequence);
		ie.setReference(reference);
		return ie;
	}

	public IE createPicture(int position, int width, int height, byte[] bitmap) {
		PictureIE ie = new PictureIE(0x10);
		ie.setPosition(position);
		ie.setWidth(width);
		ie.setHeight(height);
		ie.setBitmap(bitmap);
		return ie;
	}

	public IE createPortAddressing(int originatorPort, int destinationPort) {
		PortAddressingIE ie = new PortAddressingIE(4);
		ie.setOriginatorPort(originatorPort);
		ie.setDestinationPort(destinationPort);
		return ie;
	}

	public IE createPredefinedSound(int position, int sound) {
		PredefinedSoundIE ie = new PredefinedSoundIE(0x0b);
		ie.setPosition(position);
		ie.setSound(sound);
		return ie;
	}

	public IE createTextFormatting(int style, int start, int end) {
		TextFormattingIE ie = new TextFormattingIE(0x0a);
		ie.setStyle(style);
		ie.setStart(start);
		ie.setEnd(end);
		return ie;
	}
}
