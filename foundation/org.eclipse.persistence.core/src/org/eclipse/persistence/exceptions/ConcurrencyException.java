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
package org.eclipse.persistence.exceptions;

import org.eclipse.persistence.exceptions.i18n.ExceptionMessageGenerator;

/**
 * <P><B>Purpose</B>: Concurrency deadlock or interupts will raise this exception.
 */
public class ConcurrencyException extends EclipseLinkException {
    public final static int WAIT_WAS_INTERRUPTED = 2001;
    public final static int WAIT_FAILURE_SERVER = 2002;
    public final static int WAIT_FAILURE_CLIENT = 2003;
    public final static int SIGNAL_ATTEMPTED_BEFORE_WAIT = 2004;
    public final static int WAIT_FAILURE_SEQ_DATABASE_SESSION = 2005;
    public final static int SEQUENCING_MULTITHREAD_THRU_CONNECTION = 2006;
    public final static int MAX_TRIES_EXCEDED_FOR_LOCK_ON_CLONE = 2007;
    public final static int MAX_TRIES_EXCEDED_FOR_LOCK_ON_MERGE = 2008;
    public final static int MAX_TRIES_EXCEDED_FOR_LOCK_ON_BUILD_OBJECT = 2009;
    public final static int ACTIVE_LOCK_ALREADY_TRANSITIONED = 2010;

    /**
     * INTERNAL:
     * TopLink exceptions should only be thrown by TopLink.
     */
    protected ConcurrencyException(String theMessage) {
        super(theMessage);
    }

    /**
     * INTERNAL:
     * TopLink exceptions should only be thrown by TopLink.
     */
    protected ConcurrencyException(String theMessage, Exception exception) {
        super(theMessage, exception);
    }

    public static ConcurrencyException activeLockAlreadyTransitioned(Thread currentThread) {
        Object[] args = { currentThread};

        ConcurrencyException concurrencyException = new ConcurrencyException(ExceptionMessageGenerator.buildMessage(ConcurrencyException.class, ACTIVE_LOCK_ALREADY_TRANSITIONED, args));
        concurrencyException.setErrorCode(ACTIVE_LOCK_ALREADY_TRANSITIONED);
        return concurrencyException;
    }


    public static ConcurrencyException maxTriesLockOnCloneExceded(Object objectToClone) {
        Object[] args = { objectToClone, CR };

        ConcurrencyException concurrencyException = new ConcurrencyException(ExceptionMessageGenerator.buildMessage(ConcurrencyException.class, MAX_TRIES_EXCEDED_FOR_LOCK_ON_CLONE, args));
        concurrencyException.setErrorCode(MAX_TRIES_EXCEDED_FOR_LOCK_ON_CLONE);
        return concurrencyException;
    }

    public static ConcurrencyException maxTriesLockOnMergeExceded(Object objectToClone) {
        Object[] args = { objectToClone, CR };

        ConcurrencyException concurrencyException = new ConcurrencyException(ExceptionMessageGenerator.buildMessage(ConcurrencyException.class, MAX_TRIES_EXCEDED_FOR_LOCK_ON_MERGE, args));
        concurrencyException.setErrorCode(MAX_TRIES_EXCEDED_FOR_LOCK_ON_MERGE);
        return concurrencyException;
    }

    public static ConcurrencyException maxTriesLockOnBuildObjectExceded(Thread cacheKeyThread, Thread currentThread) {
        Object[] args = { cacheKeyThread, currentThread, CR };

        ConcurrencyException concurrencyException = new ConcurrencyException(ExceptionMessageGenerator.buildMessage(ConcurrencyException.class, MAX_TRIES_EXCEDED_FOR_LOCK_ON_BUILD_OBJECT, args));
        concurrencyException.setErrorCode(MAX_TRIES_EXCEDED_FOR_LOCK_ON_BUILD_OBJECT);
        return concurrencyException;
    }

    public static ConcurrencyException signalAttemptedBeforeWait() {
        Object[] args = { CR };

        ConcurrencyException concurrencyException = new ConcurrencyException(ExceptionMessageGenerator.buildMessage(ConcurrencyException.class, SIGNAL_ATTEMPTED_BEFORE_WAIT, args));
        concurrencyException.setErrorCode(SIGNAL_ATTEMPTED_BEFORE_WAIT);
        return concurrencyException;
    }

    public static ConcurrencyException waitFailureOnClientSession(InterruptedException exception) {
        Object[] args = {  };

        ConcurrencyException concurrencyException = new ConcurrencyException(ExceptionMessageGenerator.buildMessage(ConcurrencyException.class, WAIT_FAILURE_CLIENT, args), exception);
        concurrencyException.setErrorCode(WAIT_FAILURE_CLIENT);
        return concurrencyException;
    }

    public static ConcurrencyException waitFailureOnServerSession(InterruptedException exception) {
        Object[] args = {  };

        ConcurrencyException concurrencyException = new ConcurrencyException(ExceptionMessageGenerator.buildMessage(ConcurrencyException.class, WAIT_FAILURE_SERVER, args), exception);
        concurrencyException.setErrorCode(WAIT_FAILURE_SERVER);
        return concurrencyException;
    }

    public static ConcurrencyException waitWasInterrupted(String message) {
        Object[] args = { CR, message };

        ConcurrencyException concurrencyException = new ConcurrencyException(ExceptionMessageGenerator.buildMessage(ConcurrencyException.class, WAIT_WAS_INTERRUPTED, args));
        concurrencyException.setErrorCode(WAIT_WAS_INTERRUPTED);
        return concurrencyException;
    }

    public static ConcurrencyException waitFailureOnSequencingForDatabaseSession(InterruptedException exception) {
        Object[] args = {  };

        ConcurrencyException concurrencyException = new ConcurrencyException(ExceptionMessageGenerator.buildMessage(ConcurrencyException.class, WAIT_FAILURE_SEQ_DATABASE_SESSION, args), exception);
        concurrencyException.setErrorCode(WAIT_FAILURE_SEQ_DATABASE_SESSION);
        return concurrencyException;
    }

    public static ConcurrencyException sequencingMultithreadThruConnection(String accessor) {
        Object[] args = { accessor };

        ConcurrencyException concurrencyException = new ConcurrencyException(ExceptionMessageGenerator.buildMessage(ConcurrencyException.class, SEQUENCING_MULTITHREAD_THRU_CONNECTION, args));
        concurrencyException.setErrorCode(SEQUENCING_MULTITHREAD_THRU_CONNECTION);
        return concurrencyException;
    }
}
