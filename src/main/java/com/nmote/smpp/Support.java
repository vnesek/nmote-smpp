/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

import org.apache.commons.lang3.builder.StandardToStringStyle;

/**
 * Support class.
 *
 * @author Vjekoslav Nesek
 */
class Support {

	private static StandardToStringStyle TO_STRING_STYLE;

	static final StandardToStringStyle getStringStye() {
		if (TO_STRING_STYLE == null) {
			TO_STRING_STYLE = new StandardToStringStyle();
			TO_STRING_STYLE.setUseShortClassName(true);
			TO_STRING_STYLE.setContentStart("(");
			TO_STRING_STYLE.setContentEnd(")");
			TO_STRING_STYLE.setContentEnd(")");
			TO_STRING_STYLE.setUseIdentityHashCode(false);
		}

		return TO_STRING_STYLE;
	}
}
