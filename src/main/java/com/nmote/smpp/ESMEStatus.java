/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * ESME SMPP status codes.
 */
public class ESMEStatus {

	/** No Error */
	public static final int OK = 0x00000000;

	/** Message Length is invalid */
	public static final int INVMSGLEN = 0x00000001;

	/** Command Length is invalid */
	public static final int INVCMDLEN = 0x00000002;

	/** Invalid Command ID */
	public static final int INVCMDID = 0x00000003;

	/** Incorrect BIND Status for given command */
	public static final int INVBNDSTS = 0x00000004;

	/** ESME Already in Bound State */
	public static final int ALYBND = 0x00000005;

	/** Invalid Priority Flag */
	public static final int INVPRTFLG = 0x00000006;

	/** Invalid Registered Delivery Flag */
	public static final int INVREGDLVFLG = 0x00000007;

	/** System Error */
	public static final int SYSERR = 0x00000008;

	/** Invalid Source Address */
	public static final int INVSRCADR = 0x0000000A;

	/** Invalid Dest Addr */
	public static final int INVDSTADR = 0x0000000B;

	/** Message ID is invalid */
	public static final int INVMSGID = 0x0000000C;

	/** Bind Failed */
	public static final int BINDFAIL = 0x0000000D;

	/** Invalid Password */
	public static final int INVPASWD = 0x0000000E;

	/** Invalid System ID */
	public static final int INVSYSID = 0x0000000F;

	/** Cancel SM Failed */
	public static final int CANCELFAIL = 0x00000011;

	/** Replace SM Failed */
	public static final int REPLACEFAIL = 0x00000013;

	/** Message Queue Full */
	public static final int MSGQFUL = 0x00000014;

	/** Invalid Service Type Reserved */
	public static final int INVSERTYP = 0x00000015;

	/** Invalid number of destinations */
	public static final int INVNUMDESTS = 0x00000033;

	/** Invalid Distribution List name */
	public static final int INVDLNAME = 0x00000034;

	/** Destination flag is invalid (submit_multi) */
	public static final int INVDESTFLAG = 0x00000040;

	/**
	 * Invalid �submit with replace� request (i.e. submit_sm with
	 * replace_if_present_flag set)
	 */
	public static final int INVSUBREP = 0x00000042;

	/** Invalid esm_class field data */
	public static final int INVESMCLASS = 0x00000043;

	/** Cannot Submit to Distribution List */
	public static final int CNTSUBDL = 0x00000044;

	/** submit_sm or submit_multi failed */
	public static final int SUBMITFAIL = 0x00000045;

	/** Invalid Source address TON */
	public static final int INVSRCTON = 0x00000048;

	/** Invalid Source address NPI */
	public static final int INVSRCNPI = 0x00000049;

	/** Invalid Destination address TON */
	public static final int INVDSTTON = 0x00000050;

	/** Invalid Destination address NPI */
	public static final int INVDSTNPI = 0x00000051;

	/** Invalid system_type field */
	public static final int INVSYSTYP = 0x00000053;

	/** Invalid replace_if_present flag */
	public static final int INVREPFLAG = 0x00000054;

	/** Invalid number of messages */
	public static final int INVNUMMSGS = 0x00000055;

	/** Throttling error (ESME has exceeded allowed message limits) */
	public static final int THROTTLED = 0x00000058;

	/** Invalid Scheduled Delivery Time */
	public static final int INVSCHED = 0x00000061;

	/** Invalid message validity period (Expiry time) */
	public static final int INVEXPIRY = 0x00000062;

	/** Predefined Message Invalid or Not Found */
	public static final int INVDFTMSGID = 0x00000063;

	/** ESME Receiver Temporary App Error Code */
	public static final int RX_T_APPN = 0x00000064;

	/** ESME Receiver Permanent App Error Code */
	public static final int RX_P_APPN = 0x00000065;

	/** ESME Receiver Reject Message Error Code */
	public static final int RX_R_APPN = 0x00000066;

	/** query_sm request failed */
	public static final int QUERYFAIL = 0x00000067;

	/** Error in the optional part of the PDU Body. */
	public static final int INVOPTPARSTREAM = 0x000000C0;

	/** Optional Parameter not allowed */
	public static final int OPTPARNOTALLWD = 0x000000C1;

	/** Invalid Parameter Length. */
	public static final int INVPARLEN = 0x000000C2;

	/** Expected Optional Parameter missing */
	public static final int MISSINGOPTPARAM = 0x000000C3;

	/** Invalid Optional Parameter Value */
	public static final int INVOPTPARAMVAL = 0x000000C4;

	/** Delivery Failure (used for data_sm_resp) */
	public static final int DELIVERYFAILURE = 0x000000FE;

	/** Unknown Error */
	public static final int UNKNOWNERR = 0x000000FF;

	private static ResourceBundle bundle;

	public static String toString(int status) {
		// Double checked idiom. However there is no problem
		// other than double loading of bundle even if we
		// enter a race condition.
		if (bundle == null) {
			synchronized (ESMEStatus.class) {
				if (bundle == null) {
					bundle = ResourceBundle.getBundle("com/nmote/smpp/ESMEStatus");
				}
			}
		}

		String key = Integer.toHexString(status);
		String result;
		try {
			result = bundle.getString(key).replace(' ', '_') + "(" + key + ")";
		} catch (MissingResourceException me) {
			result = "Unknown(" + key + ")";
		}

		return result;
	}
}
