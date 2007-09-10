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
public class JMSClusteringConfig extends JNDIClusteringServiceConfig {
    private String m_jmsTopicConnectionFactoryName;
    private String m_jmsTopicName;

    public JMSClusteringConfig() {
        super();
    }

    public void setJMSTopicConnectionFactoryName(String jmsTopicConnectionFactoryName) {
        m_jmsTopicConnectionFactoryName = jmsTopicConnectionFactoryName;
    }

    public String getJMSTopicConnectionFactoryName() {
        return m_jmsTopicConnectionFactoryName;
    }

    public void setJMSTopicName(String jmsTopicName) {
        m_jmsTopicName = jmsTopicName;
    }

    public String getJMSTopicName() {
        return m_jmsTopicName;
    }
}