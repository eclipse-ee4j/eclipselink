/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.internal.sessions.coordination.jgroups;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.coordination.broadcast.BroadcastRemoteConnection;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;
import org.eclipse.persistence.sessions.serializers.Serializer;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

/**
 * <p>
 * <b>Purpose</b>: Define the implementation of the abstract RemoteConnection for JGroups.
 * <p>
 * <b>Description</b>:  Executing commands implementation of RemoteConnection is done via JGroups JChannel.
 *
 * @author James Sutherland
 * @since EclipseLink 2.5
 */
public class JGroupsRemoteConnection extends BroadcastRemoteConnection {

    private static final long serialVersionUID = -2285543305296840902L;

    protected transient JChannel channel;
    // indicates whether it's a local connection.
    protected boolean isLocal;

    /**
     * INTERNAL:
     * Constructor creating either a local or external connection.
     */
    public JGroupsRemoteConnection(RemoteCommandManager rcm, JChannel channel, boolean isLocalConnectionBeingCreated) {
        super(rcm);
        this.channel = channel;
        this.isLocal = isLocalConnectionBeingCreated;
        rcm.logDebug("creating_broadcast_connection", getInfo());
        try {
            if (isLocalConnectionBeingCreated) {
                // it's a local connection
                this.channel.setReceiver(new ReceiverAdapter() {
                    @Override
                    public void receive(Message message) {
                        onMessage(message);
                    }
                });
                rcm.logDebug("broadcast_connection_created", getInfo());
            }
        } catch (RuntimeException ex) {
            rcm.logDebug("failed_to_create_broadcast_connection", getInfo());
            close();
            throw ex;
        }
    }

    /**
     * Creates local connections.
     */
    public JGroupsRemoteConnection(RemoteCommandManager rcm){
        super(rcm);
        this.isLocal = true;
    }

    /**
     * INTERNAL:
     * Indicates whether connection is local (subscriber)
     * or external (publisher).
     */
    public boolean isLocal() {
        return isLocal;
    }

    /**
     * INTERNAL:
     * Execute the remote command. The result of execution is returned.
     * This method is used only by external (publishing) connection.
     */
    @Override
    protected Object executeCommandInternal(Object command) throws Exception {
        Message message = null;
        if (command instanceof byte[]) {
            message = new Message(null, null, (byte[])command);
        } else {
            message = new Message(null, null, command);
        }

        Object[] debugInfo = null;
        if(this.rcm.shouldLogDebugMessage()) {
            debugInfo = logDebugBeforePublish(null);
        }

        this.channel.send(message);

        // debug logging is on
        if (debugInfo != null) {
            logDebugAfterPublish(debugInfo, null);
        }

        return null;
    }

    /**
     * INTERNAL:
     * Process received JGroups message.
     * This method is used only by local (listening) connection.
     */
    public void onMessage(Message message) {
        String messageId = "";
        if (rcm.shouldLogDebugMessage()) {
            logDebugOnReceiveMessage(null);
            logDebugMessage(message);
        }

        Object object = null;
        try {
            Serializer serializer = this.rcm.getSerializer();
            if (serializer != null) {
                object = serializer.deserialize(message.getBuffer(), (AbstractSession)this.rcm.getCommandProcessor());
            } else {
                object = message.getObject();
            }
        } catch (Exception exception) {
            failDeserializeMessage(null, exception);
            return;
        }

        processReceivedObject(object, messageId);
    }

    /**
     * INTERNAL:
     * Indicates whether all the resources used by connection are freed after close method returns.
     */
    @Override
    protected boolean areAllResourcesFreedOnClose() {
        return !isLocal();
    }

    /**
     * INTERNAL:
     * This method is called by close method.
     */
    @Override
    protected void closeInternal() {
        if (areAllResourcesFreedOnClose() && this.channel != null) {
            this.channel.close();
        }
    }

    /**
     * INTERNAL:
     */
    protected String logDebugMessage(Message message) {
        Object[] args = { this.channel.getName() };
        // call logDebugWithoutLevelCheck to avoid the second rcm.shouldLogDebugMessage() check
        rcm.logDebugWithoutLevelCheck("retreived_remote_message_from_jgroup_channel", args);
        return this.channel.getName();
    }

    /**
     * INTERNAL:
     * Used for debug logging
     */
    @Override
    protected void createDisplayString() {
        this.displayString = Helper.getShortClassName(this) + "[" + serviceId.toString() + "]";
    }

    /**
     * INTERNAL:
     * Return whether a BroadcastConnection should check a ServiceId against its
     * own ServiceId to avoid the processing of Commands with the same ServiceId.
     * Not required for JGroups.
     */
    @Override
    protected boolean shouldCheckServiceId() {
        return false;
    }
}
