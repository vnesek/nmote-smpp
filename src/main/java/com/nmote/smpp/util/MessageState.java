package com.nmote.smpp.util;

/**
 * Standard constants for message_state parameter.
 */
public interface MessageState {

	/** The message is in enroute state. */
	int ENROUTE = 1;

	/** Message is delivered to destination. */
	int DELIVERED = 2;

	/** Message validity period has expired. */
	int EXPIRED = 3;

	/** Message has been deleted. */
	int DELETED = 4;

	/** Message is undeliverable. */
	int UNDELIVERABLE = 5;

	/**
	 * Message is in accepted state (i.e. has been manually read on behalf of
	 * the subscriber by customer service)
	 */
	int ACCEPTED = 6;

	/** Message is in invalid state. */
	int UNKNOWN = 7;

	/** Message is in a rejected state. */
	int REJECTED = 8;
}
