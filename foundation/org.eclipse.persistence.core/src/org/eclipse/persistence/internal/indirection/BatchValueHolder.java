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
package org.eclipse.persistence.internal.indirection;

import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.exceptions.*;

/**
 * BatchValueHolder is used by the mappings that use indirection when using query optimization (batch reading).
 * This value holder is different from QueryBasedValueHolder in that its value must be extracted from one of the
 * results return by the query, not the entire result.
 * The query is also shared by all other value holders within the batch and it must be ensured that the query is only
 * executed once.  Concurrency must also be maintained across all of the value holders in the batch.
 */
public class BatchValueHolder extends QueryBasedValueHolder {
    protected transient ForeignReferenceMapping mapping;
    protected transient ObjectLevelReadQuery originalQuery;
    protected transient CacheKey parentCacheKey;

    /**
     * Initialize the query-based value holder.
     * @param query The query that returns the object when executed.
     * @param row The row representation of the object.
     * @param mapping The mapping that is uses batch reading.
     */
    public BatchValueHolder(ReadQuery query, AbstractRecord row, ForeignReferenceMapping mapping, ObjectLevelReadQuery originalQuery, CacheKey parentCacheKey) {
        super(query, row, originalQuery.getSession());
        this.mapping = mapping;
        this.originalQuery = originalQuery;
        this.parentCacheKey = parentCacheKey;
    }

    protected ForeignReferenceMapping getMapping() {
        return mapping;
    }

    /**
     * Instantiate the object by having the mapping extract its value from the query.
     * Concurrency must be maintained across all of the value holders,
     * since they all share the same query, the extractResultFromBatchQuery method must be synchronized.
     */
    protected Object instantiate(AbstractSession session) throws EclipseLinkException {
        return this.mapping.extractResultFromBatchQuery(this.query, this.parentCacheKey, this.row, session, this.originalQuery);
    }

    /**
     * Triggers UnitOfWork valueholders directly without triggering the wrapped
     * valueholder (this).
     * <p>
     * When in transaction and/or for pessimistic locking the
     * UnitOfWorkValueHolder needs to be triggered directly without triggering
     * the wrapped valueholder. However only the wrapped valueholder knows how
     * to trigger the indirection, i.e. it may be a batchValueHolder, and it
     * stores all the info like the row and the query. Note: This method is not
     * thread-safe. It must be used in a synchronized manner.
     * The batch value holder must use a batch query relative to the unit of work,
     * as the batch is local to the unit of work.
     */
    public Object instantiateForUnitOfWorkValueHolder(UnitOfWorkValueHolder unitOfWorkValueHolder) {
        UnitOfWorkImpl unitOfWork = unitOfWorkValueHolder.getUnitOfWork();
        ReadQuery localQuery = unitOfWork.getBatchQueries().get(this.query);
        if (localQuery == null) {
            localQuery = (ReadQuery)this.query.clone();
            unitOfWork.getBatchQueries().put(this.query, localQuery);
        }
        return this.mapping.extractResultFromBatchQuery(localQuery, this.parentCacheKey, this.row, unitOfWorkValueHolder.getUnitOfWork(), this.originalQuery);
    }

    /**
     * INTERNAL:
     * Answers if this valueholder is easy to instantiate.
     * @return true if getValue() won't trigger a database read.
     */
    public boolean isEasilyInstantiated() {
        return this.isInstantiated;
    }

    /**
     * Reset all the fields that are not needed after instantiation.
     */
    protected void resetFields() {
        super.resetFields();
        this.mapping = null;
        this.originalQuery = null;
    }

    protected void setMapping(ForeignReferenceMapping mapping) {
        this.mapping = mapping;
    }
}
