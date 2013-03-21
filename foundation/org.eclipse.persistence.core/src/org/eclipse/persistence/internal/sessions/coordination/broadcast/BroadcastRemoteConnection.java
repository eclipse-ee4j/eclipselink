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
package org.eclipse.persistence.internal.sessions.coordination.broadcast;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.coordination.RemoteConnection;
import org.eclipse.persistence.sessions.coordination.broadcast.BroadcastTransportManager;
import org.eclipse.persistence.sessions.coordination.Command;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;
import org.eclipse.persistence.exceptions.CommunicationException;
import org.eclipse.persistence.exceptions.RemoteCommandManagerException;

/**
 * <p>
 * <b>Purpose</b>: Base class extending RemoteConnection for broadcasting RCM protocols: JMS and Oc4jJGroups.
 * <p>
 * <b>Description</b>: Defines lifecycle states and most of the methods,
 * as well as exception processing and info logging.
 * <p>
 * @author Andrei Ilitchev
 * @since OracleAS TopLink 11<i>g</i> (11.1.1)
 */
public abstract class BroadcastRemoteConnection extends RemoteConnection {
    protected RemoteCommandManager rcm;
    protected String topicName;
    
    // Working state - connection can send and/or receive messages.
    public static final String STATE_ACTIVE         = "ACTIVE";
    // close method called but not all resources used by connection are freed yet.
    // Note that even after close method returns the state still may be CLOSING.
    public static final String STATE_CLOSING        = "CLOSING";
    // All resources are freed.
    public static final String STATE_CLOSED         = "CLOSED";
    // STATE_ACTIVE -> STATE_CLOSING -> STATE_CLOSED
    protected String state = STATE_ACTIVE;
    
    // Connection information String.
    protected String displayString;
    // Array containing a single element - displayString. Used for warnings and debug logging.
    protected Object[] info;
    // Array containing a two elements - displayString and an empty String.
    // Used for debug logging which require messageId in case messageId is null
    // (so that a new array is not created each time).
    protected Object[] infoExt;
        
    public BroadcastRemoteConnection(RemoteCommandManager rcm) {
        this.serviceId = rcm.getServiceId();
        this.rcm = rcm;
        this.topicName = ((BroadcastTransportManager)rcm.getTransportManager()).getTopicName();
    }

    /**
     * INTERNAL:
     * Publish the remote command. The result of execution is returned.
     * This method is used only by external (publishing) connection.
     */
    public Object executeCommand(Command command) throws CommunicationException {
        if(isActive()) {
            try {
                return executeCommandInternal(command);
            } catch (Exception exception) {
                // Note that there is no need to removeConnection here - it's removed by the calling method:
                // org.eclipse.persistence.internal.sessions.coordination.CommandPropagator.propagateCommand.
                // This method catches CommunicationException and processes it in handleCommunicationException method.
                // The latter method, in case shouldRemoveConnectionOnError==true, removes the connection;
                // otherwise it wraps the CommunicationException into RemoteCommandManagerException
                // (with errorCode RemoteCommandManagerException.ERROR_PROPAGATING_COMMAND)
                // and gives the use a chance to handle it - this is an opportunity for the user to
                // stop remote command processing.
                throw CommunicationException.errorSendingMessage(getServiceId().getId(), exception);
            }
        } else {
            rcm.logWarning("broadcast_ignored_command_while_closing_connection", getInfo());
            return null;
        }
    }
    
    /**
     * INTERNAL:
     * Publish the remote command. The result of execution is returned.
     * This method is used only by external (publishing) connection.
     */
    protected abstract Object executeCommandInternal(Command command) throws Exception;
    
    /**
     * INTERNAL:
     * Called from executeCommandInternal to log debugInfo right before sending the message.
     * Returns array {toString(), messageId }.
     * In case messageId is null returns getInfoExt() avoiding creation of a new array.
     */
    protected Object[] logDebugBeforePublish(String messageId) {
        Object[] debugInfo = null;
        if(messageId == null) {
            debugInfo = getInfoExt();
        } else {
            debugInfo = new Object[] {toString(), messageId};
        }
        // call logDebugWithoutLevelCheck to avoid the second rcm.shouldLogDebugMessage() check
        rcm.logDebugWithoutLevelCheck("broadcast_sending_message", debugInfo);
        return debugInfo;
    }
    
    /**
     * INTERNAL:
     * Called from executeCommandInternal to log debugInfo right after sending the message.
     * Only call this method in case logDebugBeforePublish returned non-null
     * this is indication that debug logging is enabled.
     * Pass to this method debugInfo returned by logDebugBeforePublish.
     * Need to pass messageId only in case it has changed since logDebugBeforePublish:
     * some broadcasting protocols (JMS) don't generate messageId until the message is published.
     */
    protected void logDebugAfterPublish(Object[] debugInfo, String messageId) {
        if(messageId != null) {
            if(debugInfo == getInfoExt()) {
                // need to create a new debugInfo object - the original is the cached info.
                debugInfo = new Object[] {toString(), messageId};
            } else {
                // need only update messageId on existing debugInfo
                debugInfo[3] = messageId;
            }
        }
        // call logDebugWithoutLevelCheck to avoid the second rcm.shouldLogDebugMessage() check
        rcm.logDebugWithoutLevelCheck("broadcast_sent_message", debugInfo);
    }
    
    /**
     * INTERNAL:
     * Called when a message is received to log debugInfo:
     * { toString(), messageId }.
     * This method is used by local (listening) connection only.
     */
    protected void logDebugOnReceiveMessage(String messageId) {
        Object[] debugInfo = null;
        if(messageId == null) {
            debugInfo = getInfoExt();
        } else {
            debugInfo = new Object[] {toString(), messageId};
        }
        // call logDebugWithoutLevelCheck to avoid the second rcm.shouldLogDebugMessage() check
        rcm.logDebugWithoutLevelCheck("broadcast_retreived_message", debugInfo);
    }
    
    /**
     * INTERNAL:
     * Process the object extracted from the received message.
     * Pass to this method messageInfo created by logDebugOnReceiveMessage method.
     * This method is used by local (listening) connection only.
     */
    protected void processReceivedObject(Object object, String messageId) {
        Command remoteCommand = null;
        if (object instanceof Command) {
            remoteCommand = (Command)object;
            try {
                // prevent the processing of messages sent with the same serviceId
                if (shouldCheckServiceId()) {
                    if (remoteCommand.getServiceId().getId().equals(this.serviceId.getId())) {
                        return;
                    }
                }
                if (remoteCommand.getServiceId().getChannel().equals(this.serviceId.getChannel())) {
                    if(rcm.shouldLogDebugMessage()) {
                        Object[] args = { toString(), messageId, remoteCommand.getServiceId().toString(), Helper.getShortClassName(remoteCommand) };
                        rcm.logDebugWithoutLevelCheck("broadcast_processing_remote_command", args);
                    }
                    rcm.processCommandFromRemoteConnection(remoteCommand);
                } else {
                    if(rcm.shouldLogWarningMessage()) {
                        Object[] args = { toString(), messageId, remoteCommand.getServiceId().toString(), Helper.getShortClassName(remoteCommand)};
                        rcm.logWarningWithoutLevelCheck("broadcast_ignore_remote_command_from_different_channel", args);
                    }
                }
            } catch (RuntimeException e) {
                try {
                    rcm.handleException(RemoteCommandManagerException.errorProcessingRemoteCommand(toString(), messageId, remoteCommand.getServiceId().toString(), Helper.getShortClassName(remoteCommand), e));
                } catch (RuntimeException ex) {
                    // User had a chance to handle the exception.
                    // The method is called by a listener thread - no one could catch this exception.
                }
            }
        } else if (object == null) {
            Object[] args = { toString(), messageId};
            rcm.logWarning("broadcast_remote_command_is_null", args);
        } else {
            if(rcm.shouldLogWarningMessage()) {
                String className = object.getClass().getName();
                Object[] args = { toString(), messageId, className };
                rcm.logWarningWithoutLevelCheck("broadcast_remote_command_wrong_type", args);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Call this message in case there is failure to extract the object 
     * (to be passed to processReceivedObject) from the message.
     * Pass to this method debugInfo created by logDebugOnReceiveMessage method.
     * This method is used by local (listening) connection only.
     */
    protected void failDeserializeMessage(String messageId, Exception exception) {
        try {
            rcm.handleException(RemoteCommandManagerException.errorDeserializeRemoteCommand(toString(), messageId, exception));
        } catch (Exception ex) {
            // User had a chance to handle the exception.
            // The method is called by a listener thread - no one could catch this exception.
        }
    }
    
    /**
     * INTERNAL:
     * This method is called when connection in no longer used and it's resources should be freed.
     * As soon as this method is called the state is CLOSING.
     * Usually the state is CLOSED just before the method returns,
     * but there are some special cases (see comment to areAllResourcesFreedOnClose method)
     * when the state is still CLOSING after the method returns.
     */
    public void close() {
        synchronized(this) {
            if(isClosing()) {
                rcm.logWarning("broadcast_connection_already_closing", getInfo());
                return;
            } else if(isClosed()) {
                rcm.logWarning("broadcast_connection_already_closed", getInfo());
                return;
            } else {
                state = STATE_CLOSING;
            }
        }
        try {
            rcm.logDebug("broadcast_closing_connection", getInfo());
            closeInternal();
        } catch (Exception exception) {
            Object[] args = { toString(), exception };
            rcm.logWarning("broadcast_exception_thrown_when_attempting_to_close_connection", args);
        } finally {
            if(areAllResourcesFreedOnClose()) {
                rcm.logDebug("broadcast_connection_closed", getInfo());
                state = STATE_CLOSED;
            }
        }
    }

    /**
     * INTERNAL:
     * State of the connection.
     */
    public String getState() {
        return state;
    }
    
    /**
     * INTERNAL:
     * Connection is open for business.
     */
    public boolean isActive() {
        return state == STATE_ACTIVE;
    }

    /**
     * INTERNAL:
     * close method has been called.
     */
    public boolean isClosing() {
        return state == STATE_CLOSING;
    }

    /**
     * INTERNAL:
     * Connection is closed - all resources were freed.
     */
    public boolean isClosed() {
        return state == STATE_CLOSED;
    }

    /**
     * INTERNAL:
     * This method is called by close method.
     * This method usually 
     * (but not always see comment to areAllResourcesFreedOnClose method)
     * frees all the resources.
     */
    protected abstract void closeInternal() throws Exception;
    
    /**
     * INTERNAL:
     */
    public String getTopicName() {
        return topicName;
    }    

    /**
     * INTERNAL:
     */
    protected Object[] getInfo() {
        if(info == null) {
            info = new Object[] {toString()};
        }
        return info;
    }    

    /**
     * INTERNAL:
     */
    protected Object[] getInfoExt() {
        if(infoExt == null) {
            infoExt = new Object[] {toString(), ""};
        }
        return infoExt;
    }    

    /**
     * INTERNAL:
     * Indicates whether all the resources used by connection are freed after close method returns.
     * Usually that's the case. However in case of local (listening) JMSTopicRemoteConnection
     * close merely indicates to the listening thread that it should free TopicConnection and exit.
     * Note that it may take a while: the listening thread waits until subscriber.receive method either
     * returns a message or throws an exception.
     */
    protected boolean areAllResourcesFreedOnClose() {
        return true;
    }

    /**
     * INTERNAL:
     */
    public String toString() {
        if(displayString == null) {
            createDisplayString();
        }
        return displayString;
    }
    
    /**
     * INTERNAL:
     */
    protected void createDisplayString() {
        this.displayString = Helper.getShortClassName(this) + "[" + serviceId.toString() + ", topic " + topicName +"]";
    }

    /**
     * INTERNAL:
     * Return whether a BroadcastConnection should check a ServiceId against its
     * own ServiceId to avoid the processing of Commands with the same ServiceId.
     * @return boolean
     */
    protected boolean shouldCheckServiceId() {
        return false;
    }
    
}
