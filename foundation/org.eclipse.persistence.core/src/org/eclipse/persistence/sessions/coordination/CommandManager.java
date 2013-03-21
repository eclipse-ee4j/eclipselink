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
package org.eclipse.persistence.sessions.coordination;


/**
 * <p>
 * <b>Purpose</b>: Provide an interface that clients can use to invoke remote
 * commands on other EclipseLink instances, or on other applications that may want
 * to receive and process EclipseLink commands.
 * <p>
 * <b>Description</b>: This is the main interface that is used to send and receive
 * remote commands in a EclipseLink cluster. The sequence of remote command execution
 * is as follows:
 * <p>
 * <ol>
 * <li> CommandProcessor creates a command and gives it to its local CommandManager
 * to send to remote services.
 * <li> CommandManager uses its CommandConverter to convert the command into a
 * TopLink Command object
 * <li> CommandManager then uses its TransportManager to send the Command to all
 * other known cluster members that are subscribing to the same channel
 * <li> Each remote TransportManager receives the Command and passes it off to the
 * CommandManager
 * <li> Each CommandManager uses its local CommandConverter to convert the command
 * into a format appropriate to its application (CommandProcessor)
 * <li> Each CommandManager invokes its local CommandProcessor and passes it the
 * command (formatted the way that the application-specified converter left it in)
 * to be executed. If the CommandProcessor is a EclipseLink session then the
 * executeWithSession method will be invoked on the command.
 * </ol>
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Invoke the DiscoveryManager to establish the EclipseLink cluster
 * <li> Propagate remote command to EclipseLink cluster
 * <li> Delegate processing command to its CommandProssesor
 * <li> Convert command to appropriate format for sending to EclipseLink cluster
 * or its command processor
 * </ul>
 * @see Command
 * @see CommandProcessor
 * @author Steven Vo
 * @since OracleAS TopLink 10<i>g</i> (9.0.4)
 */
public interface CommandManager {

    /**
     * PUBLIC:
     * Initialize the remote command manager. This will also trigger the
     * DiscoveryManager to start establishing the EclipseLink cluster.
     */
    public void initialize();

    /**
     * PUBLIC:
     * Shut down the remote command manager. This will also trigger the
     * DiscoveryManager to stop.
     * NOTE: Although this call initiates the shutdown process,
     * no guarantees are made as to when it will actually complete.
     */
    public void shutdown();

    /**
     * PUBLIC:
     * Return the URL for this command manager.
     *
     * @return The URL String for this CommandManager
     */
    public String getUrl();

    /**
     * ADVANCED:
     * Set the URL for this command manager.
     *
     * @param url The URL String for this CommandManager
     */
    public void setUrl(String url);

    /**
     * PUBLIC:
     * Return the service channel for this command manager. All command managers
     * with the same service channel will send and receive commands from each other.
     * Commands sent on other service channels will not be exchanged with this
     * command manager.
     *
     * @return The service channel String subscribed to by this CommandManager
     */
    public String getChannel();

    /**
     * ADVANCED:
     * Set the service channel for this command manager. All command managers
     * with the same service channel will send and receive commands from each other.
     * Commands sent on other service channels will not be exchanged with this
     * command manager.
     *
     * @param channel The service channel subscribed to by this CommandManager
     */
    public void setChannel(String channel);

    /**
     * ADVANCED:
     * Propagate a remote command to all remote RCM services participating
     * in the EclipseLink cluster.
     *
     * @param command An object representing a TopLink command
     */
    public void propagateCommand(Object command);

    /**
     * PUBLIC:
     * Return the command processor that processes commands received from the cluster.
     *
     * @return An implementation instance of CommandProcessor
     */
    public CommandProcessor getCommandProcessor();

    /**
     * ADVANCED:
     * Set the command processor that will be invoked to process commands. EclipseLink
     * applications can implement this interface in order to receive remote commands
     * from a EclipseLink cluster.
     *
     * @param commandProcessor The intended processor of remote commands
     */
    public void setCommandProcessor(CommandProcessor commandProcessor);

    /**
     * ADVANCED:
     * Set a specific transport manager to manage sending and receiving of remote commands.
     *
     * @param newTransportManager An instance of the desired transport manager type
     */
    public void setTransportManager(TransportManager newTransportManager);

    /**
    * PUBLIC:
     * Return the transport manager that manages sending and receiving of remote commands.
     *
     * @return The TransportManager instance being used by this CommandManager
     */
    public TransportManager getTransportManager();

    /**
     * PUBLIC:
     * Return the discovery manager that manages membership in the EclipseLink cluster.
     *
     * @return The DiscoveryManager instance being used by this CommandManager
     */
    public DiscoveryManager getDiscoveryManager();

    /**
     * PUBLIC:
     * Return the converter instance used to convert between TopLink Command
     * objects and an application command format.
     *
     * @return The converter being used by this CommandManager
     */
    public CommandConverter getCommandConverter();

    /**
     * ADVANCED:
     * Set the converter instance that will be invoked by this CommandProcessor
     * to convert commands from their application command format into EclipseLink
     * Command objects before being propagated to remote command manager services.
     * The converter will also be invoked to convert EclipseLink Command objects into
     * application format before being sent to the CommandProcessor for execution.
     *
     * @param commandConverter The converter to be used by this CommandManager
     */
    public void setCommandConverter(CommandConverter commandConverter);

    /**
     * PUBLIC:
     * Return whether this command manager should propagate commands
     * synchronously or asynchronously. If set to synchronous propagation then
     * propagateCommand() will not return to the caller until the command has
     * been executed on all of the services in the cluster. In asynchronous mode
     * the command manager will create a separate thread for each of the remote
     * service executions, and then promptly return to the caller.
     */
    public boolean shouldPropagateAsynchronously();

    /**
     * ADVANCED:
     * Set whether this command manager should propagate commands
     * synchronously or asynchronously. If set to synchronous propagation then
     * propagateCommand() will not return to the caller until the command has
     * been executed on all of the services in the cluster. In asynchronous mode
     * the command manager will create a separate thread for each of the remote
     * service executions, and then promptly return to the caller.
     */
    public void setShouldPropagateAsynchronously(boolean asyncMode);

    /**
    * ADVANCE:
    * Return whether this commandProcessor is a EclipseLink session or not.
    */
    public boolean isCommandProcessorASession();

}
