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
 *     dminsky - changed id to use a String UUID instead of String hashcode()
 ******************************************************************************/  
package org.eclipse.persistence.sessions.coordination;

import java.io.Serializable;
import java.util.UUID;

/**
 * <p>
 * <b>Purpose</b>: Encapsulate the information that uniquely identifies a specific
 * remote command service instance.
 * <p>
 * <b>Description</b>: A service instance consists primarily of a CommandManager
 * and its associated components including its CommandProcessor, CommandConverter,
 * DiscoveryManager and TransportManager. Whenever a service instance sends out an
 * announcement or remote command to the cluster the service id is included in the
 * message.
 *
 * @see org.eclipse.persistence.internal.sessions.coordination.ServiceAnnouncement
 * @author Steven Vo
 * @since OracleAS TopLink 10<i>g</i> (9.0.4)
 */
public class ServiceId implements Serializable {

    /** Generated unique id to distinguish the service instance from all others */
    private String id;

    /** The logical channel that the service instance subscribes to */
    private String channel;

    /** Url of the service instance */
    private String url;

    /* Cached display string to prevent rebuilding */
    private String displayString;

    /** This $HOST token indicate that the host ip of the URL should be replaced at runtime by user input */
    public final static String HOST_TOKEN = "$HOST";

    /** This $HOST token indicate that the host ip of the URL should be replaced at runtime by user input */
    public final static String PORT_TOKEN = "$PORT";

    public ServiceId() {
        super();
        // Use UUID value instead of hashcode value for id
        id = String.valueOf(UUID.randomUUID());
    }

    public ServiceId(String channel, String id, String url) {
        this.channel = channel;
        this.id = id;
        this.url = url;
    }

    /**
     * INTERNAL:
     * Return the URL for the service
     */
    public String getURL() {
        return url;
    }

    /**
     * INTERNAL:
     * Set the URL for the service
     */
    public void setURL(String newUrl) {
        url = newUrl;
        displayString = null;
    }

    /**
     * INTERNAL:
     * Get the unique identifier for the service
     */
    public String getId() {
        return this.id;
    }

    /**
     * INTERNAL:
     * Set the unique identifier for the service
     */
    public void setId(String newId) {
        this.id = newId;
        displayString = null;
    }

    /**
     * INTERNAL:
     * Return the logical channel that this service subscribes to
     */
    public String getChannel() {
        return channel;
    }

    /**
     * INTERNAL:
     * Set the logical channel that this service subscribes to
     */
    public void setChannel(String newChannel) {
        channel = newChannel;
        displayString = null;
    }

    public String toString() {
        if (displayString == null) {
            displayString = "Service[" + channel + ", " + id + ", " + url + "]";
        }
        return displayString;
    }
}
