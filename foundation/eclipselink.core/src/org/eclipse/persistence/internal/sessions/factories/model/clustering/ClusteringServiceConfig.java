/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions.factories.model.clustering;


/**
 * INTERNAL:
 */
public abstract class ClusteringServiceConfig {
    private Integer m_multicastPort;
    private Integer m_packetTimeToLive;
    private String m_multicastGroupAddress;
    private String m_namingServiceURL;

    protected ClusteringServiceConfig() {
    }

    public String getMulticastGroupAddress() {
        return m_multicastGroupAddress;
    }

    public Integer getMulticastPort() {
        return m_multicastPort;
    }

    public String getNamingServiceURL() {
        return m_namingServiceURL;
    }

    public Integer getPacketTimeToLive() {
        return m_packetTimeToLive;
    }

    public void setMulticastGroupAddress(String multicastGroupAddress) {
        m_multicastGroupAddress = multicastGroupAddress;
    }

    public void setMulticastPort(Integer multicastPort) {
        m_multicastPort = multicastPort;
    }

    public void setNamingServiceURL(String namingServiceURL) {
        m_namingServiceURL = namingServiceURL;
    }

    public void setPacketTimeToLive(Integer packetTimeToLive) {
        m_packetTimeToLive = packetTimeToLive;
    }
}