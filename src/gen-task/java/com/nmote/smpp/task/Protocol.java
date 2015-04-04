/* 
 * Copyright (c) Nmote d.o.o. 2003. All rights reserved.
 * Unathorized use of this file is prohibited by law.
 * See LICENSE.txt for licensing information.
 */
 
package com.nmote.smpp.task;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Protocol.java
 * @author Vjekoslav Nesek
 */
public class Protocol {

	/**
	 * Constructor for Protocol.
	 */
	public Protocol() {
	}

	public void addParam(Param param) {
		if (params.containsKey(param.getName())) {
			throw new IllegalArgumentException("Duplicate parameter " + param.getName());
		}
		params.put(param.getName(), param);
	}
	
	public void addPDU(PDU pdu) {
		if (pdusByName.containsKey(pdu.getName())) {
			throw new IllegalArgumentException("Duplicate PDU " + pdu.getName());
		}
		pdusByName.put(pdu.getName(), pdu);
		pdusById.put(new Long(Long.parseLong(pdu.getId(), 16)), pdu);
	}
	
	public Param getParam(String name) {
		return (Param) params.get(name);
	}
		
	public Collection getParams() {
		return params.values();
	}
	
	public PDU getPDU(String name) {
		return (PDU) pdusByName.get(name);
	}
	
	public PDU getPDUById(long id) {
		return (PDU) pdusById.get(new Long(id));
	}
	
	public Collection getPDUs() {
		return pdusByName.values();
	}
	
	public PDU getRequestPDU(PDU pdu) {
		return getPDUById(~0x80000000L & Long.parseLong(pdu.getId(), 16));		
	}
	
	public PDU getResponsePDU(PDU pdu) {
		return getPDUById(0x80000000L | Long.parseLong(pdu.getId(), 16));		
	}
	
	private Map params = new HashMap();
	private Map pdusById = new HashMap();
	private Map pdusByName = new HashMap();
}
