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
 *     ailitchev - Bug 244124 - New support for loading 
 ******************************************************************************/  
package org.eclipse.persistence.queries;

import org.eclipse.persistence.core.queries.CoreAttributeGroup;

/**
 * <b>Purpose</b>: Used to load specified relationship attributes and nested
 * relationship attributes.
 * <p>
 * A LoadGroup can be configured for use on a query using
 * {@link ObjectLevelReadQuery#setLoadGroup(LoadGroup)} or in the case of JPA
 * users with LOAD_GROUP query hint. Alternatively a {@link FetchGroup} could be
 * used with {@link FetchGroup#shouldLoad()} set to true and the FetchGroup
 * configured on a query be executed.
 * 
 * @see FetchGroup
 * 
 * @author ailitchev
 * @since Eclipselink 2.1
 */
public class LoadGroup extends AttributeGroup {

    protected Boolean isConcurrent;

    public LoadGroup() {
        super();
    }

    public LoadGroup(String name) {
        super(name);
    }

    @Override
    protected LoadGroup newGroup(String name, CoreAttributeGroup parent) {
        return new LoadGroup(name);
    }
    
    @Override
    public boolean isLoadGroup() {
        return true;
    }
    
    @Override
    public LoadGroup clone() {
        return (LoadGroup)super.clone();
    }

    /**
     * Returns LoadGroup corresponding to the passed (possibly nested) attribute.
     */
    @Override
    public LoadGroup getGroup(String attributeNameOrPath) {
        return (LoadGroup)super.getGroup(attributeNameOrPath);
    }

    @Override
    public void addAttribute(String attributeNameOrPath, CoreAttributeGroup group) {
        super.addAttribute(attributeNameOrPath, (group != null ? ((AttributeGroup)group).toLoadGroup() : null));
    }

    public void addAttribute(String attributeNameOrPath, LoadGroup group) {
        super.addAttribute(attributeNameOrPath, group);
    }
    
    /**
     * INTERNAL:
     * Return if the load group should load its relationships concurrently.
     */
    public Boolean getIsConcurrent() {
        return this.isConcurrent;
    }
    
    /**
     * ADVANCED:
     * Return if the load group should load its relationships concurrently.
     * This will use the session's server platform's thread pool.
     * Each of the query results objects relationships will be loaded on a different thread.
     * This can improve performance by processing many queries at once.
     * Concurrent load is only supported when a shared cache is used.
     */
    public boolean isConcurrent() {
        if (this.isConcurrent == null) {
            return false;
        }
        return this.isConcurrent.booleanValue();
    }
    
    /**
     * ADVANCED:
     * Set if the load group should load its relationships concurrently.
     * This will use the session's server platform's thread pool.
     * Each of the query results objects relationships will be loaded on a different thread.
     * This can improve performance by processing many queries at once.
     * Concurrent load is only supported when a shared cache is used.
     */
    public void setIsConcurrent(Boolean isConcurrent) {
        this.isConcurrent = isConcurrent;
    }
 }
