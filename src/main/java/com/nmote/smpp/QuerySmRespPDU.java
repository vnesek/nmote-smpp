/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

/*
 * WARNING Do not modify this file.
 *
 * This file was generated from protocol description file
 * and will be OVERWRITTEN on next invocation of
 * smpp-gen ant task during build process.
 */

package com.nmote.smpp;

import java.io.IOException;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * The MC returns a query_sm_resp PDU as a means of
indicating the result of a message query attempt. The PDU
will indicate the success or failure of the attempt and for
successful attempts will also include the current state of the
message.
  
 *
 * @author (this class was autogenerated)
 */
public class QuerySmRespPDU extends AbstractPDU {

    private static final long serialVersionUID = About.SERIAL_VERSION_UID;

    /**
     * Creates a new PDU object.
     */
    public QuerySmRespPDU() {
    }

    /**
     * Returns Command ID for this PDU.
     *
     * @return PDU.QUERY_SM_RESP;
     */
    public int getCommandId() {
        return PDU.QUERY_SM_RESP;
    }

    /**
     * Reads mandatory and optional parameters from SMPPInputStream.
     *
     * @param in SMPPInputStream for reading parameters.
     * @throws IOException In case of a problem while reading data.
     */
    void readParameters(SMPPInputStream in) throws IOException {
		// Mandatory parameters
		message_id = in.readCString();
		final_date = in.readSMPPTime();
		message_state = in.readInteger1();
		error_code = in.readInteger1();

		// Optional parameters
		readOptionalParameters(in);
    }

    /**
     * Write mandatory and optional PDU parameters to SMPPOutputStream.
     *
     * @param out SMPPOutputStream for writting paramters.
     * @throws IOException In case of errors while writing.
     */
    void writeParameters(SMPPOutputStream out) throws IOException {
        // Mandatory parameters
		out.writeCString(message_id);
		out.writeSMPPTime(final_date);
		out.writeInteger1(message_state);
		out.writeInteger1(error_code);

		// Optional parameters
		writeOptionalParameters(out);
    }

    /**
     * @see com.nmote.smpp.AbstractPDU#isRequestPDU()
     */
	public boolean isRequestPDU() {
		return false;
	}

	/**
	 * @see com.nmote.smpp.AbstractPDU#createResponse()
	 */
	public AbstractPDU createResponse() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.nmote.smpp.AbstractPDU#isOneWay()
	 */
	public final boolean isOneWay() {
		return false;
	}

    /**
     * Returns PDU name.
     *
     * @return PDU name
     */
    public String getName() {
    	return "query_sm_resp";
    }

    /**
     * Creates a string representation of a PDU.
     *
     * @return a String
     */
    public String toString() {
    	ToStringBuilder b = new ToStringBuilder(this);
    	b.append(getSequence());
    	b.append(ESMEStatus.toString(getStatus()));

        // Appending mandatory parameters
        b.append("message_id", toStringHelper(message_id));
        b.append("final_date", toStringHelper(final_date));
        b.append("message_state", toStringHelper(message_state));
        b.append("error_code", toStringHelper(error_code));

        // Appending optional parameters
        if (getParameters() != null) {
        	b.append(getParameters());
        }

        return b.toString();
    }

    /**
     * Calculates hash code of this object.
     *
     * @return hash code
     */
    public int hashCode() {
    	HashCodeBuilder b = new HashCodeBuilder();
    	b.append(getSequence());
    	b.append(getStatus());

        // Appending mandatory parameters
        b.append(message_id);
        b.append(final_date);
        b.append(message_state);
        b.append(error_code);

        // Appending optional parameters
        if (getParameters() != null) {
        	b.append(getParameters());
        }

        return b.toHashCode();
    }

    /**
     * Checks if <code>o</code> and this object are equal.
     *
     * @return true if objects are equal, false otherwise
     */
    public boolean equals(Object o) {
    	boolean result;
    	if (o instanceof QuerySmRespPDU) {
    		QuerySmRespPDU p = (QuerySmRespPDU) o;
    		EqualsBuilder b = new EqualsBuilder();
	    	b.append(p.getSequence(), getSequence());
    		b.append(p.getStatus(), getStatus());

	        // Appending mandatory parameters
    	    b.append(p.message_id, message_id);
    	    b.append(p.final_date, final_date);
    	    b.append(p.message_state, message_state);
    	    b.append(p.error_code, error_code);

	        // Appending optional parameters
        	b.append(p.getParameters(), getParameters());

        	result = b.isEquals();
        } else {
        	result = false;
        }

        return result;
    }

    // Mandatory parameters

    private String message_id;

    /**
     * Getter for a mandatory parameter message_id.
     * The unique message identifier reference assigned by the MC to each submitted short message. It is an opaque value and is set according to MC implementation.
     * @return parameter value
     */
    public String getMessageId() {
		return message_id;
    }

    /**
     * Setter for a mandatory parameter message_id.
     * The unique message identifier reference assigned by the MC to each submitted short message. It is an opaque value and is set according to MC implementation.
     * @param value New parameter value.
     */
    public void setMessageId(String value) {
		message_id = value;
    }

    private SMPPTime final_date;

    /**
     * Getter for a mandatory parameter final_date.
     * 
     * @return parameter value
     */
    public SMPPTime getFinalDate() {
		return final_date;
    }

    /**
     * Setter for a mandatory parameter final_date.
     * 
     * @param value New parameter value.
     */
    public void setFinalDate(SMPPTime value) {
		final_date = value;
    }

    private int message_state;

    /**
     * Getter for a mandatory parameter message_state.
     * The message_state value is returned by the MC to the ESME as part of the query_sm_resp PDU.
     * @return parameter value
     */
    public int getMessageState() {
		return message_state;
    }

    /**
     * Setter for a mandatory parameter message_state.
     * The message_state value is returned by the MC to the ESME as part of the query_sm_resp PDU.
     * @param value New parameter value.
     */
    public void setMessageState(int value) {
		message_state = value;
    }

    private int error_code;

    /**
     * Getter for a mandatory parameter error_code.
     * 
     * @return parameter value
     */
    public int getErrorCode() {
		return error_code;
    }

    /**
     * Setter for a mandatory parameter error_code.
     * 
     * @param value New parameter value.
     */
    public void setErrorCode(int value) {
		error_code = value;
    }
}