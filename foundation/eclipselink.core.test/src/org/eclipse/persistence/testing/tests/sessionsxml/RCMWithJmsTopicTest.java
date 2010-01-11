/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.sessionsxml;

import java.util.Enumeration;
import java.util.Vector;

import javax.naming.Context;

import org.eclipse.persistence.internal.security.SecurableObjectHolder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;
import org.eclipse.persistence.sessions.coordination.jms.JMSTopicTransportManager;
import org.eclipse.persistence.testing.framework.TestErrorException;


public class RCMWithJmsTopicTest extends RcmBasicTest {
    public RCMWithJmsTopicTest() {
        sessionsXmlFileName = "org/eclipse/persistence/testing/models/sessionsxml/sessions_rcm_jms_topic.xml";
    }

    public static void main(String[] args) {
        RCMWithJmsTopicTest test = new RCMWithJmsTopicTest();
        test.setup();
        test.verify();
    }

    public void verify() {
        RemoteCommandManager rcm = (RemoteCommandManager)((AbstractSession)loadedSession).getCommandManager();

        JMSTopicTransportManager transportMgr = (JMSTopicTransportManager)rcm.getTransportManager();
        Vector errors = new Vector();

        if (!rcm.getChannel().equals("new_channel")) {
            errors.add("channel = " + rcm.getChannel());
        }
        if (!((AbstractSession)loadedSession).shouldPropagateChanges()) {
            errors.add("cache sync is not enable");
        }
        if (rcm.getTransportManager().shouldRemoveConnectionOnError()) {
            errors.add("remove connection on error");
        }
        if (!rcm.shouldPropagateAsynchronously()) {
            errors.add("send mode is synchronous but should always be asynchronous by default.");
        }
        // naming services
        if (transportMgr.getTopicHostUrl() == null || !(transportMgr.getTopicHostUrl().equals("ormi://jms_topic_host"))) {
            errors.add("topic-host-url = " + transportMgr.getTopicHostUrl());
        }

        if (!(transportMgr.getTopicConnectionFactoryName().equals("test-topic-connection-factory-name"))) {
            errors.add("test-topic-connection-factory-name = " + transportMgr.getTopicConnectionFactoryName());
        }

        if (!(transportMgr.getTopicName().equals("test-topic-name"))) {
            errors.add("test-topic-name = " + transportMgr.getTopicName());
        }

        if (transportMgr.getTopicHostUrl() == null || !(transportMgr.getTopicHostUrl().equals("ormi://jms_topic_host"))) {
            errors.add("topic-host-url = " + transportMgr.getTopicHostUrl());
        }

        if (!rcm.getUrl().equals("ormi://jms_topic_host")) {
            errors.add("Url = " + rcm.getUrl());
        }

        String userName = (String)transportMgr.getRemoteContextProperties().get(Context.SECURITY_PRINCIPAL);
        if (userName == null || !userName.equals("new_user_name")) {
            errors.add("user name = " + userName);
        }
        String password = (String)transportMgr.getRemoteContextProperties().get(Context.SECURITY_CREDENTIALS);

        // decrypt password using default encryption
        password = new SecurableObjectHolder().getSecurableObject().decryptPassword(password);
        if (password == null || !password.equals("new_password")) {
            errors.add("password = " + password);
        }
        String contextFactory = (String)transportMgr.getRemoteContextProperties().get(Context.INITIAL_CONTEXT_FACTORY);
        if (contextFactory == null || !contextFactory.equals("new_initial_context_factory_name")) {
            errors.add("initial context factory name = " + contextFactory);
        }

        // extra properties
        String propertyValue1 = (String)transportMgr.getRemoteContextProperties().get("name1");
        String propertyValue2 = (String)transportMgr.getRemoteContextProperties().get("name2");

        if (propertyValue1 == null || !propertyValue1.equals("value1")) {
            errors.add("extra property name =  name1, value = " + propertyValue1);
        }
        if (propertyValue2 == null || !propertyValue2.equals("value2")) {
            errors.add("extra property name =  name2, value = " + propertyValue2);
        }

        if (!errors.isEmpty()) {
            String errorString = "The following RCM elements do not match their expected values:";
            for (Enumeration enumtr = errors.elements(); enumtr.hasMoreElements(); ) {
                errorString += "\n   " + enumtr.nextElement();
            }
            throw new TestErrorException(errorString);
        }
    }

}
