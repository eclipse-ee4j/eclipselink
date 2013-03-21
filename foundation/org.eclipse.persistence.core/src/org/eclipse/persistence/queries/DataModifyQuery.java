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
package org.eclipse.persistence.queries;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;

/**
 * <p><b>Purpose</b>:
 * Concrete class used for executing non selecting SQL strings.
 *
 * <p><b>Responsibilities</b>:
 * <ul>
 * <li> Execute a non selecting raw SQL string.
 * </ul>
 *
 * @author Yvon Lavoie
 * @since TOPLink/Java 1.0
 */
public class DataModifyQuery extends ModifyQuery {
    /** Used to distinguish query that have a different modify row than translation row. */
    protected boolean hasModifyRow;
    
    public DataModifyQuery() {
        super();
    }

    /** 
     * Warning: Allowing an unverified SQL string to be passed into this 
     * method makes your application vulnerable to SQL injection attacks. 
     */
    public DataModifyQuery(String sqlString) {
        this();

        setSQLString(sqlString);
    }

    public DataModifyQuery(Call call) {
        this();
        setCall(call);
    }

    /**
     * Return if a modify row has been set.
     * Allows distinguishing query that have a different modify row than translation row.
     */
    public boolean hasModifyRow() {
        return hasModifyRow;
    }

    /**
     * Set if a modify row has been set.
     * Allows distinguishing query that have a different modify row than translation row.
     */
    public void setHasModifyRow(boolean hasModifyRow) {
        this.hasModifyRow = hasModifyRow;
    }
    
    /**
     * INTERNAL:
     * Perform the work to execute the SQL call.
     * Return the row count of the number of rows effected by the SQL call.
     */
    public Object executeDatabaseQuery() throws DatabaseException {

        /* Fix to allow executing non-selecting SQL in a UnitOfWork. - RB */
        if (this.session.isUnitOfWork()) {
            UnitOfWorkImpl unitOfWork = (UnitOfWorkImpl)this.session;
            /* bug:4211104 for DataModifyQueries executed during an event, while transaction was started by the uow*/
            if (!unitOfWork.getCommitManager().isActive() && !unitOfWork.isInTransaction()) {
                unitOfWork.beginEarlyTransaction();
            }
            unitOfWork.setWasNonObjectLevelModifyQueryExecuted(true);
        }
        return getQueryMechanism().executeNoSelect();
    }

    /**
     * PUBLIC:
     * Return if this is a data modify query.
     */
    public boolean isDataModifyQuery() {
        return true;
    }

    /**
     * INTERNAL:
     * Prepare the receiver for execution in a session.
     */
    protected void prepare() {
        super.prepare();

        getQueryMechanism().prepareExecuteNoSelect();
    }

    /**
     * INTERNAL:
     * Prepare the receiver for execution in a session. In particular,
     * set the descriptor of the receiver to the ClassDescriptor for the
     * appropriate class for the receiver's object.
     */
    public void prepareForExecution() throws QueryException {
        super.prepareForExecution();

        // Only replace the modify row if unset.
        if (!this.hasModifyRow) {
            this.modifyRow = this.translationRow;
        }
    }
}
