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
package org.eclipse.persistence.internal.sessions.coordination;

import org.eclipse.persistence.sessions.SessionProfiler;
import org.eclipse.persistence.sessions.coordination.*;
import org.eclipse.persistence.exceptions.*;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>
 * <b>Purpose</b>: Synchronous and asynchronous propagation of remote commands
 * <p>
 * <b>Description</b>: Maintains a reference to the RemoteCommandManager to obtain
 * the connection information required to send the commands. In sync mode the caller
 * is blocked until the command has been sent along all of the connections. If in
 * async mode then a CommandPropagator instance is further created for each of the
 * connections and control is returned to the caller while the threads send the
 * command to the remote services.
 * <p>
 */
public class CommandPropagator implements Runnable {

    /** Reference to manager to get connections, etc. */
    protected RemoteCommandManager rcm;

    /** The command to send */
    protected Command command;

    /** The command to send */
    protected byte[] commandBytes;

    /** Connection to send to */
    protected RemoteConnection connection;

    /**
     * Constructor used to create the main propagator
     */
    public CommandPropagator(RemoteCommandManager rcm, Command command, byte[] commandBytes) {
        super();
        this.rcm = rcm;
        this.command = command;
        this.commandBytes = commandBytes;
    }

    /**
     * Constructor used to create the async propagator threads
     */
    public CommandPropagator(RemoteCommandManager rcm, Command command, byte[] commandBytes, RemoteConnection connection) {
        this(rcm, command, commandBytes);
        this.connection = connection;
    }

    /**
     * INTERNAL:
     * Returns the remote command manager
     */
    protected RemoteCommandManager getRemoteCommandManager() {
        return rcm;
    }

    /**
     * INTERNAL:
     * Returns the command to be sent
     */
    protected Command getCommand() {
        return command;
    }

    /**
     * INTERNAL:
     * Synchronously propagate the command
     */
    public void synchronousPropagateCommand() {
        rcm.logDebug("sync_propagation", (Object[])null);
        Iterator connections = rcm.getTransportManager().getConnectionsToExternalServicesForCommandPropagation().values().iterator();

        while (connections.hasNext()) {
            connection = (RemoteConnection)connections.next();
            this.propagateCommand(connection);
        }
    }

    /**
     * INTERNAL:
     * Asynchronously propagate the command
     */
    public void asynchronousPropagateCommand() {
        // The async logic is in the run() method
        rcm.logDebug("async_propagation", (Object[])null);
        this.rcm.getServerPlatform().launchContainerRunnable(this);
    }

    /**
     * INTERNAL:
     * Propagate the command to the specified connection.
     */
    public void propagateCommand(RemoteConnection connection) {
        Object[] arguments = { command.getClass().getName(), connection.getServiceId() };
        rcm.logDebug("propagate_command_to", arguments);

        try {
            // The result will be null on success, and an exception string on failure
            Object result = null;
            // PERF: Support plugable serialization.
            if (this.commandBytes != null) {
                result = connection.executeCommand(this.commandBytes);
            } else {
                result = connection.executeCommand(this.command);
            }
            if (result != null) {
                // An error occurred executing the remote command
                handleExceptionFromRemoteExecution(connection, (String)result);
            }
        } catch (CommunicationException comEx) {
            // We got a comms exception.
            this.handleCommunicationException(connection, comEx);
        }
    }

    /**
     * INTERNAL:
     * We received the specified exception String while executing the command
     * over the specified connection. Turn it into a RemoteCommandManagerException
     * and throw it on this, the client side.
     */
    public void handleExceptionFromRemoteExecution(RemoteConnection conn, String exString) {
        // Log the error and pass the exception off to the handler
        Object[] args = { conn.getServiceId(), exString };
        rcm.logWarning("failed_command_propagation", args);

        // Build an RCMException and concoct an RTException to stick inside
        RemoteCommandManagerException rcmException = RemoteCommandManagerException.unableToPropagateCommand(conn.toString(), new Exception(exString));
        rcm.handleException(rcmException);
    }

    /**
     * INTERNAL:
     * We received the specified CommunicationException trying to execute the command
     * over the specified connection. We treat CommunicationExceptions as the
     * transport-generic wrapper exception that wraps the transport exception,
     * so the real exception that interests us is inside.
     */
    public void handleCommunicationException(RemoteConnection conn, CommunicationException comEx) {
        // If the removeOnError flag is set then just log a warning and discard connection
        if (rcm.getTransportManager().shouldRemoveConnectionOnError()) {
            Object[] args = { conn.getServiceId(), comEx.getInternalException() };
            rcm.logWarning("drop_connection_on_error", args);
            rcm.getTransportManager().removeConnectionToExternalService(conn);
        } else {
            // Otherwise, log the error and pass the exception off to the handler
            Object[] args = { conn.getServiceId(), comEx };
            rcm.logWarning("failed_command_propagation", args);

            RemoteCommandManagerException rcmException = RemoteCommandManagerException.unableToPropagateCommand(conn.toString(), comEx.getInternalException());
            try {
                rcm.handleException(rcmException);
            } catch (RuntimeException ex) {
                // If the connection is set then we are an async connection thread:
                // in that case the method is called from a separate thread
                // and no one could catch it.
                if (connection == null) {
                    // rethrow exception
                    throw ex;
                }
            }
        }
    }

    /**
     * INTERNAL:
     * This is the execution method of the async propagation thread.  It will create
     * other threads, one each for the remote connections, to propagate the command.
     */
    public void run() {
        // If the connection is set then we are an async connection thread
        if (this.connection != null) {
            this.rcm.getCommandProcessor().startOperationProfile(SessionProfiler.CacheCoordination);
            try {
                propagateCommand(this.connection);
            } finally {
                this.rcm.getCommandProcessor().endOperationProfile(SessionProfiler.CacheCoordination);
            }
        } else {
            Map mapConnections = this.rcm.getTransportManager().getConnectionsToExternalServicesForCommandPropagation();
            Iterator iterator = mapConnections.values().iterator();
            if (mapConnections.size() == 1) {
                // There is only one connection - no need for yet another thread.
                // Set the connection into the current one
                // so that it's recognized as async propagation in handleCommunicationException method.
                this.connection = (RemoteConnection)iterator.next();
                propagateCommand(this.connection);
            } else {
                // This is the top level thread. We need to spawn off a bunch of async connection threads
                while (iterator.hasNext()) {
                    RemoteConnection remoteConnection = (RemoteConnection)iterator.next();
                    CommandPropagator propagator = new CommandPropagator(this.rcm, this.command, this.commandBytes, remoteConnection);
                    this.rcm.getServerPlatform().launchContainerRunnable(propagator);
                }
            }
        }
    }
}
