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
package org.eclipse.persistence.internal.queries;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.internal.sessions.AbstractSession;
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

    public EntityFetchGroup(FetchGroup fetchGroup) {
        super(fetchGroup.getName());
        if(fetchGroup.hasItems()) {
            Iterator<String> it = fetchGroup.getItems().keySet().iterator();
            while(it.hasNext()) {
                super.addAttribute(it.next(), null);
            }
        }
    }
    
    public EntityFetchGroup(String attributeName) {
        super();
        super.addAttribute(attributeName, null);
    }
    
    public EntityFetchGroup(Collection<String> attributeNames) {
        super();
        Iterator<String> it = attributeNames.iterator();
        while(it.hasNext()) {
            super.addAttribute(it.next(), null);
        }
    }
    
    public EntityFetchGroup(String[] attributeNames) {
        super();
        for(int i=0; i < attributeNames.length; i++) {
            super.addAttribute(attributeNames[i], null);
        }
    }
    
    public EntityFetchGroup(FetchGroup fetchGroup, String attributeName) {
        super(fetchGroup.getName() + "+" + attributeName);
        if(fetchGroup.hasItems()) {
            Iterator<String> it = fetchGroup.getItems().keySet().iterator();
            while(it.hasNext()) {
                super.addAttribute(it.next(), null);
            }
        }
        super.addAttribute(attributeName, null);
    }
    
    @Override
    public AttributeItem addAttribute(String attributeNameOrPath, AttributeGroup group) {
        // TODO
        throw new IllegalStateException("Cannot add attribute to EntityFetchGroup");
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
        // TODO
        throw new IllegalStateException( "Cannot get unfetched attribute: " + attributeName);
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
        // TODO
        throw new IllegalStateException("Cannot remove attribute from EntityFetchGroup");
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
}
