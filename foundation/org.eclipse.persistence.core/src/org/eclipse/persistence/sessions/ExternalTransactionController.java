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
package org.eclipse.persistence.sessions;

import org.eclipse.persistence.internal.sequencing.SequencingCallback;
import org.eclipse.persistence.internal.sequencing.SequencingCallbackFactory;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.exceptions.*;

/**
 * <p>
 * <b>Purpose</b>: Interface for external transaction management.
 * <p>
 * <b>Description</b>: This interface represents a delegate to be used for external
 * transaction management. The implementing class may interface to an OMG OTS service,
 * a Java JTA service or a manufacturer's specific implementation of these services.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Define the API for UnitOfWork to add a listener to the externally controlled transaction.
 * </ul>
 */
public interface ExternalTransactionController {

    /**
     * INTERNAL:
     * Begin a transaction externally.
     * This allows for EclipseLink to force a JTS transaction.
     */
    void beginTransaction(AbstractSession session);

    /**
     * INTERNAL:
     * Commit a transaction externally.
     * This allows for EclipseLink to force a JTS transaction.
     */
    void commitTransaction(AbstractSession session);

    /**
     * INTERNAL:
     * Return the active unit of work for the current active external transaction.
     */
    UnitOfWorkImpl getActiveUnitOfWork();

    /**
     * INTERNAL:
     * Return the manager's session.
     */
    AbstractSession getSession();

    /**
     * INTERNAL:
     * Register a listener on the unit of work.
     * The listener will callback to the unit of work to tell it to commit and merge.
     */
    void registerSynchronizationListener(UnitOfWorkImpl uow, AbstractSession session) throws DatabaseException;

    /**
     * INTERNAL:
     * Rollback a transaction externally.
     * This allows for EclipseLink to force a JTS transaction.
     */
    void rollbackTransaction(AbstractSession session);

    /**
     * INTERNAL:
     * Marks the external transaction for rollback only.
     */
    void markTransactionForRollback();

    /**
     * INTERNAL:
     * Set the manager's session.
     */
    void setSession(AbstractSession session);
    
    /**
     * INTERNAL:
     * Initializes sequencing listeners.
     * Always clears sequencing listeners first.
     * There are two methods calling this method:
     * 1. setSession method - this could lead to initialization of sequencing listeners
     * only if sequencing already connected (that would happen if setSession is called
     * after session.login, which is normally not the case).
     * 2. in the very end of connecting sequencing,
     * after it's determined whether sequencing callbacks (and therefore listeners)
     * will be required.
     */
    public void initializeSequencingListeners();

    /**
     * INTERNAL:
     * Returns sequencingCallback for the current active external transaction.
     * DatabaseSession is passed for the sake of SessionBroker case.
     * This method requires active external transaction.
     */
    public SequencingCallback getActiveSequencingCallback(DatabaseSession dbSession, SequencingCallbackFactory sequencingCallbackFactory);
    
    /**
     * INTERNAL:
     * Clears sequencing listeners.
     * Called by initializeSequencingListeners and by sequencing on disconnect.
     */
    public void clearSequencingListeners();


    /**
     * Return the exception handler used to handle or wrap exceptions thrown in before/after completion.
     */
    public ExceptionHandler getExceptionHandler();

    /**
     * Set an exception handler to handle or wrap exceptions thrown in before/after completion.
     */
    public void setExceptionHandler(ExceptionHandler exceptionHandler);
}
