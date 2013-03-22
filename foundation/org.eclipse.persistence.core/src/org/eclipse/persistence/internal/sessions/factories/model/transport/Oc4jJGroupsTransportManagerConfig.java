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
package org.eclipse.persistence.internal.sessions.factories.model.transport;

/**
 * INTERNAL:
 */
public class Oc4jJGroupsTransportManagerConfig extends TransportManagerConfig {
    private String transportManagerClassName;
    private boolean m_useSingleThreadedNotification;
    private String m_topicName;

    public Oc4jJGroupsTransportManagerConfig() {
        super();
        transportManagerClassName = "org.eclipse.persistence.sessions.coordination.jgroups.oc4j.Oc4jJGroupsTransportManager";
    }

    public void setUseSingleThreadedNotification(boolean useSingleThreadedNotification) {
        m_useSingleThreadedNotification = useSingleThreadedNotification;
    }

    public boolean useSingleThreadedNotification() {
        return m_useSingleThreadedNotification;
    }
    
    public void setTopicName(String topicName) {
        m_topicName = topicName;
    }

    public String getTopicName() {
        return m_topicName;
    }

    public String getTransportManagerClassName() {
        return transportManagerClassName;
    }
}
