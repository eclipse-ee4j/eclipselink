/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sessions;

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
     * This allows for TopLink to force a JTS transaction.
     */
    void beginTransaction(AbstractSession session);

    /**
     * INTERNAL:
     * Commit a transaction externally.
     * This allows for TopLink to force a JTS transaction.
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
     * This allows for TopLink to force a JTS transaction.
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
}