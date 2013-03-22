/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.queries;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.expressions.QueryKeyExpression;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * BatchFetchPolicy defines batch reading configuration.
 *
 * @see org.eclipse.persistence.queries.ObjectLevelReadQuery#setBatchFetchPolicy(BatchFetchPolicy)
 * @author James Sutherland
 */
public class BatchFetchPolicy implements Serializable, Cloneable {
    /** Define the type of batch fetching to use. */
    protected BatchFetchType type;
    /** Define the batch size for IN style batch fetching. */
    protected int size = 500;
    /** Define the attributes to be batch fetched. */
    protected List<Expression> attributeExpressions;
    /** Define the mapping to be batch fetched. */
    protected List<DatabaseMapping> batchedMappings;
    /** PERF: Used internally to store the prepared mapping queries. */
    protected transient Map<DatabaseMapping, ReadQuery> mappingQueries;
    /** PERF: Cache the local batch read attribute names. */
    protected List<String> attributes;
    /** Stores temporary list of rows from parent batch query per batched mapping. */
    protected transient Map<Object, List<AbstractRecord>> dataResults;
    /** Stores temporary map of batched objects (this queries results). */
    protected transient Map<Object, Object> batchObjects;

    public BatchFetchPolicy() {
        this.type = BatchFetchType.JOIN;
    }
    
    public BatchFetchPolicy(BatchFetchType type) {
        this.type = type;
    }
    
    public BatchFetchPolicy clone() {
        BatchFetchPolicy clone = null;
        try {
            clone = (BatchFetchPolicy)super.clone();
        } catch (CloneNotSupportedException error) {
            throw new InternalError(error.getMessage());
        }
        if (clone.dataResults != null) {
            clone.dataResults.put(clone, clone.dataResults.get(this));
        }
        return clone;
    }
    
    /**
     * Return if using the IN fetch type.
     */
    public boolean isIN() {
        return this.type == BatchFetchType.IN;
    }
    
    /**
     * Return if using the JOIN fetch type.
     */
    public boolean isJOIN() {
        return this.type == BatchFetchType.JOIN;
    }
    
    /**
     * Return if using the EXISTS fetch type.
     */
    public boolean isEXISTS() {
        return this.type == BatchFetchType.EXISTS;
    }
    
    /**
     * Return the batch fetch type, (JOIN, IN, EXISTS).
     */
    public BatchFetchType getType() {
        return type;
    }

    /**
     * Set the batch fetch type, (JOIN, IN, EXISTS).
     */
    public void setType(BatchFetchType type) {
        this.type = type;
    }

    /**
     * Return the batch fetch size.
     */
    public int getSize() {
        return size;
    }

    /**
     * Set the batch fetch size.
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * PERF: Return the internally stored prepared mapping queries.
     */
    public Map<DatabaseMapping, ReadQuery> getMappingQueries() {
        return mappingQueries;
    }

    /**
     * PERF: Set the internally stored prepared mapping queries.
     */
    public void setMappingQueries(Map<DatabaseMapping, ReadQuery> mappingQueries) {
        this.mappingQueries = mappingQueries;
    }

    /**
     * PERF: Return the cached local (only) batch read attribute names.
     */
    public List<String> getAttributes() {
        return attributes;
    }

    /**
     * PERF: Set the cached local (only) batch read attribute names.
     */
    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public void setAttributeExpressions(List<Expression> attributeExpressions) {
        this.attributeExpressions = attributeExpressions;
    }

    /**
     * INTERNAL:
     * Return all attributes specified for batch reading.
     */
    public List<Expression> getAttributeExpressions() {
        if (this.attributeExpressions == null) {
            this.attributeExpressions = new ArrayList<Expression>();
        }        
        return this.attributeExpressions;
    }

    /**
     * INTERNAL:
     * Return true is this query has batching
     */
    public boolean hasAttributes() {
        return (this.attributeExpressions != null) && (!this.attributeExpressions.isEmpty())
                    || (this.batchedMappings != null);
    }

    /**
     * INTERNAL:
     * Return any mappings that are always batched.
     */
    public List<DatabaseMapping> getBatchedMappings() {
        return batchedMappings;
    }

    /**
     * INTERNAL:
     * Set any mappings that are always batched.
     */
    public void setBatchedMappings(List<DatabaseMapping> batchedMappings) {
        this.batchedMappings = batchedMappings;
    }
    
    /**
     * INTERNAL:
     * Return if the attribute is specified for batch reading.
     */
    public boolean isAttributeBatchRead(String attributeName) {
        if (this.attributeExpressions == null) {
            return false;
        }
        List<Expression> batchReadAttributeExpressions = this.attributeExpressions;
        int size = batchReadAttributeExpressions.size();
        for (int index = 0; index < size; index++) {
            QueryKeyExpression expression = (QueryKeyExpression)batchReadAttributeExpressions.get(index);
            while (!expression.getBaseExpression().isExpressionBuilder()) {
                expression = (QueryKeyExpression)expression.getBaseExpression();
            }
            if (expression.getName().equals(attributeName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * INTERNAL:
     * Return if the attribute is specified for batch reading.
     */
    public boolean isAttributeBatchRead(ClassDescriptor mappingDescriptor, String attributeName) {
        if (this.attributeExpressions == null) {
            return false;
        }
        if (this.attributes != null) {
            return this.attributes.contains(attributeName);
        }
        return isAttributeBatchRead(attributeName);
    }

    /**
     * INTERNAL:
     * Add the row to the set of data results.
     * This is used for IN batching in batches.
     */
    public void addDataResults(AbstractRecord row) {
        if (this.dataResults == null) {
            this.dataResults = new HashMap<Object, List<AbstractRecord>>();
            this.dataResults.put(this, new ArrayList<AbstractRecord>());
        }
        for (List<AbstractRecord> results : this.dataResults.values()) {
            results.add(row);
        }
    }

    /**
     * INTERNAL:
     * Return the remaining data results for the mapping.
     * This is used for IN batching in batches.
     */
    public List<AbstractRecord> getDataResults(DatabaseMapping mapping) {
        List<AbstractRecord> result = this.dataResults.get(mapping);
        if (result == null) {
            result = this.dataResults.get(this);
            this.dataResults.put(mapping, result);
        }
        return result;
    }

    /**
     * INTERNAL:
     * Set the remaining data results for the mapping.
     * This is used for IN batching in batches.
     */
    public void setDataResults(DatabaseMapping mapping, List<AbstractRecord> rows) {
        this.dataResults.put(mapping, rows);
    }

    /**
     * INTERNAL:
     * Set the rows to the set of data results for each mapping.
     * This is used for IN batching in batches.
     */
    public void setDataResults(List<AbstractRecord> rows) {
        this.dataResults = new HashMap<Object, List<AbstractRecord>>();
        this.dataResults.put(this, rows);
    }
    
    /**
     * INTERNAL:
     * Return temporary list of rows from parent batch query per batched mapping.
     * This is used for IN batching in batches.
     */
    public Map<Object, List<AbstractRecord>> getDataResults() {
        return this.dataResults;
    }

    /**
     * INTERNAL:
     * Set temporary list of rows from parent batch query per batched mapping.
     * This is used for IN batching in batches.
     */
    public void setDataResults(Map<Object, List<AbstractRecord>> dataResults) {
        this.dataResults = dataResults;
    }

    /**
     * INTERNAL:
     * Return temporary map of batched objects.
     */
    public Map<Object, Object> getBatchObjects() {
        return batchObjects;
    }

    /**
     * INTERNAL:
     * Set temporary map of batched objects.
     */
    public void setBatchObjects(Map<Object, Object> batchObjects) {
        this.batchObjects = batchObjects;
    }
}
