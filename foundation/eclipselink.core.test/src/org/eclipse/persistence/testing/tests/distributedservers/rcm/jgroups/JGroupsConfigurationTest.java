/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.distributedservers.rcm.jgroups;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.coordination.RemoteConnection;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;
import org.eclipse.persistence.sessions.coordination.TransportManager;
import org.eclipse.persistence.testing.tests.distributedservers.rcm.ConfigurableCacheSyncDistributedTest;
import org.junit.Assert;

import java.lang.reflect.Field;

/**
 * Test to ensure JGroups configuration file passed is used
 */
public class JGroupsConfigurationTest extends ConfigurableCacheSyncDistributedTest {
    @Override
    protected void test() throws Throwable {
        AbstractSession session = getAbstractSession();
        
        RemoteCommandManager rcm = new RemoteCommandManager(session);        
        TransportManager transport = (TransportManager)Class.forName("org.eclipse.persistence.sessions.coordination.jgroups.JGroupsTransportManager").newInstance();
        rcm.setTransportManager(transport);
        transport.createLocalConnection();
        RemoteConnection connection = transport.getConnectionToLocalHost();
        try {
            int multicastPort = getMulticastPort(connection);
            Assert.assertEquals("Default multicast port different than expected", 45588, multicastPort);
        } catch (Exception x) {
            Assert.fail("Failed to retrieve multicast port: " + x.getMessage());
        } finally {
            connection.close();
        }

        rcm = new RemoteCommandManager(session);
        transport = (TransportManager) Class.forName("org.eclipse.persistence.sessions.coordination.jgroups.JGroupsTransportManager").newInstance();
        transport.setConfig("org/eclipse/persistence/testing/config/distributedservers/rcm/jgroups/jgroups-udp-config.xml");
        rcm.setTransportManager(transport);
        transport.createLocalConnection();
        connection = transport.getConnectionToLocalHost();
        try {
            int multicastPort = getMulticastPort(connection);
            Assert.assertEquals("Configured multicast port different than expected", 45678, multicastPort);
        } catch (Exception x) {
            Assert.fail("Failed to retrieve multicast port: " + x.getMessage());
        } finally {
            connection.close();
        }
    }

    private int getMulticastPort(RemoteConnection connection) throws Exception {
        Field field = Class.forName("org.eclipse.persistence.internal.sessions.coordination.jgroups.JGroupsRemoteConnection").getDeclaredField("channel");
        field.setAccessible(true);
        Object channel = field.get(connection);
        Object protocolStack = Class.forName("org.jgroups.JChannel").getMethod("getProtocolStack").invoke(channel);
        Object bottomProtocol = Class.forName("org.jgroups.stack.ProtocolStack").getMethod("getBottomProtocol").invoke(protocolStack);
        return (int) Class.forName("org.jgroups.protocols.UDP").getMethod("getMulticastPort").invoke(bottomProtocol);
    }
}
