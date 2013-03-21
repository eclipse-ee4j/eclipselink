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

import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p><b>Purpose</b>:
 * Used for updating existing objects into the database.
 * This class does not have much behavior.
 * It inherits most of it's behavior from WriteObjectQuery
 *
 * @author Yvon Lavoie
 * @since TOPLink/Java 1.0
 */
public class UpdateObjectQuery extends WriteObjectQuery {

    /**
     * PUBLIC:
     * Default constructor.
     */
    public UpdateObjectQuery() {
        super();
    }

    /**
     * PUBLIC:
     * Create an update query with the object being updated.
     */
    public UpdateObjectQuery(Object objectToUpdate) {
        this();
        setObject(objectToUpdate);
    }

    /**
     * PUBLIC:
     * Create an update query with the custom call.
     */
    public UpdateObjectQuery(Call call) {
        this();
        setCall(call);
    }

    /**
     * INTERNAL:
     * Perform an update.
     */
    public void executeCommit() throws DatabaseException, OptimisticLockException {
        // Check for redirection.
        QueryRedirector localRedirector = getRedirectorForQuery();
        // refactored redirection for bug 3241138
        if ( localRedirector!= null) {
            redirectQuery(localRedirector, this, session, translationRow);
            return;
        }
        getQueryMechanism().updateObjectForWrite();
    }
    
    /**
     * INTERNAL:
     * Perform an update.
     */
    public void executeCommitWithChangeSet() throws DatabaseException, OptimisticLockException {
        // Check for redirection.
        QueryRedirector localRedirector = getRedirectorForQuery();
        // refactored redirection for bug 3241138
        if ( localRedirector!= null) {
            redirectQuery(localRedirector, this, session, translationRow);
            return;
        }
        getQueryMechanism().updateObjectForWriteWithChangeSet();
    }

    /**
     * INTERNAL:
     * Prepare the receiver for execution in a session.
     */
    protected void prepare() {
        super.prepare();
        if (this.name == null) {
            this.name = "update" + this.descriptor.getJavaClass().getSimpleName();
        }
        getQueryMechanism().prepareUpdateObject();
    }
    
    /**
     * INTERNAL:
     * Check to see if a custom query should be used for this query.
     * This is done before the query is copied and prepared/executed.
     * null means there is none.
     */
    protected DatabaseQuery checkForCustomQuery(AbstractSession session, AbstractRecord translationRow) {
        checkDescriptor(session);

        // check if user defined a custom query
        DescriptorQueryManager queryManager = this.descriptor.getQueryManager();
        if ((!isCallQuery())// this is not a hand-coded (custom SQL, SDK etc.) call
                 && (!isUserDefined())// and this is not a user-defined query (in the query manager)
                 && queryManager.hasUpdateQuery()) {// and there is a user-defined query (in the query manager)
            return queryManager.getUpdateQuery();
        }

        return null;
    }
    
    /**
     * INTERNAL:
     * Set the properties needed to be cascaded into the custom query.
     */
    protected void prepareCustomQuery(DatabaseQuery customQuery) {
        UpdateObjectQuery customUpdateQuery = (UpdateObjectQuery)customQuery;        
        customUpdateQuery.setObject(getObject());
        customUpdateQuery.setObjectChangeSet(getObjectChangeSet());
        customUpdateQuery.setCascadePolicy(getCascadePolicy());
        customUpdateQuery.setShouldMaintainCache(shouldMaintainCache());
        customUpdateQuery.setModifyRow(null);
    }

    /**
     * INTERNAL:
     * Returns the specific default redirector for this query type.  There are numerous default query redirectors.
     * See ClassDescriptor for their types.
     */
    protected QueryRedirector getDefaultRedirector(){
        return descriptor.getDefaultUpdateObjectQueryRedirector();
    }

    /**
     * PUBLIC:
     * Return if this is an update object query.
     */
    public boolean isUpdateObjectQuery() {
        return true;
    }
}
