/* 
 * Copyright (c) Nmote d.o.o. 2003. All rights reserved.
 * Unathorized use of this file is prohibited by law.
 * See LICENSE.txt for licensing information.
 */
 
package com.nmote.smpp.task;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vjekoslav Nesek
 */
public class PDU {
	PDU(String name) {
		this.name = name;
	}

	public List getAll() {
		List result = new ArrayList();
		if (mandatory != null) result.addAll(mandatory);
		if (optional != null) result.addAll(optional);
		return result;
	}

	/**
	 * Returns the docs.
	 * @return String
	 */
	public String getDocs() {
		return docs;
	}

	/**
	 * Returns the id.
	 * @return String
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the mandatory.
	 * @return List
	 */
	public List getMandatory() {
		return mandatory;
	}

	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the optional.
	 * @return List
	 */
	public List getOptional() {
		return optional;
	}
	
	
	/**
	 * Returns the version.
	 * @return String
	 */
	public String getVersion() {
		return version;
	}

	public boolean isRequest() {
		return (Long.parseLong(id, 16) & 0x80000000L) == 0L;
	}

	public boolean isRequestTo(PDU pdu) {
		return isRequest() && (Long.parseLong(pdu.id, 16) ^ Long.parseLong(id, 16)) == 0x80000000L;
	}

	public boolean isResponse() {
		return (Long.parseLong(id, 16) & 0x80000000L) != 0L;
	}

	public boolean isResponseTo(PDU pdu) {
		return isResponse() && (Long.parseLong(pdu.id, 16) ^ Long.parseLong(id, 16)) == 0x80000000L;
	}
	
	/**
	 * Sets the docs.
	 * @param docs The docs to set
	 */
	public void setDocs(String docs) {
		this.docs = docs;
	}

	/**
	 * Sets the id.
	 * @param id The id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Sets the mandatory.
	 * @param mandatory The mandatory to set
	 */
	public void setMandatory(List mandatory) {
		this.mandatory = mandatory;
	}

	/**
	 * Sets the optional.
	 * @param optional The optional to set
	 */
	public void setOptional(List optional) {
		this.optional = optional;
	}

	/**
	 * Sets the version.
	 * @param version The version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	private String docs;
	private String id;
	private List mandatory;

	private String name;
	private List optional;
	private String version;

}