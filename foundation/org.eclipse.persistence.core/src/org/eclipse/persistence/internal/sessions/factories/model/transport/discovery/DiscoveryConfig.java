/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
