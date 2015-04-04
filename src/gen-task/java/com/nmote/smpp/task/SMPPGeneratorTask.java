/* 
 * Copyright (c) Nmote d.o.o. 2003. All rights reserved.
 * Unathorized use of this file is prohibited by law.
 * See LICENSE.txt for licensing information.
 */
 
package com.nmote.smpp.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.velocity.context.Context;
import org.apache.velocity.texen.ant.TexenTask;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Ant task that generates sources based on SMPP protocol description file.
 * @author  vnesek
 */
public class SMPPGeneratorTask extends TexenTask {

	protected Param createParam(Element e) {
		Param param = new Param(e.getAttribute("name"));
		param.setType(e.getAttribute("type"));
		param.setLength(e.getAttribute("length"));
		param.setTag(e.getAttribute("tag"));
		param.setDef(e.getAttribute("default"));
		param.setDocs(e.getAttribute("doc"));
		param.setVersion(e.getAttribute("version"));
		return param;
	}

	protected List createParamList(Element e, String tag) {
		List params;
		NodeList nodeList = e.getElementsByTagName(tag);
		if (nodeList.getLength() > 0) {
			params = new ArrayList();
			StringTokenizer tok = new StringTokenizer(getText(nodeList.item(0).getChildNodes()));
			while (tok.hasMoreTokens()) {
				String name = tok.nextToken();
				Param p = protocol.getParam(name);
				if (p == null) {
					throw new IllegalArgumentException("Parameter " + name + " not defined");
				}
				params.add(p);
			}
		} else {
			params = null;
		}
		return params;
	}
	
	private static String getText(NodeList nodeList) {
		StringBuffer b = new StringBuffer();
		for (int idx = 0; idx < nodeList.getLength(); ++idx) {
			Node n = nodeList.item(idx);
			if (n.getNodeType() == Node.TEXT_NODE) {
				b.append(n.getNodeValue());
			}
		}
		return b.toString();
	}

	protected PDU createPDU(Element e) {
		PDU pdu = new PDU(e.getAttribute("name"));
		pdu.setId(e.getAttribute("id"));
		pdu.setMandatory(createParamList(e, "mandatory"));
		pdu.setOptional(createParamList(e, "optional"));
		pdu.setVersion(e.getAttribute("version"));
		NodeList docs = e.getElementsByTagName("doc");
		if (docs.getLength() > 0) {
			pdu.setDocs(getText(((Element) docs.item(0)).getChildNodes()));
		} else {
			log(pdu.getName() + " has no docs");
		}
		return pdu;
	}

	public String getSmppFile() {
		return smppFile;
	}

	public String getTargetPackage() {
		return targetPackage;
	}

	protected void populateInitialContext(Context context) {
		try {
			protocol = new Protocol();
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new File(getProject().getBaseDir(), getSmppFile()));

			Element root = doc.getDocumentElement();
			NodeList nodeList = ((Element) root.getElementsByTagName("params").item(0)).getElementsByTagName("param");
			for (int idx = 0; idx < nodeList.getLength(); ++idx) {
				Node n = nodeList.item(idx);
				protocol.addParam(createParam((Element) n));					
			}
			
			nodeList = ((Element) root.getElementsByTagName("pdus").item(0)).getElementsByTagName("pdu");
			for (int idx = 0; idx < nodeList.getLength(); ++idx) {
				Node n = nodeList.item(idx);
				protocol.addPDU(createPDU((Element) n));					
			}
			
			context.put("protocol", protocol);
			context.put("format", new FormattingHelper());
			context.put("package", getTargetPackage());
			context.put("task", this);
		} catch (Exception e) {
			super.log("Failed to parse: " + getSmppFile() + " (" + e + ")");
		}
	}

	public void setSmppFile(String protocolFile) {
		this.smppFile = protocolFile;
	}

	public void setTargetPackage(String targetPackage) {
		this.targetPackage = targetPackage;
	}
	private Protocol protocol;

	private String smppFile = "smpp.xml";
	private String targetPackage = "smpp";
}