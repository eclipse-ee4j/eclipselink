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
 *     cdelahun - Bug 214534: added message for JMSPublishingHelper error checking
 ******************************************************************************/  
package org.eclipse.persistence.exceptions;

import org.eclipse.persistence.exceptions.i18n.ExceptionMessageGenerator;

/**
 * Instances of this exception are raised if a problem is detected in the
 * RemoteCommandManager (RCM) feature.
 * TopLink exceptions should only ever be thrown by TopLink code.
 */
public class RemoteCommandManagerException extends org.eclipse.persistence.exceptions.EclipseLinkException implements java.io.Serializable {
    // Exceptions for RCM are in range 22000 - 23000. 
    // Leave the first 100 for DiscoveryManagerException sub exceptions
    public static final int ERROR_OBTAINING_CONTEXT_FOR_JNDI = 22101;
    public static final int ERROR_BINDING_CONNECTION = 22102;
    public static final int ERROR_LOOKING_UP_REMOTE_CONNECTION = 22103;
    public static final int ERROR_GETTING_HOST_NAME = 22104;
    public static final int ERROR_PROPAGATING_COMMAND = 22105;
    public static final int ERROR_CREATING_JMS_CONNECTION = 22106;
    public static final int ERROR_UNBINDING_LOCAL_CONNECTION = 22107;

    // CORBA
    public static final int ERROR_SERIALIZE_OR_DESERIALIZE_COMMAND = 22108;
    public static final int ERROR_RECEIVING_JMS_MESSAGE = 22109;
    public static final int ERROR_DISCOVERING_IP_ADDRESS = 22110;

    //ServerPlatform exception
    public static final int ERROR_GETTING_SERVERPLATFORM = 22111;
    
    // JMS
    public static final int ERROR_CREATING_LOCAL_JMS_CONNECTION = 22112;
    
    // Broadcast
    public static final int ERROR_CREATING_OC4J_JGROUPS_CONNECTION = 22113;
    
    public static final int ERROR_DESERIALIZE_REMOTE_COMMAND = 22114;
    public static final int ERROR_PROCESSING_REMOTE_COMMAND = 22115;
    
    // JMS
    public static final int ERROR_RECEIVED_JMS_MESSAGE_IS_NULL = 22116;
    //JMS PUBLISHING 
    public static final int RCM_UNINITIALIZED_OR_CLOSED = 22117;

    public RemoteCommandManagerException() {
        super();
    }

    public RemoteCommandManagerException(String theMessage) {
        super(theMessage);
    }

    public static RemoteCommandManagerException errorObtainingContext(Exception internalEx) {
        RemoteCommandManagerException ex;
        Object[] args = { };
        ex = new RemoteCommandManagerException(ExceptionMessageGenerator.buildMessage(RemoteCommandManagerException.class, ERROR_OBTAINING_CONTEXT_FOR_JNDI, args));
        ex.setErrorCode(ERROR_OBTAINING_CONTEXT_FOR_JNDI);
        if (internalEx != null) {
            ex.setInternalException(internalEx);
        }
        return ex;
    }

    public static RemoteCommandManagerException errorSerializeOrDeserialzeCommand(Exception internalEx) {
        RemoteCommandManagerException ex;
        Object[] args = {  };
        ex = new RemoteCommandManagerException(ExceptionMessageGenerator.buildMessage(RemoteCommandManagerException.class, ERROR_SERIALIZE_OR_DESERIALIZE_COMMAND, args));
        ex.setErrorCode(ERROR_SERIALIZE_OR_DESERIALIZE_COMMAND);
        if (internalEx != null) {
            ex.setInternalException(internalEx);
        }
        return ex;
    }

    public static RemoteCommandManagerException errorBindingConnection(String bindName, Exception internalEx) {
        RemoteCommandManagerException ex;
        Object[] args = { bindName };
        ex = new RemoteCommandManagerException(ExceptionMessageGenerator.buildMessage(RemoteCommandManagerException.class, ERROR_BINDING_CONNECTION, args));
        ex.setErrorCode(ERROR_BINDING_CONNECTION);
        if (internalEx != null) {
            ex.setInternalException(internalEx);
        }
        return ex;
    }

    public static RemoteCommandManagerException errorLookingUpRemoteConnection(String remoteName, String url, Exception internalEx) {
        RemoteCommandManagerException ex;
        Object[] args = { remoteName, url };
        ex = new RemoteCommandManagerException(ExceptionMessageGenerator.buildMessage(RemoteCommandManagerException.class, ERROR_LOOKING_UP_REMOTE_CONNECTION, args));
        ex.setErrorCode(ERROR_LOOKING_UP_REMOTE_CONNECTION);
        if (internalEx != null) {
            ex.setInternalException(internalEx);
        }
        return ex;
    }

    public static RemoteCommandManagerException errorGettingHostName(Exception internalEx) {
        RemoteCommandManagerException ex;
        Object[] args = { };
        ex = new RemoteCommandManagerException(ExceptionMessageGenerator.buildMessage(RemoteCommandManagerException.class, ERROR_GETTING_HOST_NAME, args));
        ex.setErrorCode(ERROR_GETTING_HOST_NAME);
        if (internalEx != null) {
            ex.setInternalException(internalEx);
        }
        return ex;
    }

    public static RemoteCommandManagerException unableToPropagateCommand(String connectionString, Throwable internalEx) {
        Object[] args = { connectionString };
        RemoteCommandManagerException ex = new RemoteCommandManagerException(ExceptionMessageGenerator.buildMessage(RemoteCommandManagerException.class, ERROR_PROPAGATING_COMMAND, args));
        ex.setErrorCode(ERROR_PROPAGATING_COMMAND);
        if (internalEx != null) {
            ex.setInternalException(internalEx);
        }
        return ex;
    }

    public static RemoteCommandManagerException errorCreatingJMSConnection(String topicName, String topicFactory, Throwable internalEx) {
        Object[] args = { topicName, topicFactory };
        RemoteCommandManagerException ex = new RemoteCommandManagerException(ExceptionMessageGenerator.buildMessage(RemoteCommandManagerException.class, ERROR_CREATING_JMS_CONNECTION, args));
        ex.setErrorCode(ERROR_CREATING_JMS_CONNECTION);
        if (internalEx != null) {
            ex.setInternalException(internalEx);
        }
        return ex;
    }

    public static RemoteCommandManagerException errorUnbindingLocalConnection(String unbindName, Exception internalEx) {
        RemoteCommandManagerException ex;
        Object[] args = { unbindName };
        ex = new RemoteCommandManagerException(ExceptionMessageGenerator.buildMessage(RemoteCommandManagerException.class, ERROR_UNBINDING_LOCAL_CONNECTION, args));
        ex.setErrorCode(ERROR_UNBINDING_LOCAL_CONNECTION);
        if (internalEx != null) {
            ex.setInternalException(internalEx);
        }
        return ex;
    }

    public static RemoteCommandManagerException errorReceivingJMSMessage(Exception internalEx) {
        Object[] args = { };
        RemoteCommandManagerException ex = new RemoteCommandManagerException(ExceptionMessageGenerator.buildMessage(RemoteCommandManagerException.class, ERROR_RECEIVING_JMS_MESSAGE, args));
        ex.setErrorCode(ERROR_RECEIVING_JMS_MESSAGE);
        if (internalEx != null) {
            ex.setInternalException(internalEx);
        }
        return ex;
    }

    public static RemoteCommandManagerException errorDiscoveringLocalHostIPAddress(Exception internalEx) {
        Object[] args = { };
        RemoteCommandManagerException ex = new RemoteCommandManagerException(ExceptionMessageGenerator.buildMessage(RemoteCommandManagerException.class, ERROR_DISCOVERING_IP_ADDRESS, args));
        ex.setErrorCode(ERROR_DISCOVERING_IP_ADDRESS);
        if (internalEx != null) {
            ex.setInternalException(internalEx);
        }
        return ex;
    }

    public static RemoteCommandManagerException errorGettingServerPlatform() {
        Object[] args = {  };
        RemoteCommandManagerException ex = new RemoteCommandManagerException(ExceptionMessageGenerator.buildMessage(RemoteCommandManagerException.class, ERROR_GETTING_SERVERPLATFORM, args));
        ex.setErrorCode(ERROR_GETTING_SERVERPLATFORM);
        return ex;
    }

    public static RemoteCommandManagerException errorCreatingLocalJMSConnection(String topicName, String topicFactory, Throwable internalEx) {
        Object[] args = { topicName, topicFactory };
        RemoteCommandManagerException ex = new RemoteCommandManagerException(ExceptionMessageGenerator.buildMessage(RemoteCommandManagerException.class, ERROR_CREATING_LOCAL_JMS_CONNECTION, args));
        ex.setErrorCode(ERROR_CREATING_LOCAL_JMS_CONNECTION);
        if (internalEx != null) {
            ex.setInternalException(internalEx);
        }
        return ex;
    }

    public static RemoteCommandManagerException errorCreatingOc4jJGroupsConnection(String serviceId, String topicName, String topicFactory, Throwable internalEx) {
        Object[] args = { serviceId, topicName, topicFactory };
        RemoteCommandManagerException ex = new RemoteCommandManagerException(ExceptionMessageGenerator.buildMessage(RemoteCommandManagerException.class, ERROR_CREATING_OC4J_JGROUPS_CONNECTION, args));
        ex.setErrorCode(ERROR_CREATING_OC4J_JGROUPS_CONNECTION);
        if (internalEx != null) {
            ex.setInternalException(internalEx);
        }
        return ex;
    }

    public static RemoteCommandManagerException errorDeserializeRemoteCommand(String connection, String messageId, Exception internalEx) {
        RemoteCommandManagerException ex;
        Object[] args = { connection, messageId };
        ex = new RemoteCommandManagerException(ExceptionMessageGenerator.buildMessage(RemoteCommandManagerException.class, ERROR_DESERIALIZE_REMOTE_COMMAND, args));
        ex.setErrorCode(ERROR_DESERIALIZE_REMOTE_COMMAND);
        if (internalEx != null) {
            ex.setInternalException(internalEx);
        }
        return ex;
    }

    public static RemoteCommandManagerException errorProcessingRemoteCommand(String connection, String messageId, String sourceServiceId, String commandClassName, Throwable internalEx) {
        Object[] args = { connection, messageId, sourceServiceId, commandClassName };
        RemoteCommandManagerException ex = new RemoteCommandManagerException(ExceptionMessageGenerator.buildMessage(RemoteCommandManagerException.class, ERROR_PROCESSING_REMOTE_COMMAND, args));
        ex.setErrorCode(ERROR_PROCESSING_REMOTE_COMMAND);
        if (internalEx != null) {
            ex.setInternalException(internalEx);
        }
        return ex;
    }

    public static RemoteCommandManagerException errorJMSMessageIsNull() {
        Object[] args = { };
        RemoteCommandManagerException ex = new RemoteCommandManagerException(ExceptionMessageGenerator.buildMessage(RemoteCommandManagerException.class, ERROR_RECEIVED_JMS_MESSAGE_IS_NULL, args));
        ex.setErrorCode(ERROR_RECEIVED_JMS_MESSAGE_IS_NULL);
        return ex;
    }
    
    public static RemoteCommandManagerException remoteCommandManagerIsClosed() {  
        Object[] args = { };  
        RemoteCommandManagerException ex = new RemoteCommandManagerException(ExceptionMessageGenerator.buildMessage(RemoteCommandManagerException.class,RCM_UNINITIALIZED_OR_CLOSED,args));  
        ex.setErrorCode(RCM_UNINITIALIZED_OR_CLOSED);
        return ex;  
    }
}
