/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
