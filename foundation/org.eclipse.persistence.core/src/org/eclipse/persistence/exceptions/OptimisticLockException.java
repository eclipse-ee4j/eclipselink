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

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.exceptions.i18n.ExceptionMessageGenerator;
import org.eclipse.persistence.sessions.SessionProfiler;
import java.util.Vector;

/**
 * <p><b>Purpose</b>:  This exception is used when TopLink's optimistic locking feature is used.
 * It will be raised if the object being updated or deleted was changed or deleted from the database since
 * it as last read.
 */
public class OptimisticLockException extends EclipseLinkException {

    /** Store the query that raised the optimistic violation. */
    protected transient ObjectLevelModifyQuery query;

    // ERROR CODES
    public final static int NO_VERSION_NUMBER_WHEN_DELETING = 5001;
    public final static int OBJECT_CHANGED_SINCE_LAST_READ_WHEN_DELETING = 5003;
    public final static int NO_VERSION_NUMBER_WHEN_UPDATING = 5004;
    public final static int OBJECT_CHANGED_SINCE_LAST_READ_WHEN_UPDATING = 5006;
    public final static int MUST_HAVE_MAPPING_WHEN_IN_OBJECT = 5007;
    public final static int NEED_TO_MAP_JAVA_SQL_TIMESTAMP = 5008;
    public final static int UNWRAPPING_OBJECT_DELETED_SINCE_LAST_READ = 5009;
    public final static int OBJECT_CHANGED_SINCE_LAST_MERGE = 5010;
    public final static int STATEMENT_NOT_EXECUTED_IN_BATCH = 5011;

    /**
     * INTERNAL:
     * EclipseLink exceptions should only be thrown by EclipseLink.
     */
    protected OptimisticLockException() {        
    }
    
    /**
     * INTERNAL:
     * EclipseLink exceptions should only be thrown by EclipseLink.
     */
    protected OptimisticLockException(String theMessage) {
        super(theMessage);
    }

    /**
     * INTERNAL:
     * EclipseLink exceptions should only be thrown by EclipseLink.
     */
    protected OptimisticLockException(String theMessage, ObjectLevelModifyQuery query) {
        super(theMessage);
        this.query = query;
        query.getSession().incrementProfile(SessionProfiler.OptimisticLockException);

    }

    /**
     * PUBLIC:
     * Return the object for which the problem was detected.
     */
    public Object getObject() {
        return getQuery().getObject();
    }

    /**
     * PUBLIC:
     * Return the query in which the problem was detected.
     */
    public ObjectLevelModifyQuery getQuery() {
        return query;
    }
    
    public static OptimisticLockException batchStatementExecutionFailure(){
        Object[] args = { };

        OptimisticLockException optimisticLockException = new OptimisticLockException(ExceptionMessageGenerator.buildMessage(OptimisticLockException.class, STATEMENT_NOT_EXECUTED_IN_BATCH, args));
        optimisticLockException.setErrorCode(STATEMENT_NOT_EXECUTED_IN_BATCH);
        return optimisticLockException;
    
    }

    public static OptimisticLockException mustHaveMappingWhenStoredInObject(Class aClass) {
        Object[] args = { aClass };

        OptimisticLockException optimisticLockException = new OptimisticLockException(ExceptionMessageGenerator.buildMessage(OptimisticLockException.class, MUST_HAVE_MAPPING_WHEN_IN_OBJECT, args));
        optimisticLockException.setErrorCode(MUST_HAVE_MAPPING_WHEN_IN_OBJECT);
        return optimisticLockException;

    }

    public static OptimisticLockException noVersionNumberWhenDeleting(Object object, ObjectLevelModifyQuery query) {
        Object key = null;
        if (query.getSession() != null) {
            key = query.getSession().getId(object);
        }
        Object[] args = { object, object.getClass().getName(), key, CR };

        OptimisticLockException optimisticLockException = new OptimisticLockException(ExceptionMessageGenerator.buildMessage(OptimisticLockException.class, NO_VERSION_NUMBER_WHEN_DELETING, args), query);
        optimisticLockException.setErrorCode(NO_VERSION_NUMBER_WHEN_DELETING);
        return optimisticLockException;
    }

    public static OptimisticLockException noVersionNumberWhenUpdating(Object object, ObjectLevelModifyQuery query) {
        Object key = null;
        if (query.getSession() != null) {
            key = query.getSession().getId(object);
        }
        Object[] args = { object, object.getClass().getName(), key, CR };

        OptimisticLockException optimisticLockException = new OptimisticLockException(ExceptionMessageGenerator.buildMessage(OptimisticLockException.class, NO_VERSION_NUMBER_WHEN_UPDATING, args), query);
        optimisticLockException.setErrorCode(NO_VERSION_NUMBER_WHEN_UPDATING);
        return optimisticLockException;
    }

    public static OptimisticLockException objectChangedSinceLastReadWhenDeleting(Object object, ObjectLevelModifyQuery query) {
        Object key = null;
        if (query.getSession() != null) {
            key = query.getSession().getId(object);
        }
        Object[] args = { object, object.getClass().getName(), key, CR };

        OptimisticLockException optimisticLockException = new OptimisticLockException(ExceptionMessageGenerator.buildMessage(OptimisticLockException.class, OBJECT_CHANGED_SINCE_LAST_READ_WHEN_DELETING, args), query);
        optimisticLockException.setErrorCode(OBJECT_CHANGED_SINCE_LAST_READ_WHEN_DELETING);
        return optimisticLockException;
    }

    public static OptimisticLockException objectChangedSinceLastReadWhenUpdating(Object object, ObjectLevelModifyQuery query) {
        Object key = null;
        if (query.getSession() != null) {
            key = query.getSession().getId(object);
        }
        Object[] args = { object, object.getClass().getName(), key, CR };

        OptimisticLockException optimisticLockException = new OptimisticLockException(ExceptionMessageGenerator.buildMessage(OptimisticLockException.class, OBJECT_CHANGED_SINCE_LAST_READ_WHEN_UPDATING, args), query);
        optimisticLockException.setErrorCode(OBJECT_CHANGED_SINCE_LAST_READ_WHEN_UPDATING);
        return optimisticLockException;
    }
    
    public static OptimisticLockException objectChangedSinceLastMerge(Object object) {        
        Object[] args = { object, object.getClass().getName(), CR };

        OptimisticLockException optimisticLockException = new OptimisticLockException(ExceptionMessageGenerator.buildMessage(OptimisticLockException.class, OBJECT_CHANGED_SINCE_LAST_MERGE, args));
        optimisticLockException.setErrorCode(OBJECT_CHANGED_SINCE_LAST_MERGE);
        return optimisticLockException;
    }

    public static OptimisticLockException unwrappingObjectDeletedSinceLastRead(Vector pkVector, String className) {
        Object[] args = { pkVector, className };

        OptimisticLockException optimisticLockException = new OptimisticLockException(ExceptionMessageGenerator.buildMessage(OptimisticLockException.class, UNWRAPPING_OBJECT_DELETED_SINCE_LAST_READ, args));
        optimisticLockException.setErrorCode(UNWRAPPING_OBJECT_DELETED_SINCE_LAST_READ);
        return optimisticLockException;
    }

    //For CR#2281
    public static OptimisticLockException needToMapJavaSqlTimestampWhenStoredInObject() {
        Object[] args = {  };

        OptimisticLockException optimisticLockException = new OptimisticLockException(ExceptionMessageGenerator.buildMessage(OptimisticLockException.class, NEED_TO_MAP_JAVA_SQL_TIMESTAMP, args));
        optimisticLockException.setErrorCode(NEED_TO_MAP_JAVA_SQL_TIMESTAMP);
        return optimisticLockException;
    }

    /**
     * INTERNAL:
     * Set the query in which the problem was detected.
     */
    public void setQuery(ObjectLevelModifyQuery query) {
        this.query = query;
    }
}
