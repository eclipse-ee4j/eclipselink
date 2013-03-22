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
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.descriptors;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;

/**
 * <p><b>Purpose</b>:
 * Define a secondary index on the cache.
 *
 * @see CachePolicy
 */
public class CacheIndex implements Cloneable, Serializable {
    protected boolean isUpdateable = true;
    protected boolean isInsertable = true;

    protected List<DatabaseField> fields;
    /** Allows the cache size to be set. */
    protected int cacheSize;

    /** Allows the identity map class type to be set. */
    protected Class cacheType = ClassConstants.WeakIdentityMap_Class;

    public CacheIndex() {
        this.fields = new ArrayList<DatabaseField>();
    }
    
    public CacheIndex(DatabaseField fields[]) {
        this.fields = new ArrayList<DatabaseField>(fields.length);
        for (DatabaseField field : fields) {
            this.fields.add(field);
        }
    }
    
    public CacheIndex(String... fields) {
        this.fields = new ArrayList<DatabaseField>(fields.length);
        for (String field : fields) {
            this.fields.add(new DatabaseField(field));
        }
    }
    
    public CacheIndex(List<DatabaseField> fields) {
        this.fields = fields;
    }
    
    /**
     * Return if the index field can be updated.
     */
    public boolean isUpdateable() {
        return isUpdateable;
    }
    
    /**
     * Set if the index field can be updated.
     * If updateable the object will be re-indexed on each update/refresh.
     */
    public void setIsUpdateable(boolean isUpdateable) {
        this.isUpdateable = isUpdateable;
    }
    
    /**
     * Return if the index field can be inserted.
     */
    public boolean isInsertable() {
        return isInsertable;
    }
    
    /**
     * Set if the index field can be inserted.
     * If insertable the object will be indexed after insert.
     */
    public void setIsInsertable(boolean isInsertable) {
        this.isInsertable = isInsertable;
    }

    /**
     * ADVANCED:
     * Return the type of the cache used for the index.
     * This default to a weak cache, and should normally not be changed.
     * For a weak cache, the index will remain until the object gcs from the main cache.
     */
    public Class getCacheType() {
        return cacheType;
    }

    /**
     * ADVANCED:
     * Set the type of the cache used for the index.
     * This default to a weak cache, and should normally not be changed.
     * For a weak cache, the index will remain until the object gcs from the main cache.
     */
    public void setCacheType(Class cacheType) {
        this.cacheType = cacheType;
    }

    /**
     * ADVANCED:
     * Return the cache size.
     * This is either the initial size, sub-cache size, or fixed size depending on the cache type.
     */
    public int getCacheSize() {
        return cacheSize;
    }

    /**
     * ADVANCED:
     * Set the cache size.
     * This is either the initial size, sub-cache size, or fixed size depending on the cache type.
     */
    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }
    
    public void addField(DatabaseField field) {
        this.fields.add(field);
    }
    
    /**
     * PUBLIC:
     * Add the database column name to the cache index.
     */
    public void addFieldName(String field) {
        addField(new DatabaseField(field));
    }
    
    public List<DatabaseField> getFields() {
        return fields;
    }

    public void setFields(List<DatabaseField> fields) {
        this.fields = fields;
    }
    
    public String toString() {
        return "CacheIndex(" + getFields() + ")";
    }
}
