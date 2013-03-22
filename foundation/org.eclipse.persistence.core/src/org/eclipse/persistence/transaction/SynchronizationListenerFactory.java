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
package org.eclipse.persistence.transaction;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;

/**
 * <p>
 * <b>Purpose</b>: Generate synchronization listener objects of the appropriate type.
 * <p>
 * <b>Description</b>: This interface will be used by the AbstractTransactionController
 * to obtain a listener that it will register against the external transaction in order
 * to synchronize the unit of work.
 * All new listener classes should implement this interface.
 *
 * @see AbstractSynchronizationListener
 */
public interface SynchronizationListenerFactory {

    /**
     * INTERNAL:
     * Create and return the synchronization listener object that can be registered
     * to receive transaction notification callbacks. The type of synchronization object
     * that gets returned will be dependent upon the transaction system
     */
    public AbstractSynchronizationListener newSynchronizationListener(UnitOfWorkImpl unitOfWork, AbstractSession session, Object transaction, AbstractTransactionController controller);
}
