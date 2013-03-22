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


/**
 * <p><b>Purpose</b>:
 * Provide a means of controlling the behavior of in-memory and conforming queries
 * that access un-instantiated indirection objects in processing the query against cached objects.
 *
 * <p><b>Description</b>:
 * This class defines the valid constant values for handling in-memory querying.
 * The constants should be set into the query's inMemoryQueryIndirectionPolicy.
 *
 * @author Gordon Yorke
 * @since TopLink/Java 3.6.3
 */
public class InMemoryQueryIndirectionPolicy implements java.io.Serializable {
    /** If a non-instantiated indirection object is encountered an exception is thrown. */
    public static final int SHOULD_THROW_INDIRECTION_EXCEPTION = 0;
    /** If a non-instantiated indirection object is encountered an exception it is instantiated.  Caution should be used in using this option. */
    public static final int SHOULD_TRIGGER_INDIRECTION = 1;
    /** If a non-instantiated indirection object is encountered the object is assumed to conform. */
    public static final int SHOULD_IGNORE_EXCEPTION_RETURN_CONFORMED = 2;
    /** If a non-instantiated indirection object is encountered the object is assumed to not conform. */
    public static final int SHOULD_IGNORE_EXCEPTION_RETURN_NOT_CONFORMED = 3;
    
    protected int policy;
    /** Used to set the policy state in the query. */
    protected ObjectLevelReadQuery query;
    
    public InMemoryQueryIndirectionPolicy() {
        this.policy = SHOULD_THROW_INDIRECTION_EXCEPTION;
    }

    public InMemoryQueryIndirectionPolicy(int policyValue) {
        this.policy = policyValue;
    }
    
    public InMemoryQueryIndirectionPolicy(int policy, ObjectLevelReadQuery query) {
        this.policy = policy;
        this.query = query;
    }

    public boolean shouldTriggerIndirection() {
        return this.policy == SHOULD_TRIGGER_INDIRECTION;
    }

    public boolean shouldThrowIndirectionException() {
        return this.policy == SHOULD_THROW_INDIRECTION_EXCEPTION;
    }

    public boolean shouldIgnoreIndirectionExceptionReturnConformed() {
        return this.policy == SHOULD_IGNORE_EXCEPTION_RETURN_CONFORMED;
    }

    public boolean shouldIgnoreIndirectionExceptionReturnNotConformed() {
        return this.policy == SHOULD_IGNORE_EXCEPTION_RETURN_NOT_CONFORMED;
    }

    public void ignoreIndirectionExceptionReturnNotConformed() {
        setPolicy(SHOULD_IGNORE_EXCEPTION_RETURN_NOT_CONFORMED);
    }

    public void ignoreIndirectionExceptionReturnConformed() {
        setPolicy(SHOULD_IGNORE_EXCEPTION_RETURN_CONFORMED);
    }

    public void triggerIndirection() {
        setPolicy(SHOULD_TRIGGER_INDIRECTION);
    }

    public void throwIndirectionException() {
        setPolicy(SHOULD_THROW_INDIRECTION_EXCEPTION);
    }

    public int getPolicy() {
        return this.policy;
    }

    public void setPolicy(int policy) {
        this.policy = policy;
        if (this.query != null) {
            this.query.setInMemoryQueryIndirectionPolicyState(policy);
        }
    }
    
    /**
     * INTERNAL:
     * Return the query.
     */
    public ObjectLevelReadQuery getQuery() {
        return query;
    }
    
    /**
     * INTERNAL:
     * Set the query.
     */
    public void setQuery(ObjectLevelReadQuery query) {
        this.query = query;
    }
}
