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
package org.eclipse.persistence.testing.tests.sessionsxml;

import java.util.Enumeration;
import java.util.Vector;

import javax.naming.Context;

import org.eclipse.persistence.internal.security.SecurableObjectHolder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;
import org.eclipse.persistence.sessions.coordination.rmi.RMITransportManager;
import org.eclipse.persistence.testing.framework.TestErrorException;


public class RcmWithRmiAndJndiTest extends RcmBasicTest {

    public RcmWithRmiAndJndiTest() {
        sessionsXmlFileName = "org/eclipse/persistence/testing/models/sessionsxml/sessions_rcm_rmi_jndi.xml";
    }

    public void verify() {
        RemoteCommandManager rcm = (RemoteCommandManager)((AbstractSession)loadedSession).getCommandManager();

        RMITransportManager transportMgr = (RMITransportManager)rcm.getTransportManager();

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
        if (rcm.shouldPropagateAsynchronously()) {
            errors.add("send mode is asynchronous");
        }

        // test Discovery Manager settings
        if (!rcm.getDiscoveryManager().getMulticastGroupAddress().equals("new_address")) {
            errors.add("DM multicast address = " + rcm.getDiscoveryManager().getMulticastGroupAddress());
        }
        if (rcm.getDiscoveryManager().getMulticastPort() != 3333) {
            errors.add("DM multicast port = " + rcm.getDiscoveryManager().getMulticastPort());
        }
        if (rcm.getDiscoveryManager().getAnnouncementDelay() != 1111) {
            errors.add("DM announcement delay = " + rcm.getDiscoveryManager().getAnnouncementDelay());
        }
        // naming services
        if (transportMgr.getNamingServiceType() != RMITransportManager.JNDI_NAMING_SERVICE) {
            errors.add("Naming service type (not JNDI) = " + transportMgr.getNamingServiceType());
        }
        if (!rcm.getUrl().equals("new_jndi_url")) {
            errors.add("Url = " + rcm.getUrl());
        }

        //  transport manager
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
            String errorString = "The following RCM elements's value do not match their expected values:";
            for (Enumeration enumtr = errors.elements(); enumtr.hasMoreElements(); ) {
                errorString += "\n   " + enumtr.nextElement();
            }
            throw new TestErrorException(errorString);
        }
    }

}
