/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     cdelahun - Bug 214534: added for JMSPublishingTransportManager configuration via session.xml
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
