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

import java.util.*;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.changetracking.CollectionChangeEvent;
import org.eclipse.persistence.exceptions.*;

/**
 * <p><b>Purpose</b>:
 * Abstract class for all Cursor-related policy objects
 */
public abstract class CursorPolicy extends ContainerPolicy {
    protected int pageSize;
    protected ReadQuery query;

    /**
     * INTERNAL:
     * default constructor
     */
    public CursorPolicy() {
        super();
        setPageSize(10);
    }

    /**
     * INTERNAL:
     */
    public CursorPolicy(ReadQuery query, int pageSize) {
        super();
        setQuery(query);
        setPageSize(pageSize);
    }

    /**
     * INTERNAL:
     */
    public ContainerPolicy clone(ReadQuery query) {
        CursorPolicy clone = (CursorPolicy)super.clone(query);
        clone.setQuery(query);

        return clone;
    }
    
    /**
     * INTERNAL:
     * Creates a CollectionChangeEvent for the container
     */
    public CollectionChangeEvent createChangeEvent(Object collectionOwner, String propertyName, Object collectionChanged, Object elementChanged, int changeType, Integer index, boolean isChangeApplied){
        throw ValidationException.operationNotSupported("createChangeEvent");
    }

    /**
     * INTERNAL:
     * Execute the cursored select and build the stream.
     */
    public abstract Object execute();

    /**
     * Return the number of elements to be read into a cursored stream
     * when more elements are needed from the database.
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * INTERNAL:
     * Return the query.
     */
    public ReadQuery getQuery() {
        return query;
    }

    /**
     * INTERNAL:
     * Return whether the iterator has more objects.
     *
     * @param iterator java.lang.Object
     * @return boolean true if iterator has more objects
     */
    public boolean hasNext(Object iterator) {
        return ((Enumeration)iterator).hasMoreElements();
    }

    public boolean isCursorPolicy() {
        return true;
    }

    /**
     * INTERNAL:
     * Return an iterator for the given container.
     * This iterator can then be used as a parameter to #hasNext()
     * and #next().
     *
     * @see ContainerPolicy#hasNext(java.lang.Object)
     * @see ContainerPolicy#next(java.lang.Object)
     */
    public Object iteratorFor(Object container) {
        return container;
    }

    /**
     * INTERNAL:
     * Return the next object on the queue. The iterator is the one
     * returned from #iteratorFor().
     *
     * @see ContainerPolicy#iteratorFor(java.lang.Object)
     */
    protected Object next(Object iterator) {
        return ((Enumeration)iterator).nextElement();
    }

    /**
     * INTERNAL:
     * This can be used by collection such as cursored stream to gain control over execution.
     */
    public boolean overridesRead() {
        return true;
    }

    /**
     * INTERNAL:
     * Prepare and validate.
     */
    public void prepare(DatabaseQuery query, AbstractSession session) throws QueryException {
        super.prepare(query, session);
        setQuery((ReadQuery)query);
        
        ClassDescriptor descriptor = query.getDescriptor();

        // ReadAllQuery has a descriptor, DataReadQuery does not.
        if (descriptor != null) {
            // Interface queries cannot use cursors.
            if (descriptor.isDescriptorForInterface()) {
                throw QueryException.noCursorSupport(query);
            }
            // Ensure inheritance queries outer join subclasses.
            if (query.isObjectLevelReadQuery() && descriptor.hasInheritance()) {
                ((ObjectLevelReadQuery) query).setShouldOuterJoinSubclasses(true);;
            }
        }
        query.getQueryMechanism().prepareCursorSelectAllRows();
    }

    /**
     * INTERNAL:
     * Execute the cursored select and build the stream.
     */
    public abstract Object remoteExecute();

    /**
     * Set the number of elements to be read into a cursored stream
     * when more elements are needed from the database.
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * INTERNAL:
     * Set the query.
     */
    public void setQuery(ReadQuery query) {
        this.query = query;
    }

    /**
     * INTERNAL:
     * Return the size of container.
     */
    public int sizeFor(Object container) {
        return ((Cursor)container).size();
    }

    protected Object toStringInfo() {
        return "page size = " + getPageSize();
    }
}
