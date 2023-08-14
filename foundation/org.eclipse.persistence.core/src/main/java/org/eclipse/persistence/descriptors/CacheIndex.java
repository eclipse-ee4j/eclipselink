/*
 * Copyright (c) 2011, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.descriptors;

import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.identitymaps.WeakIdentityMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    protected Class<? extends WeakIdentityMap> cacheType = ClassConstants.WeakIdentityMap_Class;

    public CacheIndex() {
        this.fields = new ArrayList<>();
    }

    public CacheIndex(DatabaseField[] fields) {
        this.fields = new ArrayList<>(fields.length);
        Collections.addAll(this.fields, fields);
    }

    public CacheIndex(String... fields) {
        this.fields = new ArrayList<>(fields.length);
        for (String field : fields) {
            this.fields.add(new DatabaseField(field));
        }
    }

    public CacheIndex(List<DatabaseField> fields) {
        this.fields = fields;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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
    @SuppressWarnings({"unchecked"})
    public <T extends WeakIdentityMap> Class<T> getCacheType() {
        return (Class<T>) cacheType;
    }

    /**
     * ADVANCED:
     * Set the type of the cache used for the index.
     * This default to a weak cache, and should normally not be changed.
     * For a weak cache, the index will remain until the object gcs from the main cache.
     */
    public void setCacheType(Class<? extends WeakIdentityMap> cacheType) {
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

    @Override
    public String toString() {
        return "CacheIndex(" + getFields() + ")";
    }
}
