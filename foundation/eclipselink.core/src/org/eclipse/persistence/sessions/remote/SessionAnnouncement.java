/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sessions.remote;

import java.io.*;
import org.eclipse.persistence.exceptions.SynchronizationException;

/**
 * INTERNAL:
 * Message sent over the multicast group that identifies
 * the session to which this announcement applies. Also
 * specified in each announcement is the host from which
 * this announcement originates, and an id identifiying
 * the sending VM on that host.
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.sessions.coordination.CommandManager}
 */
public class SessionAnnouncement implements Serializable {

    /**
     * This varible stores the unique ID for the session that is sending this message
     */
    String sessionId;

    /**
     * This is the URL of the JNDI server where the dispatcher will be stored
     */
    private String jndiHostURL;

    /** Used to store the application name in the case of an application server */
    private String applicationName;

    /**
     * INTERNAL:
     * Builds a SessionAnnouncement to use in the communication framework.
     * This constructor is used when receiving a message
     */
    public SessionAnnouncement(byte[] bytes) throws SynchronizationException {
        try {
            readFromBytes(bytes);
        } catch (IndexOutOfBoundsException iobEx) {
            throw SynchronizationException.errorUnmarshallingMsg(sessionId);
        }
    }

    /**
     * INTERNAL:
     * Builds a SessionAnnouncement to use in the communication framework.
     * This constructor is used when creating a message
     */
    public SessionAnnouncement(String sessionIdentifier, String jndiHostURL, String applicationName) {
        this.sessionId = sessionIdentifier;
        this.jndiHostURL = jndiHostURL;
        this.applicationName = applicationName;
    }

    /**
     * PUBLIC:
     *    This method stores the session ID for this announcement
     *    This value represents the Unique identifier for the session
     * @param sessionId
     * @SBGen Method set sessionId
     */
    public void setSessionId(String sessionId) {
        // SBgen: Assign variable
        this.sessionId = sessionId;
    }

    /**
     * PUBLIC:
     *    This method must return the value that will uniquely identify the Session that is sending the
     * message and must be the value that is used to store the dispatcher in the JNDI service.
     * @SBGen Method get sessionId
     */
    public String getSessionId() {
        // SBgen: Get variable
        return (sessionId);
    }

    /**
    * INTERNAL:
    * Initialize the instance fields from the bytes.
    *
    * Assumptions:
    *    - Same character converters exist on the reading and storing sides
    *
    * Byte map:
    *
    *    1 byte = size of session id string (sessionIdLength)
    *    sessionIdLength bytes = session id string
    *    1 byte = size of JNDI Service URL (jndiServiceURLLength)
    *    jndiServiceURLLength bytes = JNDI Service URL string
    */
    public void readFromBytes(byte[] bytes) {
        int curPos = 0;

        // Read the session id	
        int sessionIdLength = bytes[curPos];
        curPos++;
        this.sessionId = new String(bytes, curPos, sessionIdLength);
        curPos += sessionIdLength;

        // Read the host name	
        int jndiServiceURLLength = bytes[curPos];
        curPos++;
        this.jndiHostURL = new String(bytes, curPos, jndiServiceURLLength);
        curPos += jndiServiceURLLength;

        int applicationNameLength = bytes[curPos];
        curPos++;
        this.applicationName = new String(bytes, curPos, applicationNameLength);
        curPos += applicationNameLength;
    }

    /**
    * Convert the instance fields to bytes.
    *
    * Assumptions:
    *    - Same character converters exist on the reading and storing sides
    *    - host name converted to bytes < 256 bytes
    *
    * Byte map:
    *
    *    1 byte = size of session id string (sessionIdLength)
    *    sessionIdLength bytes = session id string
    *    1 byte = size of host name (nameLength)
    *    nameLength bytes = host name string
    */
    public byte[] toBytes() {
        byte[] bytes;
        int curPos = 0;

        // Convert the strings to bytes
        byte[] sessionIdBytes = sessionId.getBytes();
        int sessionIdLength = sessionIdBytes.length;

        byte[] hostNameBytes = jndiHostURL.getBytes();
        int hostNameLength = jndiHostURL.length();

        byte[] applicationNameBytes = this.applicationName.getBytes();
        int applicationNameLength = applicationNameBytes.length;

        // Create the byte array to hold the data
        bytes = new byte[sessionIdLength + hostNameLength + applicationNameLength + 3];

        // Store the fields in the byte array
        bytes[curPos] = (byte)sessionIdLength;
        curPos++;
        System.arraycopy(sessionIdBytes, 0, bytes, curPos, sessionIdLength);
        curPos += sessionIdLength;

        bytes[curPos] = (byte)hostNameLength;
        curPos++;
        System.arraycopy(hostNameBytes, 0, bytes, curPos, hostNameLength);
        curPos += hostNameLength;

        bytes[curPos] = (byte)applicationNameLength;
        curPos++;
        System.arraycopy(applicationNameBytes, 0, bytes, curPos, applicationNameLength);

        return bytes;
    }

    public String getApplicationName() {
        return this.applicationName;
    }

    /**
     * PUBLIC: Use this method to retreive the JNDI service URL for transmission
     * @SBGen Method get jndiHostURL
     */
    public String getJNDIHostURL() {
        // SBgen: Get variable
        return (jndiHostURL);
    }

    /**
     * PUBLIC: Use this method to set the JNDI service URL for transmission
     * @SBGen Method set jndiHostURL
     */
    public void setJNDIHostURL(String jndiHostURL) {
        // SBgen: Assign variable
        this.jndiHostURL = jndiHostURL;
    }
}