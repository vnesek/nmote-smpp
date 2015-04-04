/* 
 * Copyright (c) Nmote d.o.o. 2003. All rights reserved.
 * Unathorized use of this file is prohibited by law.
 * See LICENSE.txt for licensing information.
 */
 
package com.nmote.smpp.task;

import java.util.HashMap;
import java.util.Map;

/**
 * FormattingHelper used for velocity template expansion.
 * @author Vjekoslav Nesek
 */
public class FormattingHelper {

	public FormattingHelper() {
		javaTypes = new HashMap();
		javaTypes.put("Integer1", "int");
		javaTypes.put("Integer2", "int");
		javaTypes.put("Integer4", "int");
		javaTypes.put("Boolean", "boolean");
		javaTypes.put("String", "String");
		javaTypes.put("CString", "String");
		javaTypes.put("PString", "byte[]");
		javaTypes.put("SMPPTime", "SMPPTime");
		javaTypes.put("SMPPAddress", "SMPPAddress");
		javaTypes.put("OctetString", "byte[]");
	}

	/**
	 * For a given string, transform it in such a way that every 
	 * underscore is removed, and the character following the 
	 * underscore gets turned into uppercase.
	 *
	 * @param s String to turn into camelCase
	 */
	private static String toCamelCase(String s) {
		boolean upNext = false;
		int size = s.length();
		StringBuffer result = new StringBuffer(size);
		for (int i = 0; i < size; ++i) {
			char c = s.charAt(i);
			if (c == '_') {
				upNext = true;
			} else {
				if (upNext) {
					c = Character.toUpperCase(c);
					upNext = false;
				}
				result.append(c);
			}
		}

		return result.toString();
	}
	
	public String hexToInt(String s) {
		return Integer.toString((int) Long.parseLong(s, 16));
	}
	
	public String upperCase(String s) {
		return s.toUpperCase();
	}

	public String classCase(String s) {
		return toCamelCase("_" + s);
	}
	
	public String methodCase(String s) {
		return toCamelCase(s);
	}
	
	public String hex(long l) {
		return "0x" + Long.toHexString(l);
	}
	
	public String javaType(Param p) {
		String r = (String) javaTypes.get(p.getType());
		if (r == null) {
			System.out.println("Unknown type " + p.getType() + " " + p.getName());
		}
		return r;
	}
	
	private Map javaTypes;
}
