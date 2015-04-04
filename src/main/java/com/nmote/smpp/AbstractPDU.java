/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.nmote.smpp.util.DCS;

/**
 * Abstract base class for all SMPP PDUs (Protocol Data Units).
 */
public abstract class AbstractPDU implements Cloneable, Serializable {

	private static final long serialVersionUID = About.SERIAL_VERSION_UID;

	static {
		// Print copyright information
		System.out.println(About.COPYRIGHT);
	}

	private static String toHex(byte[] s) {
		if (s != null) {
			int len = s.length;
			StringBuffer result = new StringBuffer(4 * len);
			for (int i = 0; i < len; ++i) {
				char c = (char) s[i];
				result.append('|');
				result.append(toHexDigit((c >> 4) & 0xf));
				result.append(toHexDigit(c & 0xf));
				if (c >= 32 && c < 128) {
					result.append(c);
				}
			}
			return result.toString();
		} else {
			return "";
		}
	}

	private static char toHexDigit(int d) {
		switch (d) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
			return (char) ('0' + d);
		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
		case 15:
			return (char) ('A' + d - 10);
		default:
			throw new IllegalArgumentException("Not a hex digit: " + d);
		}
	}

	private static String toStringOrHex(String s) {
		if (s != null) {
			int len = s.length();
			StringBuffer result = new StringBuffer(len);
			for (int i = 0; i < len; ++i) {
				char c = s.charAt(i);
				if (c < 32 || c > 127) {
					result.append('|');
					result.append(toHexDigit((c >> 4) & 0xf));
					result.append(toHexDigit(c & 0xf));
				} else {
					result.append(c);
				}
			}
			return result.toString();
		} else {
			return "";
		}
	}

	/**
	 * Adds parameter with a tag <code>tag</code> to a list of optional
	 * parameters. Returns created parameter. This allows chaining of
	 * <code>addParameter()</code> call with setter calls on a Parameter
	 * instance. For example:
	 *
	 * <pre>
	 * pdu.addParameter(Tag.SET_DPF).setInt1(0);
	 * </pre>
	 *
	 * @param tag
	 *            SMPP tag id
	 * @return new Parameter instance
	 */
	public Parameter addParameter(int tag) {
		Parameter p = new Parameter(tag);
		if (parameters == null) {
			parameters = new ArrayList<Parameter>();
		}
		parameters.add(p);
		return p;
	}

	/**
	 * Clones this PDU. Cloned PDU will have status and sequence numbers reset.
	 *
	 * @see java.lang.Object#clone()
	 * @return Clone of this object
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		AbstractPDU result = (AbstractPDU) super.clone();
		result.sequence = 0;
		result.status = 0;
		return result;
	}

	/**
	 * Creates generic_nack response PDU for this request PDU.
	 *
	 * @return response PDU
	 * @throws UnsupportedOperationException
	 *             if this is a response PDU
	 */
	public GenericNackPDU createGenericNackResponse() {
		if (isRequestPDU()) {
			GenericNackPDU response = new GenericNackPDU();
			response.sequence = sequence;
			return response;
		} else {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Creates response PDU for this request PDU.
	 *
	 * @return response PDU
	 * @throws UnsupportedOperationException
	 *             if this is a response PDU
	 */
	public abstract AbstractPDU createResponse();

	/**
	 * Returns SMPP command id for this PDU.
	 *
	 * @return SMPP command ID
	 */
	public abstract int getCommandId();

	/**
	 * Returns PDU name as defined in SMPP specification.
	 *
	 * @return PDU name
	 */
	public abstract String getName();

	/**
	 * Returns first occrence of optional parameter identified by tag. If PDU
	 * doesn't contain this optional parameter returns null.
	 *
	 * @param tag
	 *            Tag identifier for optional parameter
	 * @return Paramter instance or null
	 * @see Tag
	 * @see Parameter
	 */
	public Parameter getParameter(int tag) {
		Parameter result = null;
		if (parameters != null) {
			for (Iterator<Parameter> i = parameters.iterator(); i.hasNext();) {
				Parameter p = i.next();
				if (p.getTag() == tag) {
					result = p;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Returns the a List of Parameter instances representing optional PDU
	 * parameters.
	 *
	 * @return List
	 * @see Parameter
	 */
	public List<Parameter> getParameters() {
		return parameters;
	}

	/**
	 * Returns a list of all optional parameters identified by tag. If PDU
	 * doesn't contain any optional parameter identified by tag, empty list is
	 * returned.
	 *
	 * @param tag
	 *            Tag identifier for optional parameter
	 * @return List of Parameter instances
	 * @see Tag
	 * @see Parameter
	 */
	public List<Parameter> getParameters(int tag) {
		List<Parameter> result = new ArrayList<Parameter>();
		List<Parameter> l = this.parameters;
		if (l != null) {
			for (int i = 0, m = l.size(); i < m; ++i) {
				Parameter p = l.get(i);
				if (p.getTag() == tag) {
					result.add(p);
				}
			}
		}
		return result;
	}

	/**
	 * Return PDU's SMPP sequence number. Sequence numbers are generated in
	 * sessions, newly created PDUs have sequence number 0 until they are
	 * execute()-d in a session.
	 *
	 * @return PDU sequence number
	 */
	public int getSequence() {
		return sequence;
	}

	/**
	 * Return SMPP command status. ESMEStatus class contains a list of defined
	 * SMPP constants.
	 *
	 * @return SMPP status
	 * @see ESMEStatus
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Returns true if this PDU is one-way. One-way PDUs haven't got associated
	 * response PDU.
	 *
	 * @return true if this is a one-way PDU
	 */
	public abstract boolean isOneWay();

	/**
	 * Returns true if this PDU is a request.
	 *
	 * @return true if this PDU is a request
	 * */
	public abstract boolean isRequestPDU();

	/**
	 * Removes first occurence of a parameter with a tag <code>tag</code> in a
	 * list of optional parameters. Returns true if parameter was removed.
	 *
	 * @param tag
	 *            SMPP tag id
	 * @return true if parameter was removed, false if there was no parameter
	 *         with specified tag
	 */
	public boolean removeParameter(int tag) {
		boolean result = false;
		if (parameters != null) {
			for (Iterator<Parameter> i = parameters.iterator(); i.hasNext();) {
				Parameter p = i.next();
				if (p.getTag() == tag) {
					i.remove();
					result = true;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Sets the List of optional parameters. List elements must be instances of
	 * Parameter class.
	 *
	 * @param parameters
	 *            The parameters to set
	 */
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	/**
	 * Sets SMPP command status. ESMEStatus class is an enumaration of defined
	 * SMPP status constants.
	 *
	 * @param status
	 *            SMPP status code
	 * @see ESMEStatus
	 * @throws UnsupportedOperationException
	 *             if this is a request PDU
	 */
	public void setStatus(int status) {
		if (isRequestPDU()) {
			throw new UnsupportedOperationException("Can't set a status on a request PDU");
		}
		this.status = status;
	}

	/**
	 * Read optional parameters from SMPPInputStream. Called from subclasses.
	 *
	 * @param in
	 * @throws IOException
	 */
	final void readOptionalParameters(SMPPInputStream in) throws IOException {
		while (in.hasMoreOptionalParameters()) {
			int tag = in.readInteger2();
			int len = in.readInteger2();

			Parameter p = new Parameter(tag);
			p.setData(in.readOctetString(len));

			if (parameters == null) {
				parameters = new ArrayList<Parameter>();
			}

			parameters.add(p);
		}
	}

	/**
	 * Reads optional parameters from SMPPInputStream.
	 *
	 * @param in
	 * @throws IOException
	 */
	abstract void readParameters(SMPPInputStream in) throws IOException;

	boolean toStringHelper(boolean o) {
		return o;
	}

	Object toStringHelper(byte[] data) {
		if (data == null)
			return "<null>";
		int dcs;
		int esm;
		if (this instanceof DeliverSmPDU) {
			dcs = ((DeliverSmPDU) this).getDataCoding();
			esm = ((DeliverSmPDU) this).getEsmClass();
		} else if (this instanceof SubmitSmPDU) {
			dcs = ((SubmitSmPDU) this).getDataCoding();
			esm = ((SubmitSmPDU) this).getEsmClass();
		} else {
			dcs = 0;
			esm = 0;
		}
		return esm != 0 ? toHex(data) : toStringOrHex(DCS.toUnicode(data, dcs));
	}

	int toStringHelper(int o) {
		return o;
	}

	Object toStringHelper(Object o) {
		return o;
	}

	/**
	 * Writes optional paramaters to SMPPOutputStream. Called from
	 * <code>writeParameters()</code> methods in subclasses.
	 *
	 * @param out
	 * @throws IOException
	 */
	final void writeOptionalParameters(SMPPOutputStream out) throws IOException {
		if (parameters != null) {
			for (Iterator<Parameter> i = parameters.iterator(); i.hasNext();) {
				Parameter p = i.next();
				if (Tag.since(p.getTag()) <= version) {
					out.writeInteger2(p.getTag());
					int len = p.getLength();
					out.writeInteger2(len);
					if (len > 0) {
						out.write(p.getData());
					}
				} else {
					throw new IOException("Writting " + p + " to a protocol " + "version "
							+ Integer.toHexString(version));
				}
			}
		}
	}

	/**
	 * Writes optional parameters to SMPPOutputStream.
	 *
	 * @param out
	 * @throws IOException
	 */
	abstract void writeParameters(SMPPOutputStream out) throws IOException;

	int sequence;
	int status;
	int version;
	private List<Parameter> parameters;
}