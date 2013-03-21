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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/19/2010-2.1 ailitchev - Bug 244124 - Add Nested FetchGroup 
 *     09 Jan 2013-2.5 Gordon Yorke
 *       - 397772: JPA 2.1 Entity Graph Support
 ******************************************************************************/
package org.eclipse.persistence.internal.queries;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.core.queries.CoreAttributeGroup;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.AttributeGroup;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.FetchGroupTracker;

/**
 * 
 * EntityFetchGroup reflects the state of the object.
 * Because EntityFetchGroup doesn't attempt to track 
 * the state of related objects it is flat (non-nested).
 * 
 * @author dclarke, ailitchev
 * @since EclipseLink 2.1
 */
public class EntityFetchGroup extends FetchGroup {

    protected EntityFetchGroup() {        
    }
    
    public EntityFetchGroup(FetchGroup fetchGroup) {
        super(fetchGroup.getName());
        if(fetchGroup.hasItems()) {
            Iterator<String> it = fetchGroup.getItems().keySet().iterator();
            while(it.hasNext()) {
                super.addAttribute(it.next(), (AttributeGroup)null);
            }
        }
    }
    
    public EntityFetchGroup(String attributeName) {
        super();
        super.addAttribute(attributeName, (AttributeGroup)null);
    }
    
    public EntityFetchGroup(Collection<String> attributeNames) {
        super();
        Iterator<String> it = attributeNames.iterator();
        while(it.hasNext()) {
            super.addAttribute(it.next(), (AttributeGroup)null);
        }
    }
    
    public EntityFetchGroup(String[] attributeNames) {
        super();
        for(int i=0; i < attributeNames.length; i++) {
            super.addAttribute(attributeNames[i], (AttributeGroup)null);
        }
    }
    
    public EntityFetchGroup(FetchGroup fetchGroup, String attributeName) {
        super(fetchGroup.getName() + "+" + attributeName);
        if(fetchGroup.hasItems()) {
            Iterator<String> it = fetchGroup.getItems().keySet().iterator();
            while(it.hasNext()) {
                super.addAttribute(it.next(), (AttributeGroup)null);
            }
        }
        super.addAttribute(attributeName, (AttributeGroup)null);
    }
    
    @Override
    public void addAttribute(String attributeNameOrPath, CoreAttributeGroup group) {
        throw new IllegalStateException(ExceptionLocalization.buildMessage("cannot_update_entity_fetch-group", new Object[]{this, attributeNameOrPath}));
    }

    /**
     * Called on attempt to get value of an attribute that hasn't been fetched yet.
     * Returns an error message in case exception should be thrown by the calling method,
     * null otherwise.
     */
    @Override
    public String onUnfetchedAttribute(FetchGroupTracker entity, String attributeName) {
        if (entity._persistence_getSession() != null) {
            return super.onUnfetchedAttribute(entity, attributeName);
        }
        throw new IllegalStateException(ExceptionLocalization.buildMessage("cannot_get_unfetched_attribute", new Object[]{entity, attributeName}));
    }

    /**
     * Called on attempt to assign value to an attribute that hasn't been fetched yet.
     * Returns an error message in case exception should be thrown by the calling method,
     * null otherwise.
     */
    @Override
    public String onUnfetchedAttributeForSet(FetchGroupTracker entity, String attributeName) {
        if (entity._persistence_getSession() != null) {
            return super.onUnfetchedAttributeForSet(entity, attributeName);
        } else {
            entity._persistence_setFetchGroup(new EntityFetchGroup(this, attributeName));
            return null;
        }
    }

    @Override
    public void removeAttribute(String attributeNameOrPath) {
        throw new IllegalStateException(ExceptionLocalization.buildMessage("cannot_update_entity_fetch-group", new Object[]{this, attributeNameOrPath}));
    }

    /**
     * Set this EntityFetchGroup on an entity implementing
     * {@link FetchGroupTracker}.
     */
    public void setOnEntity(Object entity, AbstractSession session) {
        ((FetchGroupTracker)entity)._persistence_setFetchGroup(this);
        ((FetchGroupTracker)entity)._persistence_setSession(session);
    }

    @Override
    public boolean isEntityFetchGroup() {
        return true;
    }
    
    /**
     * Return true if this EntityFetchGroup is a super-set of the passed in
     * EntityFetchGroup.
     */
    @Override
    public boolean isSupersetOf(CoreAttributeGroup anotherGroup) {
        if (anotherGroup == null) {
            return false;
        }
        return this.getAttributeNames().containsAll(anotherGroup.getAttributeNames());
    }

}
