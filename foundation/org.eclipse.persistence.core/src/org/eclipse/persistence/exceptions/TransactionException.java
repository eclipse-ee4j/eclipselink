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
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;

public class TransactionException extends EclipseLinkException {
    public static final int ERROR_DOING_JNDI_LOOKUP = 23001;
    public static final int ERROR_GETTING_TRANSACTION_STATUS = 23002;
    public static final int ERROR_GETTING_TRANSACTION = 23003;
    public static final int ERROR_OBTAINING_TRANSACTION_MANAGER = 23004;
    public static final int ERROR_BINDING_TO_TRANSACTION = 23005;
    public static final int ERROR_BEGINNING_TRANSACTION = 23006;
    public static final int ERROR_COMMITTING_TRANSACTION = 23007;
    public static final int ERROR_ROLLING_BACK_TRANSACTION = 23008;
    public static final int ERROR_MARKING_TRANSACTION_FOR_ROLLBACK = 23009;
    public static final int ERROR_NO_EXTERNAL_TRANSACTION_ACTIVE = 23010;
    public static final int ERROR_INACTIVE_UOW = 23011;
    public static final int ERROR_NO_TRANSACTION_ACTIVE = 23012;
    public static final int ERROR_TRANSACTION_IS_ACTIVE = 23013;
    public static final int ENTITY_TRANSACTION_WITH_JTA_NOT_ALLOWED = 23014;
    public static final int CANNOT_ENLIST_MULTIPLE_DATASOURCES = 23015;
    public static final int EXCEPTION_IN_PROXY_EXECUTION = 23016;
    public static final int ERROR_NO_ENTITY_TRANSACTION_ACTIVE = 23017;

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Exception internalException) {
        super(message, internalException);
    }

    public static TransactionException jndiLookupException(String jndiName, Exception internalException) {
        Object[] args = { jndiName };
        TransactionException ex = new TransactionException(ExceptionMessageGenerator.buildMessage(TransactionException.class, ERROR_DOING_JNDI_LOOKUP, args));
        ex.setErrorCode(ERROR_DOING_JNDI_LOOKUP);
        ex.setInternalException(internalException);
        return ex;
    }

    public static TransactionException errorGettingExternalTransactionStatus(Exception internalException) {
        Object[] args = {  };
        TransactionException ex = new TransactionException(ExceptionMessageGenerator.buildMessage(TransactionException.class, ERROR_GETTING_TRANSACTION_STATUS, args));
        ex.setErrorCode(ERROR_GETTING_TRANSACTION_STATUS);
        ex.setInternalException(internalException);
        return ex;
    }

    public static TransactionException errorGettingExternalTransaction(Exception internalException) {
        Object[] args = {  };
        TransactionException ex = new TransactionException(ExceptionMessageGenerator.buildMessage(TransactionException.class, ERROR_GETTING_TRANSACTION, args));
        ex.setErrorCode(ERROR_GETTING_TRANSACTION);
        ex.setInternalException(internalException);
        return ex;
    }

    public static TransactionException errorBindingToExternalTransaction(Exception internalException) {
        Object[] args = {  };
        TransactionException ex = new TransactionException(ExceptionMessageGenerator.buildMessage(TransactionException.class, ERROR_BINDING_TO_TRANSACTION, args));
        ex.setErrorCode(ERROR_BINDING_TO_TRANSACTION);
        ex.setInternalException(internalException);
        return ex;
    }

    public static TransactionException errorBeginningExternalTransaction(Exception internalException) {
        Object[] args = {  };
        TransactionException ex = new TransactionException(ExceptionMessageGenerator.buildMessage(TransactionException.class, ERROR_BEGINNING_TRANSACTION, args));
        ex.setErrorCode(ERROR_BEGINNING_TRANSACTION);
        ex.setInternalException(internalException);
        return ex;
    }

    public static TransactionException errorCommittingExternalTransaction(Exception internalException) {
        Object[] args = {  };
        TransactionException ex = new TransactionException(ExceptionMessageGenerator.buildMessage(TransactionException.class, ERROR_COMMITTING_TRANSACTION, args));
        ex.setErrorCode(ERROR_COMMITTING_TRANSACTION);
        ex.setInternalException(internalException);
        return ex;
    }

    public static TransactionException errorRollingBackExternalTransaction(Exception internalException) {
        Object[] args = {  };
        TransactionException ex = new TransactionException(ExceptionMessageGenerator.buildMessage(TransactionException.class, ERROR_ROLLING_BACK_TRANSACTION, args));
        ex.setErrorCode(ERROR_ROLLING_BACK_TRANSACTION);
        ex.setInternalException(internalException);
        return ex;
    }

    public static TransactionException errorMarkingTransactionForRollback(Exception internalException) {
        Object[] args = {  };
        TransactionException ex = new TransactionException(ExceptionMessageGenerator.buildMessage(TransactionException.class, ERROR_MARKING_TRANSACTION_FOR_ROLLBACK, args));
        ex.setErrorCode(ERROR_MARKING_TRANSACTION_FOR_ROLLBACK);
        ex.setInternalException(internalException);
        return ex;
    }

    public static TransactionException externalTransactionNotActive() {
        Object[] args = {  };
        TransactionException ex = new TransactionException(ExceptionMessageGenerator.buildMessage(TransactionException.class, ERROR_NO_EXTERNAL_TRANSACTION_ACTIVE, args));
        ex.setErrorCode(ERROR_NO_EXTERNAL_TRANSACTION_ACTIVE);
        return ex;
    }

    public static TransactionException inactiveUnitOfWork(UnitOfWorkImpl unitOfWork) {
        Object[] args = { unitOfWork };
        TransactionException ex = new TransactionException(ExceptionMessageGenerator.buildMessage(TransactionException.class, ERROR_INACTIVE_UOW, args));
        ex.setErrorCode(ERROR_INACTIVE_UOW);
        return ex;
    }

    public static TransactionException errorObtainingTransactionManager(Exception internalException) {
        Object[] args = {  };
        TransactionException ex = new TransactionException(ExceptionMessageGenerator.buildMessage(TransactionException.class, ERROR_OBTAINING_TRANSACTION_MANAGER, args));
        ex.setErrorCode(ERROR_OBTAINING_TRANSACTION_MANAGER);
        ex.setInternalException(internalException);
        return ex;
    }
    
    public static TransactionException transactionNotActive() {
        Object[] args = {  };
        TransactionException ex = new TransactionException(ExceptionMessageGenerator.buildMessage(TransactionException.class, ERROR_NO_TRANSACTION_ACTIVE, args));
        ex.setErrorCode(ERROR_NO_TRANSACTION_ACTIVE);
        return ex;
    }
    public static TransactionException transactionIsActive() {
        Object[] args = {  };
        TransactionException ex = new TransactionException(ExceptionMessageGenerator.buildMessage(TransactionException.class, ERROR_TRANSACTION_IS_ACTIVE, args));
        ex.setErrorCode(ERROR_TRANSACTION_IS_ACTIVE);
        return ex;
    }

    public static TransactionException entityTransactionWithJTANotAllowed() {
        Object[] args = {  };
        TransactionException ex = new TransactionException(ExceptionMessageGenerator.buildMessage(TransactionException.class, ENTITY_TRANSACTION_WITH_JTA_NOT_ALLOWED, args));
        ex.setErrorCode(ENTITY_TRANSACTION_WITH_JTA_NOT_ALLOWED);
        return ex;
    }

    public static TransactionException multipleResourceException() {
        Object[] args = {  };
        TransactionException ex = new TransactionException(ExceptionMessageGenerator.buildMessage(TransactionException.class, CANNOT_ENLIST_MULTIPLE_DATASOURCES, args));
        ex.setErrorCode(CANNOT_ENLIST_MULTIPLE_DATASOURCES);
        return ex;
    }

    public static TransactionException internalProxyException(Exception ex1) {
        Object[] args = {  };
        TransactionException ex = new TransactionException(ExceptionMessageGenerator.buildMessage(TransactionException.class, EXCEPTION_IN_PROXY_EXECUTION, args), ex1);
        ex.setErrorCode(EXCEPTION_IN_PROXY_EXECUTION);
        return ex;
    }

    public static TransactionException entityTransactionNotActive() {
        Object[] args = {  };
        TransactionException ex = new TransactionException(ExceptionMessageGenerator.buildMessage(TransactionException.class, ERROR_NO_ENTITY_TRANSACTION_ACTIVE, args));
        ex.setErrorCode(ERROR_NO_ENTITY_TRANSACTION_ACTIVE);
        return ex;
    }

}
