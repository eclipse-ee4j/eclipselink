/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.sessions.coordination;

import org.eclipse.persistence.sessions.coordination.*;
import org.eclipse.persistence.exceptions.*;
import java.util.Enumeration;
import java.util.Hashtable;

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

    /** Connection to send to */
    protected RemoteConnection connection;

    /**
     * Constructor used to create the main propagator
     */
    public CommandPropagator(RemoteCommandManager rcm, Command command) {
        super();
        this.rcm = rcm;
        this.command = command;
    }

    /**
     * Constructor used to create the async propagator threads
     */
    public CommandPropagator(RemoteCommandManager rcm, Command command, RemoteConnection connection) {
        this(rcm, command);
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
        Enumeration connections = rcm.getTransportManager().getConnectionsToExternalServicesForCommandPropagation().elements();

        while (connections.hasMoreElements()) {
            connection = (RemoteConnection)connections.nextElement();
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
    public void propagateCommand(RemoteConnection conn) {
        Object[] arguments = { command.getClass().getName(), conn.getServiceId() };
        rcm.logDebug("propagate_command_to", arguments);

        try {
            // The result will be null on success, and an exception string on failure
            Object result = conn.executeCommand(command);
            if (result != null) {
                // An error occurred executing the remote command
                handleExceptionFromRemoteExecution(conn, (String)result);
            }
        } catch (CommunicationException comEx) {
            // We got a comms exception.
            this.handleCommunicationException(conn, comEx);
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
        if (connection != null) {
            propagateCommand(connection);
        } else {
            Hashtable mapConnections = rcm.getTransportManager().getConnectionsToExternalServicesForCommandPropagation();
            Enumeration enumConnections = mapConnections.elements();
            if(mapConnections.size() == 1) {
                // There is only one connection - no need for yet another thread.
                // Set the connection into the current one
                // so that it's recognized as async propagation in handleCommunicationException method.
                this.connection = (RemoteConnection)enumConnections.nextElement();
                propagateCommand(connection);
            } else {
                // This is the top level thread. We need to spawn off a bunch of async connection threads
                while (enumConnections.hasMoreElements()) {
                    RemoteConnection conn = (RemoteConnection)enumConnections.nextElement();
                    CommandPropagator propagator = new CommandPropagator(rcm, command, conn);
                    this.rcm.getServerPlatform().launchContainerRunnable(propagator);
                }
            }
        }
    }
}
