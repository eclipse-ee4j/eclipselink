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

import org.eclipse.persistence.exceptions.i18n.*;
import org.eclipse.persistence.internal.helper.Helper;

/**
 * <p>
 * <b>Purpose</b>:This exception wraps all RMI or CORBA or IO exception that may occur.
 */
public class CommunicationException extends EclipseLinkException {

    /**
     * PUBLIC:
     * Creates a CommunicationException.
     * @param theMessage This is the exception message
     */
    public static final int ERROR_SENDING_CONNECTION_SERVICE = 12000;
    public static final int UNABLE_TO_CONNECT = 12001;
    public static final int UNABLE_TO_PROPAGATE_CHANGES = 12002;
    public static final int ERROR_IN_INVOCATION = 12003;
    public static final int ERROR_SENDING_MESSAGE = 12004;

    public CommunicationException(String theMessage) {
        super(theMessage);
    }

    /**
     * PUBLIC:
     * Creates a CommunicationException.
     * @param theMessage the detailed message
     * @param internalException the handle of the exception
     */
    public CommunicationException(String message, Exception internalException) {
        super(message, internalException);
    }

    public static CommunicationException errorSendingConnectionService(String serviceName, Exception exception) {
        Object[] args = { serviceName };

        CommunicationException communicationException = new CommunicationException(ExceptionMessageGenerator.buildMessage(CommunicationException.class, ERROR_SENDING_CONNECTION_SERVICE, args), exception);
        communicationException.setErrorCode(ERROR_SENDING_CONNECTION_SERVICE);
        return communicationException;
    }

    public static CommunicationException unableToConnect(String serviceName, Exception exception) {
        Object[] args = { serviceName };

        CommunicationException communicationException = new CommunicationException(ExceptionMessageGenerator.buildMessage(CommunicationException.class, UNABLE_TO_CONNECT, args), exception);
        communicationException.setErrorCode(UNABLE_TO_CONNECT);
        return communicationException;
    }

    public static CommunicationException unableToPropagateChanges(String serviceName, Exception exception) {
        Object[] args = { serviceName };

        CommunicationException communicationException = new CommunicationException(ExceptionMessageGenerator.buildMessage(CommunicationException.class, UNABLE_TO_PROPAGATE_CHANGES, args), exception);
        communicationException.setErrorCode(UNABLE_TO_PROPAGATE_CHANGES);
        return communicationException;
    }

    public static CommunicationException errorInInvocation(Exception exception) {
        Object[] args = { Helper.printStackTraceToString(exception) };

        CommunicationException communicationException = new CommunicationException(ExceptionMessageGenerator.buildMessage(CommunicationException.class, ERROR_IN_INVOCATION, args), exception);
        communicationException.setErrorCode(ERROR_IN_INVOCATION);
        return communicationException;
    }

    public static CommunicationException errorSendingMessage(String serviceId, Exception exception) {
        Object[] args = { serviceId };

        CommunicationException communicationException = new CommunicationException(ExceptionMessageGenerator.buildMessage(CommunicationException.class, ERROR_SENDING_MESSAGE, args), exception);
        communicationException.setErrorCode(ERROR_SENDING_MESSAGE);
        return communicationException;
    }
}
