/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.internal.nosql.adapters.mongo;

import javax.resource.cci.*;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;

/**
 * Interaction spec for Mongo JCA adapter.
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class MongoInteractionSpec implements InteractionSpec {
    protected MongoOperation operation;
    protected String collection;
    protected String code;

    /** Operation query options. */
    protected int options;
    
    /** Operation read preference. */
    protected ReadPreference readPreference;
    
    /** Operation write concern. */
    protected WriteConcern writeConcern;
    
    /** Operation skip for finds, number of rows to skip. */
    protected int skip;
    
    /** Operation limit for finds, number of rows to fetch. */
    protected int limit;

    /** Operation batchSize for finds, fetch size. */
    protected int batchSize;
    
    /** Operation upsert, to perform insert if document is missing. */
    protected boolean upsert;
    
    /** Operation multi, to perform update all matching documents. */
    protected boolean multi;
    
    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public MongoOperation getOperation() {
        return operation;
    }

    public void setOperation(MongoOperation operation) {
        this.operation = operation;
    }
    
    public int getOptions() {
        return options;
    }

    public void setOptions(int options) {
        this.options = options;
    }

    public ReadPreference getReadPreference() {
        return readPreference;
    }

    public void setReadPreference(ReadPreference readPreference) {
        this.readPreference = readPreference;
    }

    public WriteConcern getWriteConcern() {
        return writeConcern;
    }

    public void setWriteConcern(WriteConcern writeConcern) {
        this.writeConcern = writeConcern;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }
    
    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public boolean isUpsert() {
        return upsert;
    }

    public void setUpsert(boolean upsert) {
        this.upsert = upsert;
    }

    public boolean isMulti() {
        return multi;
    }

    public void setMulti(boolean multi) {
        this.multi = multi;
    }
    
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String toString() {
        return getClass().getSimpleName() + "(" + ")";
    }
}
