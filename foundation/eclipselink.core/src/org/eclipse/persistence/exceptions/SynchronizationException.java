/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.exceptions;

import org.eclipse.persistence.exceptions.i18n.ExceptionMessageGenerator;

/**
 * Instances of this exception are raised if a problem is detected
 * during synchronization of TopLink caches.
 * TopLink exceptions should only ever be thrown by TopLink code.
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.exceptions.RemoteCommandManagerException}
 */
public class SynchronizationException extends org.eclipse.persistence.exceptions.EclipseLinkException implements java.io.Serializable {
    // Note that the values not startingt with 80 are for intenal use in the exception and are not used as error
    // codes
    public static final int UNABLE_TO_PROPAGATE_CHANGES = 15001;
    public static final int DROPPING_REMOTE_CONNECTION = 15002;
    public static final int DROPPING_REMOTE_CONNECTION1 = 15003;
    public static final int ERROR_DOING_REMOTE_MERGE = 15004;
    public static final int ERROR_DOING_REMOTE_MERGE1 = 15005;
    public static final int ERROR_DOING_REMOTE_MERGE2 = 15006;
    public static final int ERROR_DOING_REMOTE_MERGE3 = 15007;
    public static final int ERROR_DOING_LOCAL_MERGE = 15008;
    public static final int ERROR_DOING_LOCAL_MERGE1 = 15009;
    public static final int ERROR_LOOKING_UP_LOCAL_HOST = 15010;
    public static final int ERROR_BINDING_CONTROLLER = 15011;
    public static final int ERROR_LOOKING_UP_CONTROLLER = 15012;
    public static final int ERROR_LOOKING_UP_JMS_SERVICE = 15013;
    public static final int ERROR_UNMARSHALLING_MSG = 15014;
    public static final int ERROR_UNMARSHALLING_MSG1 = 15015;
    public static final int ERROR_GETTING_SYNC_SERVICE = 15016;
    public static final int ERROR_NOTIFYING_CLUSTER = 15017;
    public static final int ERROR_JOINING_MULTICAST_GROUP = 15018;
    public static final int ERROR_DOING_REGISTER = 15019;
    public static final int ERROR_DOING_REGISTER1 = 15020;
    public static final int ERROR_DOING_REGISTER2 = 15021;
    public static final int ERROR_DOING_REGISTER3 = 15022;
    public static final int ERROR_RECEIVING_ANNOUNCEMENT = 15023;
    public static final int ERROR_RECEIVING_ANNOUNCEMENT1 = 15024;
    public static final int FAIL_TO_RESET_CACHE_SYNCH = 15025;

    public SynchronizationException() {
        super();
    }

    public SynchronizationException(String theMessage) {
        super(theMessage);
    }

    public static SynchronizationException unableToPropagateChanges(String sessionId, Exception internalEx) {
        SynchronizationException ex;
        Object[] args = { sessionId };
        ex = new SynchronizationException(ExceptionMessageGenerator.buildMessage(SynchronizationException.class, UNABLE_TO_PROPAGATE_CHANGES, args));
        ex.setErrorCode(UNABLE_TO_PROPAGATE_CHANGES);
        if (internalEx != null) {
            ex.setInternalException(internalEx);
        }
        return ex;
    }

    public static SynchronizationException droppingRemoteConnection(String sessionId) {
        SynchronizationException ex;
        if (sessionId != null) {
            Object[] args = { sessionId };
            ex = new SynchronizationException(ExceptionMessageGenerator.buildMessage(SynchronizationException.class, DROPPING_REMOTE_CONNECTION1, args));
        } else {
            Object[] args = {  };
            ex = new SynchronizationException(ExceptionMessageGenerator.buildMessage(SynchronizationException.class, DROPPING_REMOTE_CONNECTION, args));
        }
        ex.setErrorCode(DROPPING_REMOTE_CONNECTION);
        return ex;
    }

    public static SynchronizationException errorBindingController(String registryName, Exception internalEx) {
        SynchronizationException ex;
        Object[] args = { registryName };
        ex = new SynchronizationException(ExceptionMessageGenerator.buildMessage(SynchronizationException.class, ERROR_BINDING_CONTROLLER, args));
        ex.setErrorCode(ERROR_BINDING_CONTROLLER);
        if (internalEx != null) {
            ex.setInternalException(internalEx);
        }
        return ex;
    }

    public static SynchronizationException errorDoingLocalMerge(String sessionId, Exception internalEx) {
        SynchronizationException ex;
        if (sessionId != null) {
            Object[] args = { sessionId };
            ex = new SynchronizationException(ExceptionMessageGenerator.buildMessage(SynchronizationException.class, ERROR_DOING_LOCAL_MERGE1, args));
        } else {
            Object[] args = {  };
            ex = new SynchronizationException(ExceptionMessageGenerator.buildMessage(SynchronizationException.class, ERROR_DOING_LOCAL_MERGE, args));
        }
        ex.setErrorCode(ERROR_DOING_LOCAL_MERGE);
        if (internalEx != null) {
            ex.setInternalException(internalEx);
        }
        return ex;
    }

    public static SynchronizationException errorDoingRegister(String sessionId, Exception internalEx) {
        SynchronizationException ex;
        if (sessionId != null) {
            Object[] args = { sessionId };
            ex = new SynchronizationException(ExceptionMessageGenerator.buildMessage(SynchronizationException.class, ERROR_DOING_REGISTER2, args));
        } else {
            Object[] args = {  };
            ex = new SynchronizationException(ExceptionMessageGenerator.buildMessage(SynchronizationException.class, ERROR_DOING_REGISTER, args));
        }
        ex.setErrorCode(ERROR_DOING_REGISTER);
        if (internalEx != null) {
            ex.setInternalException(internalEx);
        }
        return ex;
    }

    public static SynchronizationException errorReceivingAnnouncement(String sessionId, Exception internalEx) {
        SynchronizationException ex;
        if (sessionId != null) {
            Object[] args = { sessionId };
            ex = new SynchronizationException(ExceptionMessageGenerator.buildMessage(SynchronizationException.class, ERROR_RECEIVING_ANNOUNCEMENT1, args));
        } else {
            Object[] args = {  };
            ex = new SynchronizationException(ExceptionMessageGenerator.buildMessage(SynchronizationException.class, ERROR_RECEIVING_ANNOUNCEMENT, args));
        }
        ex.setErrorCode(ERROR_RECEIVING_ANNOUNCEMENT);
        if (internalEx != null) {
            ex.setInternalException(internalEx);
        }
        return ex;
    }

    public static SynchronizationException errorDoingRemoteMerge(String sessionId, Exception internalEx) {
        SynchronizationException ex;
        if (sessionId != null) {
            Object[] args = { sessionId };
            ex = new SynchronizationException(ExceptionMessageGenerator.buildMessage(SynchronizationException.class, ERROR_DOING_REMOTE_MERGE1, args));
        } else {
            Object[] args = {  };
            ex = new SynchronizationException(ExceptionMessageGenerator.buildMessage(SynchronizationException.class, ERROR_DOING_REMOTE_MERGE, args));
        }
        ex.setErrorCode(ERROR_DOING_REMOTE_MERGE);
        if (internalEx != null) {
            ex.setInternalException(internalEx);
        }
        return ex;
    }

    public static SynchronizationException errorGettingSyncService(Exception internalEx) {
        SynchronizationException ex;
        Object[] args = {  };
        ex = new SynchronizationException(ExceptionMessageGenerator.buildMessage(SynchronizationException.class, ERROR_GETTING_SYNC_SERVICE, args));
        ex.setErrorCode(ERROR_GETTING_SYNC_SERVICE);
        ex.setInternalException(internalEx);
        return ex;
    }

    public static SynchronizationException errorNotifyingCluster(Exception internalEx) {
        SynchronizationException ex;
        Object[] args = {  };
        ex = new SynchronizationException(ExceptionMessageGenerator.buildMessage(SynchronizationException.class, ERROR_NOTIFYING_CLUSTER, args));
        ex.setErrorCode(ERROR_NOTIFYING_CLUSTER);
        if (internalEx != null) {
            ex.setInternalException(internalEx);
        }
        return ex;
    }

    public static SynchronizationException errorJoiningMulticastGroup(Exception internalEx) {
        SynchronizationException ex;
        Object[] args = {  };
        ex = new SynchronizationException(ExceptionMessageGenerator.buildMessage(SynchronizationException.class, ERROR_JOINING_MULTICAST_GROUP, args));
        ex.setErrorCode(ERROR_JOINING_MULTICAST_GROUP);
        ex.setInternalException(internalEx);
        return ex;
    }

    public static SynchronizationException errorLookingUpController(String registryName, Exception exception) {
        SynchronizationException ex;
        Object[] args = { registryName };
        ex = new SynchronizationException(ExceptionMessageGenerator.buildMessage(SynchronizationException.class, ERROR_LOOKING_UP_CONTROLLER, args));
        ex.setErrorCode(ERROR_LOOKING_UP_CONTROLLER);
        ex.setInternalException(exception);
        return ex;

    }

    public static SynchronizationException errorLookingUpJMSService(String topicName, Exception exception) {
        SynchronizationException ex;
        Object[] args = { topicName };
        ex = new SynchronizationException(ExceptionMessageGenerator.buildMessage(SynchronizationException.class, ERROR_LOOKING_UP_JMS_SERVICE, args));
        ex.setErrorCode(ERROR_LOOKING_UP_JMS_SERVICE);
        ex.setInternalException(exception);
        return ex;

    }

    public static SynchronizationException errorLookingUpLocalHost(Exception exception) {
        SynchronizationException ex;
        Object[] args = {  };
        ex = new SynchronizationException(ExceptionMessageGenerator.buildMessage(SynchronizationException.class, ERROR_LOOKING_UP_LOCAL_HOST, args));
        ex.setErrorCode(ERROR_LOOKING_UP_LOCAL_HOST);
        ex.setInternalException(exception);
        return ex;
    }

    public static SynchronizationException errorUnmarshallingMsg(String sessionId) {
        SynchronizationException ex;
        if (sessionId != null) {
            Object[] args = { sessionId };
            ex = new SynchronizationException(ExceptionMessageGenerator.buildMessage(SynchronizationException.class, ERROR_UNMARSHALLING_MSG1, args));
        } else {
            Object[] args = {  };
            ex = new SynchronizationException(ExceptionMessageGenerator.buildMessage(SynchronizationException.class, ERROR_UNMARSHALLING_MSG, args));
        }
        ex.setErrorCode(ERROR_UNMARSHALLING_MSG);
        return ex;
    }

    public static SynchronizationException failToResetCacheSynch() {
        Object[] args = {  };

        SynchronizationException synchException = new SynchronizationException(ExceptionMessageGenerator.buildMessage(SynchronizationException.class, FAIL_TO_RESET_CACHE_SYNCH, args));
        synchException.setErrorCode(FAIL_TO_RESET_CACHE_SYNCH);
        return synchException;
    }
}