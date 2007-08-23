/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.databaseaccess;

import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * INTERNAL:
 *    BatchWritingMechanism is a private interface, used by the DatabaseAccessor. it provides the required
 *  behaviour for batching statements, for write.<p>
 *    There are currently two types of the Mechanism impelement, one to handle the tradition dynamic SQL
 *  batching and another to handle Parameterized SQL.  Depending on what is passed to these mechanisms
 *  they may decide to switch the current one out to the alternative type.<p>
 *
 *    @since OracleAS TopLink 10<i>g</i> (9.0.4)
 */
public interface BatchWritingMechanism {

    /**
     * INTERNAL:
     * This method is called by the DatabaseAccessor to add this statement to the list of statements
     * being batched.  This call may result in the Mechanism executing the batched statements and
     * possibly, switching out the mechanisms
     */
    abstract public void appendCall(AbstractSession session, DatabaseCall call);

    /**
     * INTERNAL:
     * This method is used to clear the batched statements without the need to execute the statements first
     * This is used in the case of rollback.
     */
    abstract public void clear();

    /**
     * INTERNAL:
     * This method is used by the DatabaseAccessor to clear the batched statements in the
     * case that a non batchable statement is being execute
     */
    abstract public void executeBatchedStatements(AbstractSession session);

    /**
     * INTERNAL:
     * Sets the accessor that this mechanism will be used
     */
    abstract public void setAccessor(DatabaseAccessor accessor);
}
