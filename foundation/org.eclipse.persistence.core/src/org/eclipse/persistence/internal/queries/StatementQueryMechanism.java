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
package org.eclipse.persistence.internal.queries;

import java.util.*;
import org.eclipse.persistence.internal.expressions.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.SessionProfiler;
/**
 * <p><b>Purpose</b>:
 * Mechanism used for all  statement objects.
 * <p>
 * <p><b>Responsibilities</b>:
 * Executes the appropriate statement.
 *
 * @author Yvon Lavoie
 * @since TOPLink/Java 1.0
 */
public class StatementQueryMechanism extends CallQueryMechanism {
    protected SQLStatement sqlStatement;

    /** Normally only a single statement is used, however multiple table may require multiple statements on write. */
    protected Vector sqlStatements;

    public StatementQueryMechanism() {
    }
    
    /**
     * INTERNAL:
     * Return a new mechanism for the query
     * @param query - owner of mechanism
     */
    public StatementQueryMechanism(DatabaseQuery query) {
        super(query);
    }

    /**
     * Return a new mechanism for the query
     * @param query - owner of mechanism
     * @param statement - sql statement
     */
    public StatementQueryMechanism(DatabaseQuery query, SQLStatement statement) {
        super(query);
        this.sqlStatement = statement;
    }

    /**
     * The statement is no longer require after prepare so can be released.
     */
    public void clearStatement() {
        // Only clear the statement if it is an expression query, otherwise the statement may still be needed.
    }

    /**
     * Clone the mechanism for the specified query clone.
     */
    public DatabaseQueryMechanism clone(DatabaseQuery queryClone) {
        StatementQueryMechanism clone = (StatementQueryMechanism)super.clone(queryClone);
        if ((!hasMultipleStatements()) && (getSQLStatement() != null)) {
            clone.setSQLStatement((SQLStatement)sqlStatement.clone());
        } else {
            Vector currentStatements = getSQLStatements();
            if (currentStatements != null) {
                Vector statementClone = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(currentStatements.size());
                Enumeration enumtr = currentStatements.elements();
                while (enumtr.hasMoreElements()) {
                    statementClone.addElement(((SQLStatement)enumtr.nextElement()).clone());
                }
                clone.setSQLStatements(statementClone);
            }
        }
        return clone;
    }

    /**
     * INTERNAL:
     * delete the object
     * @exception  DatabaseException - an error has occurred on the database.
     * @return the row count.
     */
    public Integer deleteObject() throws DatabaseException {
        // Prepare the calls if not already set (prepare may not have had the modify row).
        if ((this.call == null) && (!hasMultipleCalls())) {
            prepareDeleteObject();
            if ((this.call == null) && (!hasMultipleCalls())) {
                return Integer.valueOf(1);// Must be 1 otherwise locking error will occur.
            }
        }

        return super.deleteObject();
    }

    /**
     * Update the object
     * @exception  DatabaseException - an error has occurred on the database.
     * @return the row count.
     */
    public Integer executeNoSelect() throws DatabaseException {
        // Prepare the calls if not already set (prepare may not have had the modify row).
        if ((this.call == null) && (!hasMultipleCalls())) {
            prepareExecuteNoSelect();
        }

        return super.executeNoSelect();
    }

    /**
     * Return the selection criteria for the statement.
     */
    public Expression getSelectionCriteria() {
        return getSQLStatement().getWhereClause();
    }

    /**
     * INTERNAL:
     * Return the sqlStatement
     */
    public SQLStatement getSQLStatement() {
        return sqlStatement;
    }

    /**
     * Normally only a single statement is used, however multiple table may require multiple statements on write.
     * This is lazy initialied to conserv space.
     */
    public Vector getSQLStatements() {
        if (sqlStatements == null) {
            sqlStatements = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(3);
        }
        return sqlStatements;
    }

    /**
     * Normally only a single statement is used, however multiple table may require multiple statements on write.
     * This is lazy initialized to conserve space.
     */
    public boolean hasMultipleStatements() {
        return (sqlStatements != null) && (!sqlStatements.isEmpty());
    }

    /**
     * Insert the object
     * @exception  DatabaseException - an error has occurred on the database.
     */
    public void insertObject() throws DatabaseException {
        // Prepare the calls if not already set (prepare may not have had the modify row).
        if ((this.call == null) && (!hasMultipleCalls())) {
            prepareInsertObject();
        }

        super.insertObject();
    }

    /**
     * Insert the object if the reprepare flag is set, first reprepare the query.
     * Added for CR#3237
     * @param boolean reprepare - whether to reprepare the query.
     */
    public void insertObject(boolean reprepare) {
        if (reprepare) {
            // Clear old calls, and reprepare. 
            setCalls(null);
            trimFieldsForInsert();
            prepareInsertObject();
        }
        insertObject();
    }

    /**
     * INTERNAL
     * Remove a potential sequence number field and invoke the ReturningPolicy trimModifyRowsForInsert method
     */
    public void trimFieldsForInsert() {
        getDescriptor().getObjectBuilder().trimFieldsForInsert(getSession(), getModifyRow());
    }
    
    /**
     * Return true if this is a call query mechanism
     */
    public boolean isCallQueryMechanism() {
        return false;
    }

    /**
     * Return true if this is a statement query mechanism
     */
    public boolean isStatementQueryMechanism() {
        return true;
    }

    /**
     * INTERNAL:
     * This is different from 'prepareForExecution' in that this is called on the original query,
     * and the other is called on the copy of the query.
     * This query is copied for concurrency so this prepare can only setup things that
     * will apply to any future execution of this query.
     */
    public void prepare() {
        if ((!hasMultipleStatements()) && (getSQLStatement() == null)) {
            throw QueryException.sqlStatementNotSetProperly(getQuery());
        }

        // Cannot call super yet as the call is not built.
    }

    /**
     * Pre-build the SQL call from the statement.
     */
    public void prepareCursorSelectAllRows() {
        setCallFromStatement();
        // The statement is no longer require so can be released.
        clearStatement();

        super.prepareCursorSelectAllRows();
    }

    /**
     * Pre-build the SQL call from the statement.
     */
    public void prepareDeleteAll() {
        setCallFromStatement();
        // The statement is no longer require so can be released.
        clearStatement();

        super.prepareDeleteAll();
    }

    /**
     * Pre-build the SQL call from the statement.
     */
    public void prepareDeleteObject() {
        setCallFromStatement();
        // The statement is no longer require so can be released.
        clearStatement();

        super.prepareDeleteObject();
    }

    /**
     * Pre-build the SQL call from the statement.
     */
    public void prepareDoesExist(DatabaseField field) {
        setCallFromStatement();
        // The statement is no longer require so can be released.
        clearStatement();

        getCall().returnOneRow();
        prepareCall();
    }

    /**
     * Pre-build the SQL call from the statement.
     */
    public void prepareExecuteNoSelect() {
        setCallFromStatement();
        // The statement is no longer require so can be released.
        clearStatement();

        super.prepareExecuteNoSelect();
    }

    /**
     * Pre-build the SQL call from the statement.
     */
    public void prepareExecuteSelect() {
        setCallFromStatement();
        // The statement is no longer require so can be released.
        clearStatement();

        super.prepareExecuteSelect();
    }

    /**
     * Pre-build the SQL call from the statement.
     */
    public void prepareInsertObject() {
        // Require modify row to prepare.
        if (getModifyRow() == null) {
            return;
        }

        if (hasMultipleStatements()) {
            for (Enumeration statementEnum = getSQLStatements().elements();
                     statementEnum.hasMoreElements();) {
                ((SQLModifyStatement)statementEnum.nextElement()).setModifyRow(getModifyRow());
            }
        } else if (getSQLStatement() != null) {
            ((SQLModifyStatement)getSQLStatement()).setModifyRow(getModifyRow());
        }
        setCallFromStatement();
        // The statement is no longer require so can be released.
        clearStatement();

        super.prepareInsertObject();
    }

    /**
     * Pre-build the SQL call from the statement.
     */
    public void prepareSelectAllRows() {
        setCallFromStatement();
        // The statement is no longer require so can be released.
        clearStatement();

        super.prepareSelectAllRows();
    }

    /**
     * Pre-build the SQL call from the statement.
     */
    public void prepareSelectOneRow() {
        setCallFromStatement();
        // The statement is no longer require so can be released.
        clearStatement();

        super.prepareSelectOneRow();
    }

    /**
     * Pre-build the SQL call from the statement.
     */
    public void prepareUpdateObject() {
        // Require modify row to prepare.
        if (getModifyRow() == null) {
            return;
        }

        if (hasMultipleStatements()) {
            for (Enumeration statementEnum = getSQLStatements().elements();
                     statementEnum.hasMoreElements();) {
                ((SQLModifyStatement)statementEnum.nextElement()).setModifyRow(getModifyRow());
            }
        } else if (getSQLStatement() != null) {
            ((SQLModifyStatement)getSQLStatement()).setModifyRow(getModifyRow());
        }
        setCallFromStatement();
        // The statement is no longer require so can be released.
        clearStatement();

        super.prepareUpdateObject();
    }

    /**
     * Pre-build the SQL call from the statement.
     */
    public void prepareUpdateAll() {
        setCallFromStatement();// Will build an SQLUpdateAllStatement
        clearStatement();// The statement is no longer require so can be released.
        super.prepareUpdateAll();
    }

    /**
     * Pre-build the SQL call from the statement.
     */
    protected void setCallFromStatement() {
        // Profile SQL generation.
        getSession().startOperationProfile(SessionProfiler.SqlGeneration, getQuery(), SessionProfiler.ALL);
        try {
            if (hasMultipleStatements()) {
                for (Enumeration statementEnum = getSQLStatements().elements(); statementEnum.hasMoreElements();) {
                    DatasourceCall call = null;
                    if (getDescriptor() != null) {
                        call = getDescriptor().buildCallFromStatement((SQLStatement)statementEnum.nextElement(), getQuery(), getExecutionSession());
                    } else {
                        call = ((SQLStatement)statementEnum.nextElement()).buildCall(getExecutionSession());
                    }
    
                    // In case of update call may be null if no update required.
                    if (call != null) {
                        addCall(call);
                    }
                }
            } else {
                DatasourceCall call = null;
                if (getDescriptor() != null) {
                    call = getDescriptor().buildCallFromStatement(getSQLStatement(), getQuery(), getExecutionSession());
                } else {
                    call = getSQLStatement().buildCall(getExecutionSession());
                }
    
                // In case of update call may be null if no update required.
                if (call != null) {
                    setCall(call);
                }
            }
        } finally {
            // Profile SQL generation.
            getSession().endOperationProfile(SessionProfiler.SqlGeneration, getQuery(), SessionProfiler.ALL);
        }
    }

    /**
     * Set the sqlStatement
     */
    public void setSQLStatement(SQLStatement statement) {
        this.sqlStatement = statement;
    }

    /**
     * Normally only a single statement is used, however multiple table may require multiple statements on write.
     * This is lazy initialized to conserve space.
     */
    protected void setSQLStatements(Vector sqlStatements) {
        this.sqlStatements = sqlStatements;
    }

    /**
     * Update the object
     * @exception  DatabaseException - an error has occurred on the database.
     * @return the row count.
     */
    public Integer updateObject() throws DatabaseException {
        // Prepare the calls if not already set (prepare may not have had the modify row).
        if ((this.call == null) && (!hasMultipleCalls())) {
            prepareUpdateObject();
            if ((this.call == null) && (!hasMultipleCalls())) {
                return Integer.valueOf(1);// Must be 1 otherwise locking error will occur.
            }
        }

        return super.updateObject();
    }
}
