/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions.coordination;

import org.eclipse.persistence.sessions.coordination.ServiceId;

/**
 * <p>
 * <b>Purpose</b>: A structured message object to announce a new RCM service
 * instance becoming available
 * <p>
 * <b>Description</b>: This object is sent over the multicast by a service wanting
 * to join the EclipseLink cluster. It is received by all other services subscribing
 * to the same channel. Receipt of this announcement triggers an exchange protocol
 * between the sending and receiving services to establish communications with all
 * of the other services on the channel.
 * <p>
 */
public class ServiceAnnouncement {

    /** The id information of the sending service */
    ServiceId serviceId;

    /**
     * INTERNAL:
     * Constructor to initialize a new instance when receiving a message
     */
    public ServiceAnnouncement(byte[] bytes) {
        readFromBytes(bytes);
    }

    /**
     * INTERNAL:
     * Constructor to initialize a new instance when creating a message
     */
    public ServiceAnnouncement(ServiceId newServiceId) {
        this.serviceId = newServiceId;
    }

    /**
     * INTERNAL:
     * Initialize the instance fields from the serialized bytes.
     *
     * Assumptions:
     *    - Same character converters exist on the reading and storing sides
     *    - Strings are not greater than 255 bytes (bytes, not characters)
     *
     * Byte storage:
     *    - 1 byte to store length of String that is to follow
     *    - String of 'length' bytes follows
     */
    public void readFromBytes(byte[] bytes) {
        serviceId = new ServiceId();
        int curPos = 0;

        // Read the channel	
        int channelLength = bytes[curPos];
        curPos++;
        serviceId.setChannel(new String(bytes, curPos, channelLength));
        curPos += channelLength;

        // Read the id	
        int idLength = bytes[curPos];
        curPos++;
        serviceId.setId(new String(bytes, curPos, idLength));
        curPos += idLength;

        // Read the URL	
        int urlLength = bytes[curPos];
        curPos++;
        if (urlLength > 0) {
            serviceId.setURL(new String(bytes, curPos, urlLength));
        }
    }

    /**
     * INTERNAL:
     * Convert the instance attributes to serialized bytes.
     *
     * Assumptions:
     *    - Same character converters exist on the reading and storing sides
     *    - channel, id and converted to bytes < 256 bytes each
     *
     * Byte storage:
     *    - 1 byte to store length of String that is to follow
     *    - String of 'length' bytes follows
     */
    public byte[] toBytes() {
        byte[] bytes;
        int curPos = 0;

        // Convert the strings to bytes
        byte[] channelBytes = serviceId.getChannel().getBytes();
        int channelLength = channelBytes.length;

        byte[] idBytes = serviceId.getId().getBytes();
        int idLength = idBytes.length;
        
        byte[] urlBytes = null;
        int urlLength = 0;
        if (serviceId.getURL() == null) {
            urlBytes = new byte[]{};
        } else {
            urlBytes = serviceId.getURL().getBytes();
            urlLength = urlBytes.length;
        }

        // Create the byte array to hold the data
        bytes = new byte[channelLength + idLength + urlLength + 3];

        // Store the fields in the byte array
        bytes[curPos] = (byte)channelLength;
        curPos++;
        System.arraycopy(channelBytes, 0, bytes, curPos, channelLength);
        curPos += channelLength;

        bytes[curPos] = (byte)idLength;
        curPos++;
        System.arraycopy(idBytes, 0, bytes, curPos, idLength);
        curPos += idLength;

        bytes[curPos] = (byte)urlLength;
        curPos++;
        System.arraycopy(urlBytes, 0, bytes, curPos, urlLength);
        return bytes;
    }

    /**
     * INTERNAL:
     * Return the id of the service sending this announcement
     */
    public ServiceId getServiceId() {
        return serviceId;
    }
}
