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
package org.eclipse.persistence.internal.sessions.factories.model.transport.discovery;


/**
 * INTERNAL:
 */
public class DiscoveryConfig {
    private String m_multicastGroupAddress;
    private int m_multicastPort;
    private int m_announcementDelay;
    private int m_packetTimeToLive;

    public DiscoveryConfig() {
    }

    public void setMulticastGroupAddress(String multicastGroupAddress) {
        m_multicastGroupAddress = multicastGroupAddress;
    }

    public String getMulticastGroupAddress() {
        return m_multicastGroupAddress;
    }

    public void setMulticastPort(int multicastPort) {
        m_multicastPort = multicastPort;
    }

    public int getMulticastPort() {
        return m_multicastPort;
    }

    public void setAnnouncementDelay(int announcementDelay) {
        m_announcementDelay = announcementDelay;
    }

    public int getAnnouncementDelay() {
        return m_announcementDelay;
    }

    public int getPacketTimeToLive() {
        return m_packetTimeToLive;
    }

    public void setPacketTimeToLive(int packetTimeToLive) {
        m_packetTimeToLive = packetTimeToLive;
    }
}
