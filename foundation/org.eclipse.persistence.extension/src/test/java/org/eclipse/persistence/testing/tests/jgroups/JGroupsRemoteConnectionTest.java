/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - Initial implementation
package org.eclipse.persistence.testing.tests.jgroups;

import static org.junit.Assert.assertEquals;

import org.eclipse.persistence.internal.sessions.coordination.jgroups.JGroupsRemoteConnection;
import org.eclipse.persistence.sessions.coordination.Command;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;
import org.eclipse.persistence.sessions.coordination.ServiceId;
import org.eclipse.persistence.sessions.coordination.TransportManager;

import org.jgroups.JChannel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class JGroupsRemoteConnectionTest {

    private final String COMMAND_CONTENT = "testCommand";
    private final String CLUSTER_NAME = "ChatCluster";
    private static final long DELIVERY_TIMEOUT_MS = 5000;

    private JChannel jChannel = null;
    private JGroupsRemoteConnection remoteConnection = null;
    private JGroupsCommandProcessor commandProcessor = null;

    @Before
    public void init() throws Exception {
        commandProcessor = new JGroupsCommandProcessor();
        RemoteCommandManager remoteCommandManager = new RemoteCommandManager(commandProcessor);
        TransportManager senderTransportManager = new JGroupsTestTransportManager();
        remoteCommandManager.setTransportManager(senderTransportManager);
        jChannel = new JChannel();
        remoteConnection = new JGroupsRemoteConnection(remoteCommandManager, jChannel, true);
        jChannel.connect(CLUSTER_NAME);
    }

    @After
    public void shutdown() throws Exception {
        remoteConnection.close();
        jChannel.close();
    }

    /**
     * Test JGroupsRemoteConnection with Command passed as a Object.
     */
    @Test
    public void testObjectCommand() throws Exception {
        ServiceId serviceId = new ServiceId();
        serviceId.setChannel(RemoteCommandManager.DEFAULT_CHANNEL);
        Command testCommand = new JGroupsTestCommand(serviceId, COMMAND_CONTENT);
        jChannel.connect(CLUSTER_NAME);
        remoteConnection.executeCommand(testCommand);
        awaitCommandContent();
        assertEquals(COMMAND_CONTENT, commandProcessor.getCommandContent());
    }

    /**
     * Test JGroupsRemoteConnection with Command passed as an array of bytes.
     */
    @Test
    public void testByteArrayCommand() throws Exception {
        ServiceId serviceId = new ServiceId();
        serviceId.setChannel(RemoteCommandManager.DEFAULT_CHANNEL);
        Command testCommand = new JGroupsTestCommand(serviceId, COMMAND_CONTENT);
        byte[] testCommandSerialized = serializeCommand(testCommand);
        jChannel.connect(CLUSTER_NAME);
        remoteConnection.executeCommand(testCommandSerialized);
        awaitCommandContent();
        assertEquals(COMMAND_CONTENT, commandProcessor.getCommandContent());
    }

    /**
     * JGroups 5.5 removed the FLUSH protocol and {@code JChannel.startFlush()}, which
     * this test previously used as a delivery barrier. Wait for the asynchronous
     * delivery to complete instead.
     */
    private void awaitCommandContent() throws InterruptedException {
        long deadline = System.currentTimeMillis() + DELIVERY_TIMEOUT_MS;
        while (commandProcessor.getCommandContent() == null && System.currentTimeMillis() < deadline) {
            Thread.sleep(10);
        }
    }

    private byte[] serializeCommand(Command command) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        byte[] result = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(command);
            out.flush();
            result = bos.toByteArray();
            bos.close();
        } catch (IOException ex) {
            // ignore exception
        }
        return result;
    }
}
