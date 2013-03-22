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

import org.eclipse.persistence.exceptions.RemoteCommandManagerException;
import org.eclipse.persistence.internal.sessions.coordination.RemoteConnection;
import org.eclipse.persistence.internal.sessions.coordination.RCMCommand;
import org.eclipse.persistence.internal.sessions.coordination.CommandPropagator;
import org.eclipse.persistence.sessions.coordination.rmi.RMITransportManager;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.localization.LoggingLocalization;
import org.eclipse.persistence.internal.localization.TraceLocalization;
import org.eclipse.persistence.internal.sessions.coordination.*;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.platform.server.ServerPlatform;
import java.net.InetAddress;

/**
 * <p>
 * <b>Purpose</b>: Provide a CommandManager implementation for cache coordination.
 * <p>
 * <b>Description</b>: A RemoteCommandManager (or RCM) instance is the primary component
 * of an RCM service instance. It manages the other components of the service, and
 * directs the overall service operation. Its ServiceId uniquely distinguishes it
 * from other service instances in the cluster.
 * <p>
 * Each RCM has a logical channel to which it subscribes and publishes. This channel
 * determines which other instances in the cluster the service instance sends and
 * receives remote commands to/from. All RCM's on the same channel should have the same
 * discovery manager settings (be communicating on the same multicast) so that
 * the discovery managers may be able to discover one another. RCM's on different
 * channels may operate on the same or on different multicast groups.
 * <p>
 * An RCM instance knows about other instances in the cluster through its DiscoveryManager.
 * Its TransportManager is responsible for setting up the connections to other instances
 * once they are discovered.
 * <p>
 * An RCM is instructed to "propagate", or execute on all remote service instances
 * in the cluster that subscribe to the same channel, a remote command by its
 * CommandProcessor. Likewise, when an RCM receives a remote command to be executed
 * then it passes the command off to the CommandProcessor for the processing of the
 * command to occur. CommandProcessors pass commands to the RCM as an Object (in a
 * format that may be specific to the application) and the RCM uses its CommandConverter
 * to convert it to a EclipseLink Command object before sending the Command off to the
 * cluster. Similarly, when a EclipseLink Command object is received then the RCM invokes
 * its CommandConverter to convert the object into the application format that will be
 * passed to the CommandProcessor to process the command.
 *
 * @see CommandManager
 * @see Command
 * @see CommandProcessor
 * @see CommandConverter
 * @see CommandTransporter
 * @see DiscoveryManager
 * @author Steven Vo
 * @since OracleAS TopLink 10<i>g</i> (9.0.4)
 */
public class RemoteCommandManager implements org.eclipse.persistence.sessions.coordination.CommandManager {
    public static final String DEFAULT_CHANNEL = "EclipseLinkCommandChannel";
    public static final boolean DEFAULT_ASYNCHRONOUS_MODE = true;

    /** Uniquely identifies this service in the cluster */
    protected ServiceId serviceId;

    /** Manages the detection of new services as they join the cluster */
    protected DiscoveryManager discoveryManager;

    /** Manages the transport level connections between command managers */
    protected TransportManager transportManager;

    /** Invoked to process a command when it is received from the cluster */
    protected CommandProcessor commandProcessor;

    /** Used for converting commands between EclipseLink Command and app command formats */
    protected CommandConverter commandConverter;

    /** Determines whether propagation should be synchronous or asynchronous */
    protected boolean isAsynchronous;

    /** Determines whether profiling command should be send */
    protected boolean isEclipseLinkSession;

    /** Uniquely identifies ServerPlatform in the cluster */
    protected ServerPlatform serverPlatform;
    
    //** Indicates whether RCM is active. In case there's discoveryManager it mirrors discoveryManager.isDiscoveryStopped()
    protected boolean isStopped = true;

    public RemoteCommandManager(CommandProcessor commandProcessor) {
        this.serviceId = new ServiceId();

        // BUG - 3824040 we must call the setCommandProcessor method to
        // ensure the isEclipseLinkSession flag is set correctly.
        setCommandProcessor(commandProcessor);

        // Set default values
        this.transportManager = new RMITransportManager(this);
        this.discoveryManager = this.transportManager.createDiscoveryManager();
        this.serviceId.setChannel(DEFAULT_CHANNEL);
        this.isAsynchronous = DEFAULT_ASYNCHRONOUS_MODE;

        // Set the command processor to point back to this command manager
        commandProcessor.setCommandManager(this);
    }

    public RemoteCommandManager(CommandProcessor commandProcessor, TransportManager transportManager) {
        this(commandProcessor);
        this.transportManager = transportManager;
        this.discoveryManager = this.transportManager.createDiscoveryManager();
    }

    /**
     * PUBLIC:
     * Initialize the remote command manager. This will also trigger the
     * DiscoveryManager to start establishing the EclipseLink cluster.
     */
    public void initialize() {
        Object[] args = { this.getServiceId() };
        logDebug("starting_rcm", args);

        // replace the $HOST substring of the URL with the discovered ipAddress
        if ((getUrl() != null) && (getUrl().indexOf(ServiceId.HOST_TOKEN) >= 0)) {
            try {
                // discover local IP address
                String ipAddress = InetAddress.getLocalHost().getHostAddress();
                replaceLocalHostIPAddress(ipAddress);
            } catch (Exception ex) {
                // catch different exceptions that are due to security or IP address of a host could not be determined. 
                // i.e. java.lang.SecurityException or java.net.UnknownHostException
                throw RemoteCommandManagerException.errorDiscoveringLocalHostIPAddress(ex);
            }
        }
        this.isStopped = false;
        if (this.discoveryManager != null) {
            this.discoveryManager.startDiscovery();
        } else {
            this.transportManager.createConnections();
        }
    }

    /**
     * PUBLIC:
     * Indicates whether the RCM has been stopped:
     * either initialize hasn't been called or shutdown has been called.
     */
    public boolean isStopped() {
        return isStopped;
    }
    
    /**
     * PUBLIC:
     * Shut down the remote command manager. This will also trigger the
     * DiscoveryManager to stop.
     * NOTE: Although this call initiates the shutdown process,
     * no guarantees are made as to when it will actually complete.
     */
    public void shutdown() {
        Object[] args = { this.getServiceId() };
        logDebug("stopping_rcm", args);
        
        if(discoveryManager != null) {
            discoveryManager.stopDiscovery();
    
            // use a new Discovery thread with same settings as the previous one.
            DiscoveryManager newDmgr = transportManager.createDiscoveryManager();
            newDmgr.shallowCopy(discoveryManager);
            discoveryManager = newDmgr;
        }
        isStopped = true;
        transportManager.discardConnections();
    }

    /**
     * ADVANCED:
     * Propagate a remote command to all remote RCM services participating
     * in the EclipseLink cluster.
     *
     * @param command An object representing a EclipseLink command
     */
    public void propagateCommand(Object command) {
        Command newCommand;
        CommandPropagator propagator;

        if (commandConverter != null) {
            // Use the converter if we have one
            Object[] args = { command };
            logDebug("converting_to_toplink_command", args);
            //for dms profiling
            if (isCommandProcessorASession()) {
                getCommandProcessor().processCommand(new ProfileMessageSentCommand());
            }
            newCommand = commandConverter.convertToEclipseLinkCommand(command);
        } else if (command instanceof Command) {
            // If converter is not set then maybe it just doesn't need converting
            newCommand = (Command)command;
        } else {
            // We can't convert the thing - we may as well chuck it!
            Object[] args = { command };
            logWarning("missing_converter", args);
            return;
        }

        // Set our service id on the command to indicate that it came from us
        newCommand.setServiceId(getServiceId());

        // Propagate the command (synchronously or asynchronously)
        propagator = new CommandPropagator(this, newCommand);

        if (shouldPropagateAsynchronously()) {
            propagator.asynchronousPropagateCommand();
        } else {
            propagator.synchronousPropagateCommand();
        }
    }

    /**
     * INTERNAL:
     * Delegate to command processor
     */
    public void processCommandFromRemoteConnection(Command command) {
        Object[] args = { command.getClass().getName(), command.getServiceId() };
        logDebug("received_remote_command", args);
        //for dms profiling
        if (isCommandProcessorASession()) {
            getCommandProcessor().processCommand(new ProfileMessageReceiveCommand());
        }

        // If the command is internal then execute it on this RCM
        if (command.isInternalCommand() || command instanceof RCMCommand) {
            logDebug("processing_internal_command", args);
            ((RCMCommand)command).executeWithRCM(this);
            return;
        }

        // Convert command if neccessary
        Object newCommand = command;
        if (commandConverter != null) {
            logDebug("converting_to_user_command", args);
            newCommand = commandConverter.convertToUserCommand(command);
        }

        // process command with command processor
        logDebug("processing_remote_command", args);
        commandProcessor.processCommand(newCommand);
        if (isCommandProcessorASession()) {
            getCommandProcessor().processCommand(new ProfileRemoteChangeSetCommand());
        }
    }

    public CommandProcessor getCommandProcessor() {
        return commandProcessor;
    }

    public void setCommandProcessor(CommandProcessor newCommandProcessor) {
        commandProcessor = newCommandProcessor;
        if (newCommandProcessor instanceof Session) {
            isEclipseLinkSession = true;
        }
    }

    public TransportManager getTransportManager() {
        return transportManager;
    }

    public void setTransportManager(TransportManager newTransportManager) {
        transportManager = newTransportManager;
        discoveryManager = transportManager.createDiscoveryManager();
    }

    /**
     * INTERNAL:
     * Delegate to the command procesor to handle the exception.
     */
    public void handleException(RuntimeException exception) {
        commandProcessor.handleException(exception);
    }

    /**
     * INTERNAL:
     * A new service has been detected by the discovery manager. Take the appropriate
     * action to connect to the service.
     */
    public void newServiceDiscovered(ServiceId service) {
        RemoteConnection connection = transportManager.createConnection(service);
        transportManager.addConnectionToExternalService(connection);
    }

    /**
     * PUBLIC:
     * Return the discovery manager that detects the arrival of new cluster members
     */
    public DiscoveryManager getDiscoveryManager() {
        return discoveryManager;
    }

    /**
     * PUBLIC:
     * Return the converter instance used to convert between EclipseLink Command
     * objects and an application command format.
     */
    public CommandConverter getCommandConverter() {
        return commandConverter;
    }

    /**
     * ADVANCED:
     * Set the converter instance that will be invoked by this CommandProcessor
     * to convert commands from their application command format into EclipseLink
     * Command objects before being propagated to remote command manager services.
     * The converter will also be invoked to convert EclipseLink Command objects into
     * application format before being sent to the CommandProcessor for execution.
     */
    public void setCommandConverter(CommandConverter newCommandConverter) {
        commandConverter = newCommandConverter;
    }

    /**
     * INTERNAL:
     */
    public boolean shouldLogMessage(int logLevel) {
        return commandProcessor.shouldLogMessages(logLevel);
    }
    public boolean shouldLogDebugMessage() {
        return commandProcessor.shouldLogMessages(CommandProcessor.LOG_DEBUG);
    }
    public boolean shouldLogWarningMessage() {
        return commandProcessor.shouldLogMessages(CommandProcessor.LOG_WARNING);
    }

    /**
     * INTERNAL:
     */
    public void logMessage(int logLevel, String message, Object[] args) {
        if (commandProcessor.shouldLogMessages(logLevel)) {
            logMessageWithoutLevelCheck(logLevel, message, args);
        }
    }

    /**
     * INTERNAL:
     * Use this method in case the necessary logLevel has been confirmed
     * by calling commandProcessor.shouldLogMessages method
     */
    public void logMessageWithoutLevelCheck(int logLevel, String message, Object[] args) {
        String i18nmsg = message;
        if ((logLevel == CommandProcessor.LOG_ERROR) || (logLevel == CommandProcessor.LOG_WARNING)) {
            i18nmsg = LoggingLocalization.buildMessage(message, args);
        } else if ((logLevel == CommandProcessor.LOG_INFO) || (logLevel == CommandProcessor.LOG_DEBUG)) {
            i18nmsg = TraceLocalization.buildMessage(message, args);
        }
        commandProcessor.logMessage(logLevel, i18nmsg);
    }

    /**
     * INTERNAL:
     * Convenience logging methods.
     */
    public void logDebug(String message, Object[] args) {
        this.logMessage(CommandProcessor.LOG_DEBUG, message, args);
    }
    public void logDebugWithoutLevelCheck(String message, Object[] args) {
        this.logMessageWithoutLevelCheck(CommandProcessor.LOG_DEBUG, message, args);
    }

    public void logInfo(String message, Object[] args) {
        this.logMessage(CommandProcessor.LOG_INFO, message, args);
    }

    public void logWarning(String message, Object[] args) {
        this.logMessage(CommandProcessor.LOG_WARNING, message, args);
    }
    public void logWarningWithoutLevelCheck(String message, Object[] args) {
        this.logMessageWithoutLevelCheck(CommandProcessor.LOG_WARNING, message, args);
    }

    public void logError(String message, Object[] args) {
        this.logMessage(CommandProcessor.LOG_ERROR, message, args);
    }

    /**
     * INTERNAL:
     * Return the service info that identifies this service instance
     */
    public ServiceId getServiceId() {
        return serviceId;
    }

    /**
     * PUBLIC:
     * Return the service channel for this command manager. All command managers
     * with the same service channel will send and receive commands from each other.
     * Commands sent on other service channels will not be exchanged with this
     * command manager.
     */
    public String getChannel() {
        return this.getServiceId().getChannel();
    }

    /**
     * ADVANCED:
     * Set the service channel for this command manager. All command managers
     * with the same service channel will send and receive commands from each other.
     * Commands sent on other service channels will not be exchanged with this
     * command manager.
     */
    public void setChannel(String channel) {
        this.getServiceId().setChannel(channel);
    }

    /**
     * INTERNAL:
     * Return whether this command manager should process profile commands
     */
    public boolean isCommandProcessorASession() {
        return this.isEclipseLinkSession;
    }

    /**
     * PUBLIC:
     * Return the URL for this command manager.
     */
    public String getUrl() {
        return this.getServiceId().getURL();
    }

    /**
     * ADVANCED:
     * Set the URL for this command manager.
     */
    public void setUrl(String url) {
        this.getServiceId().setURL(url);
    }

    /**
     * PUBLIC:
     * Return whether this command manager should propagate commands
     * synchronously or asynchronously. If set to synchronous propagation then
     * propagateCommand() will not return to the caller until the command has
     * been executed on all of the services in the cluster. In asynchronous mode
     * the command manager will create a separate thread for each of the remote
     * service executions, and then promptly return to the caller.
     */
    public boolean shouldPropagateAsynchronously() {
        return isAsynchronous;
    }

    /**
     * ADVANCED:
     * Set whether this command manager should propagate commands
     * synchronously or asynchronously. If set to synchronous propagation then
     * propagateCommand() will not return to the caller until the command has
     * been executed on all of the services in the cluster. In asynchronous mode
     * the command manager will create a separate thread for each of the remote
     * service executions, and then promptly return to the caller.
     */
    public void setShouldPropagateAsynchronously(boolean asyncMode) {
        isAsynchronous = asyncMode;
    }

    /**
     * ADVANCED:
     * Allow user to replace the $HOST subString of the local host URL with the user user input at runtime.
     * By default, EclipseLink will try to discovery the local host IP and may fail due to security or network restrictions.
     * In this case, user can call this API to specify the IP address or host name during pre-login session event or before session login.
     * Example:
     * If input is 145.23.127.79, the local host URL of ormi://$HOST:2971:/app_name will become ormi://145.23.127.79:2971:/app_name
     */
    public void replaceLocalHostIPAddress(String ipAddress) {
        String newURL = Helper.replaceFirstSubString(this.getUrl(), ServiceId.HOST_TOKEN, ipAddress);
        if (newURL != null) {
            this.setUrl(newURL);
        }
    }

    /**
     * ADVANCED:
     * Allow user to replace the $PORT subString of the local host URL with the user user input at runtime.
     * In this case, user can call this API to specify the port number for a specific transport during pre-login session event or before session login.
     * Example:
     * If input is 7799, the local host URL of ormi://145.23.127.79:$PORT/app_name will become ormi://145.23.127.79:7799/app_name
     */
    public void replaceTransportPortNumber(String portNumber) {
        String newURL = Helper.replaceFirstSubString(this.getUrl(), ServiceId.PORT_TOKEN, portNumber);
        if (newURL != null) {
            this.setUrl(newURL);
        }
    }

    /**
     * INTERNAL:
     * Return the serverPlatform that identifies the application server
     */
    public ServerPlatform getServerPlatform() {
        if (isCommandProcessorASession()) {
            return ((DatabaseSessionImpl)getCommandProcessor()).getServerPlatform();
        } else {
            if (serverPlatform != null) {
                return this.serverPlatform;
            } else {
                throw RemoteCommandManagerException.errorGettingServerPlatform();
            }
        }
    }

    /**
     * PUBLIC:
     * The ServerPlatform must be set manually when the RemoteCommandManager'CommandProcessor is not EclipseLink Session.
     * When the CommandProcessor is a EclipseLink Session, the ServerPlatform is automatically gotten from the Session.
     */
    public void setServerPlatform(ServerPlatform theServerPlatform) {
        this.serverPlatform = theServerPlatform;
    }
}
