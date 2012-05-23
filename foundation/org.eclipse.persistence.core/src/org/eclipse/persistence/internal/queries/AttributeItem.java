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
 *     05/19/2010-2.1 ailitchev - Bug 244124 - Add Nested FetchGroup 
 ******************************************************************************/
package org.eclipse.persistence.internal.queries;

import java.io.Serializable;

import org.eclipse.persistence.queries.AttributeGroup;

/**
 * <b>Purpose</b>: Elements stored in AttributeGroup
 * 
 * @author dclarke, ailitchev
 * @since EclipseLink 2.1
 */
public class AttributeItem implements Serializable {

    private String attributeName;

    private AttributeGroup parent;

    private AttributeGroup group;

//    private transient DatabaseMapping mapping;

    public AttributeItem(AttributeGroup parent, String attributeName) {
        this.parent = parent;
        this.attributeName = attributeName;
    }

    public String getAttributeName() {
        return this.attributeName;
    }

    public AttributeGroup getGroup() {
        return this.group;
    }

    public void setGroup(AttributeGroup group) {
        this.group = group;
    }

    public AttributeGroup getParent() {
        return this.parent;
    }

    public boolean equals(Object obj) {
        if (this != obj) {
            if(obj == null) {
                return false;
            }
            AttributeItem anotherItem = null;
            try {
                anotherItem = (AttributeItem)obj;
            } catch (ClassCastException cce) {
                return false;
            }
            if(this.group != null) {
                return this.group.equals(anotherItem.getGroup());
            } else {
                return (anotherItem.getGroup()==null) || !anotherItem.getGroup().hasItems();
            }
        } else {
            return true;
        }
    }

    /**
     * Lazily lookup and hold the mapping
     * 
     * @param session
     * @param entity
     * @return
     */
/*    public DatabaseMapping getMapping(Session session, Class<?> entityClass) {
        if (this.mapping == null) {
            ClassDescriptor descriptor = session.getClassDescriptor(entityClass);
            this.mapping = descriptor.getMappingForAttributeName(getAttributeName());

            if (this.mapping == null) {
                throw QueryException.fetchGroupAttributeNotMapped(getAttributeName());
            }
        }
        return this.mapping;
    }*/

    public String toString() {        
        return getClass().getSimpleName() + "(" + getAttributeName() + ")" + (this.group!=null ? " => " + this.group.toString() : "");
    }

    public String toStringNoClassName() {        
        return getAttributeName() + (this.group!=null ? " => " + this.group.toString() : "");
    }
}
