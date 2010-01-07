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
package org.eclipse.persistence.internal.sessions.coordination.jms;

import org.eclipse.persistence.internal.sessions.coordination.broadcast.BroadcastRemoteConnection;
import org.eclipse.persistence.sessions.coordination.Command;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;
import org.eclipse.persistence.sessions.coordination.jms.JMSTopicTransportManager;
import org.eclipse.persistence.exceptions.RemoteCommandManagerException;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Message;

/**
 * <p>
 * <b>Purpose</b>: Define the implementation of the abstract RemoteConnection for JMS.
 * <p>
 * <b>Description</b>:  Executing commands implementation of RemoteConnection is done via JMS Publisher.
 *
 * Using a single TopicConnection for both publishing and subscribing would
 * allow subscriber to ignore messages sent through the same TopicConnection - and therefore
 * allow JMSTopicRemoteConnection to ignore messages that it has itself published.
 * Unfortunately J2EE doesn't allow that:
 * J2EE spec. (J2EE.6.6 in v1.4) states:
 *   "The following methods may only be used by application components executing 
 *     in the application client container: javax.jms.Session method setMessageListener ...
 *   Application components in the web and EJB containers must not attempt to create more than one
 *     active (not closed) Session object per connection."
 * Because of these restrictions 
 * a) two TopicConnections are required - one for publishing (external) and another one for listening (local);
 * b) listening should be done using subscriber.receive() in an infinite loop in a separate thread,
 * that's why the class implements Runnable interface.
 * 
 * @author Steven Vo
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class JMSTopicRemoteConnection extends BroadcastRemoteConnection implements Runnable {
    protected TopicConnection topicConnection;
    protected Topic topic;
    protected TopicSession topicSession;
    protected TopicPublisher publisher;
    protected TopicSubscriber subscriber;
    // indicates whether it's a local connection.
    protected boolean isLocal;
    // Used only by local (listening) connection and 
    // only in case shouldRemoveConnectionOnError==false.
    // Time to wait after error receiving jms message:
    // wait to avoid a busy thread throwing barrage of exceptions
    // in case JMS server goes down.
    // User can avoid the wait by handling ERROR_RECEIVING_JMS_MESSAGE exception.
    public static long WAIT_ON_ERROR_RECEIVING_JMS_MESSAGE = 10000;

    public JMSTopicRemoteConnection(RemoteCommandManager rcm, TopicConnection topicConnection, Topic topic, boolean isLocalConnectionBeingCreated) throws JMSException {
        super(rcm);
        this.topicConnection = topicConnection;
        this.topic = topic;
        this.isLocal = isLocalConnectionBeingCreated;
        rcm.logDebug("creating_broadcast_connection", getInfo());
        try {
            this.topicSession = topicConnection.createTopicSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
            if(isLocalConnectionBeingCreated) {
                // it's a local connection
                this.subscriber = topicSession.createSubscriber(topic);
                this.topicConnection.start();
                rcm.logDebug("broadcast_connection_created", getInfo());
                rcm.getServerPlatform().launchContainerRunnable(this);
            } else {
                // it's an external connection
                this.publisher = topicSession.createPublisher(topic);
                rcm.logDebug("broadcast_connection_created", getInfo());
            }
        } catch (JMSException ex) {
            rcm.logDebug("failed_to_create_broadcast_connection", getInfo());
            close();
            throw ex;
        }
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
    protected Object executeCommandInternal(Command command) throws Exception {        
        ObjectMessage message = topicSession.createObjectMessage();
        message.setObject(command);
        
        Object[] debugInfo = null;
        if(rcm.shouldLogDebugMessage()) {
            // null passed because JMSMessageId is not yet created.
            debugInfo = logDebugBeforePublish(null);
        }
        
        publisher.publish(message);
        
        // debug logging is on
        if(debugInfo != null) {
            // now messageId has been created - let's use it.
            logDebugAfterPublish(debugInfo, message.getJMSMessageID());
        }
        
        return null;
    }

    /**
     * INTERNAL:
     * Process received JMS message.
     * This method is used only by local (listening) connection.
     */
    public void onMessage(Message message) {
        String topic = null;
        String messageId = "";
        if(rcm.shouldLogDebugMessage()) {
            try {
                messageId = message.getJMSMessageID();
                logDebugOnReceiveMessage(messageId);
                topic = logDebugJMSTopic(message);
            } catch (JMSException ex) {
                // ignore
            }
        }
        
        ObjectMessage objectMessage = null;
        if (message instanceof ObjectMessage) {
            objectMessage = (ObjectMessage)message;        
        } else {
            if(rcm.shouldLogWarningMessage() && topic == null) {
                try {
                    topic = ((Topic)message.getJMSDestination()).getTopicName();
                } catch (JMSException ex) {
                    // ignore
                    topic = "";
                }
                Object[] args = { message.getClass().getName(), topic };
                rcm.logWarningWithoutLevelCheck("received_unexpected_message_type", args);
            }
            return;
        }
        
        Object object = null;
        try {
            object = objectMessage.getObject();
        } catch (Exception exception) {
            if(messageId.length() == 0) {
                try {
                    messageId = message.getJMSMessageID();
                } catch (JMSException ex) {
                    // ignore
                }
            }
            failDeserializeMessage(messageId, exception);
            return;
        }
        
        processReceivedObject(object, messageId);
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
        return !isLocal();
    }

    /**
     * INTERNAL:
     * This method is called by close method.
     * This method usually 
     * (but not always see comment to areAllResourcesFreedOnClose method)
     * frees all the resources.
     */
    protected void closeInternal() throws JMSException {
        if(areAllResourcesFreedOnClose()) {
            // There is no need to close the sessions, producers, and consumers of a closed TopicConnection.
            topicConnection.close();
        }
    }

    /**
     * INTERNAL:
     */
    protected String logDebugJMSTopic(Message message) throws JMSException {
        String topic = ((Topic)message.getJMSDestination()).getTopicName();
        Object[] args = { topic };
        // call logDebugWithoutLevelCheck to avoid the second rcm.shouldLogDebugMessage() check
        rcm.logDebugWithoutLevelCheck("retreived_remote_message_from_JMS_topic", args);
        return topic;
    }

    /**
     * INTERNAL:
     * This method is used by local (listening) connection only.
     * The only way to exit the loop is to set isActive to false -
     * there should be no uncaught exceptions thrown from inside the loop.
     * The execution exits the loop either in case of exception in remove connection on error mode;
     * or by trasportManager.removeLocalConnection() call
     * (which calls connection.close(), which sets isActive to false).
     */
    public void run() {
        JMSTopicTransportManager tm = (JMSTopicTransportManager)rcm.getTransportManager();
        rcm.logDebug("broadcast_connection_start_listening", getInfo());

        // indicates whether to create a new connection before exiting the thread.
        boolean shouldReconnect = false;
        // exception indicating that the received message is null.
        RuntimeException messageIsNullException = null;
        // isActive() returning false indicates that close method has been called.
        // Should never exit this loop because of an uncaught exception.
        while (isActive()) {
            try {
                Message message = subscriber.receive();
                // need the second isActive check here:
                // close method could have been called while subscriber.receive() was waiting.
                if (isActive()) {
                    if(message == null) {
                        try {
                            // user has a chance to handle exception - for instance to ignore it.
                            rcm.handleException(RemoteCommandManagerException.errorJMSMessageIsNull());
                            // exception has been handled, go to the next iteration.
                            continue;
                        } catch (RuntimeException ex) {
                            messageIsNullException = ex;
                            // throw a dummy JMSException to get into catch block
                            throw new JMSException("");
                        }
                    }
                    // process the message and log a warning without throwing exception if there was exception.
                    rcm.getServerPlatform().launchContainerRunnable(new JMSOnMessageHelper(message));
                }
            } catch (JMSException e) {
                // need the second isActive check here:
                // close method could have been called while subscriber.receive() was waiting.
                if (isActive()) {
                    RemoteCommandManagerException rcmException;
                    if(messageIsNullException != null) {
                        rcmException = RemoteCommandManagerException.errorReceivingJMSMessage(messageIsNullException);
                        messageIsNullException = null;
                    } else {
                        rcmException = RemoteCommandManagerException.errorReceivingJMSMessage(e);
                    }
                    if (tm.shouldRemoveConnectionOnError()) {
                        shouldReconnect = true;
                        Object[] args = { getServiceId(), rcmException };
                        rcm.logWarning("drop_connection_on_error", args);
                        // after connection is closed isActive will return false.
                        tm.removeLocalConnection();
                    } else {
                        try {
                            // user has a chance to handle exception:
                            // for instance to shut down the command manager.
                            rcm.handleException(rcmException);
                        } catch (RuntimeException ex) {
                            // Ignore the exception, sleep before going back to listening.
                            Object[] args = { toString(), rcmException, WAIT_ON_ERROR_RECEIVING_JMS_MESSAGE };
                            rcm.logWarning("broadcast_listening_sleep_on_error", args);
                            try {
                                Thread.sleep(WAIT_ON_ERROR_RECEIVING_JMS_MESSAGE);
                            } catch (InterruptedException interruptedEception) {
                                // Ignore
                            }
                        }
                    }
                }
            }
        }
        
        // Out of the loop - that means close method has been called.
        rcm.logDebug("broadcast_connection_stop_listening", getInfo());
        if(isClosing()) {
            try {
                // There is no need to close the sessions, producers, and consumers of a closed TopicConnection.
                topicConnection.close();
            } catch (JMSException closeException) {
                Object[] args = { displayString, closeException };
                rcm.logWarning("broadcast_exception_thrown_when_attempting_to_close_connection", args);
            } finally {
                rcm.logDebug("broadcast_connection_closed", getInfo());
                state = STATE_CLOSED;
            }
        }
        
        if(shouldReconnect && !tm.getRemoteCommandManager().isStopped()) {
            try {
                tm.createLocalConnection();
            } catch (RemoteCommandManagerException ex) {
                // Ignore exception - user had a chance to handle it in createLocalConnection method:
                // for instance to change host url and create a new local connection.
            }
        }
    }

    /**
     * INTERNAL:
     */
    protected void createDisplayString() {
        super.createDisplayString();
        displayString = (isLocal() ? "Local " : "External ") + displayString;
    }

    /**
     * INTERNAL:
     * Return whether a BroadcastConnection should check a ServiceId against its
     * own ServiceId to avoid the processing of Commands with the same ServiceId.
     * This should take place (return true) for a JMSTopicRemoteConnection.
     * @return boolean
     */
    protected boolean shouldCheckServiceId() {
        return true;
    }    
    
    class JMSOnMessageHelper implements Runnable {
        Message message = null;

        public JMSOnMessageHelper(Message message) {
            this.message = message;
        }

        public void run() {
            onMessage(message);
        }
    }
}
