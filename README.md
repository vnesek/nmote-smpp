Nmote SMPP library
==================

Java SMPP 5.0 client and server library. Supports 5.0 and 3.x protocol versions. BSD style license.

The Short Message Peer-to-Peer (SMPP) in the telecommunications industry is an open, industry standard protocol designed to provide a flexible data communication interface for the transfer of short message data between External Short Messaging Entities (ESME), Routing Entities (RE) and Message Centres.

(See https://en.wikipedia.org/wiki/Short_Message_Peer-to-Peer)


Add to Your's Project
---------------------

If you use maven for dependency management, add following snippet to pom.xml:

```xml
	<dependencies>
		...

		<dependency>
			<groupId>com.nmote.smpp</groupId>
			<artifactId>nmote-smpp</artifactId>
			<version>1.7.2</version>
		</dependency>

	</dependencies>
```

Changes
-------

1.7.2 Upgraded commons collections to 3.2.2. Version 3.2.1 has a CVSS 10.0 vulnerability.
      Thanks to Glenn Lewis for bringing the issue up.

1.7.1 New sample class (SmscConnection), starting point for integration into other projects.
      Added MessageState constants. Fixed DCS UCS-2 unicode decoding/encoding. Few javadoc typos and few
      helper methods added.

Building
--------
To produce a JAR you will need apache maven installed. Run:

> mvn clean package

Author contact and support
--------------------------
For any further information please contact Vjekoslav Nesek (vnesek@nmote.com)

SMPP Addressing
===============

SMPP uses addresses to distinguish senders and recipients of messages. Addresses are also used to send messages to predefined distribution lists. SMPP addresses support multiple address types, for example MSISDNs, abbreviated numbers, IP network addresses and
Nmote SMPP library supports SME (Short Message Entity) addresses and distribution lists.

SMPPAddress
-----------
Normal SME address is modeled after GSM address and it is characterized by three fields: NPI, TON and address field.
SME address is represented by com.nmote.smpp.SMPPAddress class.

NPI (Network Plan Identificator) specifies network plan for address. SMPPAddress contains constants for all standardized NPI values.

TON Type Of Number defines a type of number for SME address. SMPPAddress contains constants for all standardized TON values.

Protocol Data Units
===================
SMPP communication is based on passing PDUs (Protocol Data Unit) between SMPP client and SMPP server. You can think of PDU as a message encoded according to SMPP protocol rules.

Request-Response and One-Way
----------------------------
Most SMPP PDUs come in pairs. One PDU is a request PDU that invokes some operation on a server. When server finishes operation, results are transmitted back as a response PDU. For example, BindReceiverPDU class represents a request sent from ESME to SMSC requesting SMSC to bind ESME as SMPP receiver. Response PDU is represented with BindReceiverRespPDU class.
Some SMPP operations are represented with a single PDU. This communication model is called one-way. For example, AlertNotificationPDU class represents a one-way message.

PDU Classes
-----------
Package com.nmote.smpp contains a class for each PDU defined in SMPP 5.0 specification. PDU classes extend base class AbstractPDU which implements Cloneable and Serializable interfaces. Naming convention for PDU classes is PDU name in a camel case followed with PDU for request and one-way PDUs and RespPDU for response PDUs.

```
Operation	Request PDU class	Response PDU Class
bind_receiver	BindReceiverPDU	BindReceiverRespPDU
submit_sm	SubmitSmPDU	SubmitSmPDU
alert_notification	AlertNotificationPDU
```

Sequence Numbers
----------------
Each PDU sent over network has associated sequence number. Sequence numbers are monotonically raising numbers unique in a scope of a single SMPP session (connection). Session objects assign sequence number to a request PDU when PDU is sent over network.
Sequence number is used to match responses to requests. You can call getSequence() on a PDU object to get it’s sequence number.

Command ID
----------
PDU types in a SMPP protocol are distinguished by a command id. You can get command id of an PDU object by calling getCommandId().

Status Code
-----------
All response PDUs have a status code that signifies if operation was successful or not. Request and one-way PDUs doesn’t have a status code. getStatus() call will give you a status of a response PDU received from remote entity. When handling requests from remote entity, call setStatus(int status) to set a status code.
ESMEStatus class contains constants defining status codes from SMPP specification and utility method for converting status code to string.

Mandatory Parameters
--------------------
PDU parameters that are mandatory for a given PDU. Mandatory parameters are represented as Java Beans properties with associated getters and setters. Check javadocs for a PDU class to see which mandatory parameters are available.

Optional Parameters
-------------------
Optional Parameters are represented with a list of Parameter objects. Each SMPP optional parameter has a tag (number code) defining parameter meaning and type. Parameter objects have utility methods for reading and writing most common SMPP parameter types. For rarely used parameter types application must construct a parameter value in a byte array and set it directly on a parameter object.

Sessions
========
Sessions represent clients and servers of SMPP communication. Sessions come in two flavors:
ESME Session is a session running in an ESME that communicates (over the Link) with SMSC session running in a SMSC. Normally you will create an ESME Session to establish communication with SMSC for send and/or receiving of SMS messages.
SMSC Session is running in an SMSC that communicates with ESME Session running in a client. While SMSC Session isn’t interesting to developers wishing to create SMPP clients, it is of utility to developers developing SMSC simulators, SMPP gateways or SMPP proxies.

Asynchronous Calls
------------------
SMPP is asynchronous protocol. New request PDUs can be sent before responses are received. Responses can come out of order. Session objects can execute commands either synchronously (blocking) or asynchronously (non-blocking). Multiple threads can make parallel blocking requests at a same time.
While blocking calls are easier to implement, for maximum performance callback mechanism and asynchronous calling is recommended. To receive responses clients have to implement CommandStateListener. Adapter class CommandStateAdapter can simplify implementation of CommandStateListener.
Example of asynchronous message sending:

```java
// Create a submit_sm command
SubmitSmPDU req = new SubmitSmPDU();
req.setDestAddr(new SMPPAddress("438893248"));
req.setSourceAddr(new SMPPAddress("438876544"));
req.setShortMessage(DCS.toGSM("This is a sample text"));

Command submit = new Command(req);

// Set a CommandStateListener that will be called when
// command completes execution
submit.setCommandStateListener(new CommandStateAdapter() {
	public void executed(Command cmd) {
		SubmitSmRespPDU resp = (SubmitSmRespPDU) cmd.getResponse();
		System.out.println("Status " + resp.getStatus());
		System.out.println("Message ID " + resp.getMessageId());
	}
});

// Execute command asynchronously
smsc.executeAsync(submit);
````
Use executeAsync(Command cmd) call to asynchronously execute command.

Synchronous Calls
-----------------
Use execute(Command cmd) or execute(Command cmd, long timeout) calls to synchronously execute command. These calls will block until response is received from a remote entity or command times out.
If no response is received in a specified amount of time CommandTimedOutException is throwed.


