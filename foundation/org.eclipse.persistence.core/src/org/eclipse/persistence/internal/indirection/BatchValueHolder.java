/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
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
    protected transient AbstractRecord argumentRow;

    /**
     * Initialize the query-based value holder.
     * @param query The query that returns the object when executed.
     * @param row The row representation of the object.
     * @param mapping The mapping that is uses batch reading.
     */
    public BatchValueHolder(ReadQuery query, AbstractRecord row, ForeignReferenceMapping mapping, DatabaseQuery originalQuery) {
        super(query, row, originalQuery.getSession());
        this.mapping = mapping;
        this.argumentRow = originalQuery.getTranslationRow();
    }

    protected AbstractRecord getArgumentRow() {
        return argumentRow;
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
        return getMapping().extractResultFromBatchQuery(getQuery(), getRow(), session, getArgumentRow());
    }

    /**
     * INTERNAL:
     * Answers if this valueholder is easy to instantiate.
     * @return true if getValue() won't trigger a database read.
     */
    public boolean isEasilyInstantiated() {
        return isInstantiated() || (getQuery().getProperty("batched objects") != null);
    }

    /**
     * Reset all the fields that are not needed after instantiation.
     */
    protected void resetFields() {
        super.resetFields();
        setArgumentRow(null);
        setMapping(null);
    }

    protected void setArgumentRow(AbstractRecord argumentRow) {
        this.argumentRow = argumentRow;
    }

    protected void setMapping(ForeignReferenceMapping mapping) {
        this.mapping = mapping;
    }
}
