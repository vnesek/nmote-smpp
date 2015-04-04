/*
 * Copyright (c) Nmote d.o.o. 2003. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp.samples;

import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Samples contains code in common for all SMPP sample programs.
 *
 * @author Vjekoslav Nesek
 */
public class Samples {

	/**
	 * Configurates debuging styles.
	 */
	public static void init() {
		StandardToStringStyle tss = new StandardToStringStyle();
		tss.setUseShortClassName(true);
		tss.setContentStart("(");
		tss.setContentEnd(")");
		tss.setUseIdentityHashCode(false);
		ToStringBuilder.setDefaultStyle(tss);
	}
}
