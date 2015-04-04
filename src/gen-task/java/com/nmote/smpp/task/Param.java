/* 
 * Copyright (c) Nmote d.o.o. 2003. All rights reserved.
 * Unathorized use of this file is prohibited by law.
 * See LICENSE.txt for licensing information.
 */
 
package com.nmote.smpp.task;

/**
 *
 * @author Vjekoslav Nesek
 */
public class Param {
	public Param(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the def.
	 * @return String
	 */
	public String getDef() {
		return def;
	}

	/**
	 * Returns the docs.
	 * @return String
	 */
	public String getDocs() {
		return docs;
	}

	/**
	 * Returns the length.
	 * @return String
	 */
	public String getLength() {
		return length;
	}

	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the tag.
	 * @return String
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * Returns the type.
	 * @return String
	 */
	public String getType() {
		return type;
	}

	/**
	 * Returns the version.
	 * @return String
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Returns the mandatory.
	 * @return boolean
	 */
	public boolean isMandatory() {
		return mandatory;
	}

	/**
	 * Sets the def.
	 * @param def The def to set
	 */
	public void setDef(String def) {
		if ("".equals(def)) {
			def = null;
		}
		this.def = def;
	}

	/**
	 * Sets the docs.
	 * @param docs The docs to set
	 */
	public void setDocs(String docs) {
		this.docs = docs;
	}

	/**
	 * Sets the length.
	 * @param length The length to set
	 */
	public void setLength(String length) {
		this.length = length;
	}

	/**
	 * Sets the mandatory.
	 * @param mandatory The mandatory to set
	 */
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	/**
	 * Sets the tag.
	 * @param tag The tag to set
	 */
	public void setTag(String tag) {
		if ("".equals(tag)) {
			tag = null;
		}
		this.tag = tag;
	}

	/**
	 * Sets the type.
	 * @param type The type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Sets the version.
	 * @param version The version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	private String def;
	private String docs;
	private String length;
	private boolean mandatory;

	private String name;
	private String tag;
	private String type;
	private String version;

	/**
	 * For a given string, transform it in such a way that every 
	 * underscore is removed, and the character following the 
	 * underscore gets turned into uppercase.
	 *
	 * @param s String to turn into camelCase
	 */
	public static String toCamelCase(String s) {
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

}