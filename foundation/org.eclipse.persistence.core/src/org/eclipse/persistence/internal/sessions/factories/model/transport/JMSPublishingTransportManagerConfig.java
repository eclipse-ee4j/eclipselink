/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     cdelahun - Bug 214534: added for JMSPublishingTransportManager configuration via session.xml
 ******************************************************************************/
package org.eclipse.persistence.internal.sessions.factories.model.transport;

import org.eclipse.persistence.internal.sessions.factories.model.transport.naming.JNDINamingServiceConfig;

/**
 * INTERNAL:
 * 
 */
public class JMSPublishingTransportManagerConfig extends TransportManagerConfig  {

    private String m_topicHostURL;
    private String m_topicConnectionFactoryName;
    private String m_topicName;
    private JNDINamingServiceConfig m_jndiNamingServiceConfig;

    public JMSPublishingTransportManagerConfig() {
        super();
    }

    public void setTopicHostURL(String topicHostURL) {
        m_topicHostURL = topicHostURL;
    }

    public String getTopicHostURL() {
        return m_topicHostURL;
    }

    public void setTopicConnectionFactoryName(String topicConnectionFactoryName) {
        m_topicConnectionFactoryName = topicConnectionFactoryName;
    }

    public String getTopicConnectionFactoryName() {
        return m_topicConnectionFactoryName;
    }

    public void setTopicName(String topicName) {
        m_topicName = topicName;
    }

    public String getTopicName() {
        return m_topicName;
    }

    public void setJNDINamingServiceConfig(JNDINamingServiceConfig jndiNamingServiceConfig) {
        m_jndiNamingServiceConfig = jndiNamingServiceConfig;
    }

    public JNDINamingServiceConfig getJNDINamingServiceConfig() {
        return m_jndiNamingServiceConfig;
    }
}
