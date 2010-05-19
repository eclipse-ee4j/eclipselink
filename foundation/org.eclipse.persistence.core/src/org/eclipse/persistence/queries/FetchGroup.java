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
 *     05/19/2010-2.1 ailitchev - Bug 244124 - Add Nested FetchGroup 
 ******************************************************************************/  
package org.eclipse.persistence.queries;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.queries.AttributeGroup;
import org.eclipse.persistence.internal.queries.AttributeItem;

/**
 * <p><b>Purpose</b>: A fetch group is a performance enhancement that allows a group of
 * attributes of an object to be loaded on demand, which means that the data for an attribute
 * might not loaded from the underlying data source until an explicit access call for the
 * attribute first occurs. It avoids the wasteful practice of loading up all data of the object's
 * attributes, in which the user is interested in only partial of them.
 * <p>
 * A great deal of caution and careful system use case analysis should be use when using the fetch
 * group feature, as the extra round-trip would well offset the gain from the deferred loading in
 * many cases.
 * <p>
 * EclipseLink fetch group support is twofold: the pre-defined fetch groups at the descriptor level; and
 * dynamic (use case) fetch groups at the query level.
 * <p>
 * Every query can has at most one fetch group. There is an optional pre-defined default fetch group
 * at the descriptor level. If set, and the query has no fetch group being set, the default fetch group
 * would be used, unless query.setShouldUseDefaultFetchGroup(false) is also called. In the latter case,
 * the full object will be fetched after the query execution.
 *
 * @see org.eclipse.persistence.queries.FetchGroup
 * @see org.eclipse.persistence.queries.FetchGroupTracker
 *
 * @author King Wang, dclarke
 * @since TopLink 10.1.3.
 */
public class FetchGroup extends AttributeGroup {

    /** 
     * Indicates whether LoadGroup corresponding to FetchGroup should be applied to the query.
     * If set to true then all group's relationship attributes are instantiated. 
     */
    private boolean shouldLoad;
    
    public FetchGroup() {
        super();
    }

    public FetchGroup(String name) {
        super(name);
    }

    /**
     * Return the attribute names on the current FetchGroup. This does not
     * include the attributes on nested FetchGroups
     */
    @Deprecated
    public Set<String> getAttributes() {
        return getAttributeNames();
    }

    /**
     * INTERNAL:
     * Called on attempt to get value of an attribute that hasn't been fetched yet.
     * Returns an error message in case javax.persistence.EntityNotFoundException 
     * should be thrown by the calling method,
     * null otherwise.
     */
    public String onUnfetchedAttribute(FetchGroupTracker entity, String attributeName) {
        ReadObjectQuery query = new ReadObjectQuery(entity);
        query.setShouldUseDefaultFetchGroup(false);
        Object result = entity._persistence_getSession().executeQuery(query);
        if (result == null) {
            Object[] args = { query.getSelectionId() };
            return ExceptionLocalization.buildMessage("no_entities_retrieved_for_get_reference", args);
        }
        return null;
    }

    /**
     * INTERNAL:
     * Called on attempt to assign value to an attribute that hasn't been fetched yet.
     * Returns an error message in case javax.persistence.EntityNotFoundException 
     * should be thrown by the calling method,
     * null otherwise.
     */
    public String onUnfetchedAttributeForSet(FetchGroupTracker entity, String attributeName) {
        return onUnfetchedAttribute(entity, attributeName);
    }

    public void setShouldLoad(boolean shouldLoad) {
        this.shouldLoad = shouldLoad;
    }
    
    public void setShouldLoadAll(boolean shouldLoad) {
        this.shouldLoad = shouldLoad;
        if(this.hasItems()) {
            Iterator<Map.Entry<String, AttributeItem>> it = getItems().entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry<String, AttributeItem> entry = it.next();
                FetchGroup group = (FetchGroup)entry.getValue().getGroup();
                if(group != null) {
                    group.setShouldLoadAll(shouldLoad);
                }
            }
        }
    }
    
    public boolean shouldLoad() {
        return this.shouldLoad;
    }
    
    @Override
    public FetchGroup newGroup(String name, AttributeGroup parent) {
        FetchGroup fetchGroup = new FetchGroup(name);
        if(parent != null) {
            fetchGroup.setShouldLoad(((FetchGroup)parent).shouldLoad());
        }
        return fetchGroup;
    }
    
    public boolean isFetchGroup() {
        return true;
    }

    public boolean isEntityFetchGroup() {
        return false;
    }

    /*
     * LoadGroup created with all member groups with shouldLoad set to false dropped.
     */
    public LoadGroup toLoadGroupLoadOnly() {
        if(!this.shouldLoad) {
            return null;
        }
        LoadGroup loadGroup = new LoadGroup(getName());
        if(this.hasItems()) {
            Iterator<Map.Entry<String, AttributeItem>> it = getItems().entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry<String, AttributeItem> entry = it.next();
                FetchGroup group = (FetchGroup)entry.getValue().getGroup();
                if(group != null) {
                    loadGroup.addAttribute(entry.getKey(), group.toLoadGroupLoadOnly());
                } else {
                    loadGroup.addAttribute(entry.getKey());
                }
            }
        }
        if(!loadGroup.getItems().isEmpty()) {
            return loadGroup;
        } else {
            return null;
        }
    }
    
    public FetchGroup clone() {
        return (FetchGroup)super.clone();
    }    

    /**
     * Returns FetchGroup corresponding to the passed (possibly nested) attribute.
     */
    public FetchGroup getGroup(String attributeNameOrPath) {
        return (FetchGroup)super.getGroup(attributeNameOrPath);
    }
}
