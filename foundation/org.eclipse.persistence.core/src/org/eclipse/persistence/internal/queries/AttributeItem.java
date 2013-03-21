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
 *     05/19/2010-2.1 ailitchev - Bug 244124 - Add Nested FetchGroup 
 *     09 Jan 2013-2.5 Gordon Yorke
 *       - 397772: JPA 2.1 Entity Graph Support
 ******************************************************************************/
package org.eclipse.persistence.internal.queries;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.persistence.core.queries.CoreAttributeItem;

import org.eclipse.persistence.queries.AttributeGroup;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.LoadGroup;
import org.eclipse.persistence.sessions.CopyGroup;

/**
 * <b>Purpose</b>: Elements stored in AttributeGroup
 * 
 * @author dclarke, ailitchev
 * @since EclipseLink 2.1
 */
public class AttributeItem extends CoreAttributeItem<AttributeGroup> implements Serializable {

    protected AttributeItem() {        
    }
    
    public AttributeItem(AttributeGroup parent, String attributeName) {
        this.parent = parent;
        this.attributeName = attributeName;
    }

    public String getAttributeName() {
        return this.attributeName;
    }


    public AttributeItem toCopyGroup(Map<AttributeGroup, CopyGroup> cloneMap, CopyGroup parentClone, Map copies){
        AttributeItem clone = new AttributeItem();
        clone.attributeName = this.attributeName;
        if (this.group != null){
            clone.group = this.group.toCopyGroup(cloneMap, copies);
        }
        if (clone.keyGroup != null){
            clone.keyGroup = this.keyGroup.toCopyGroup(cloneMap, copies);
        }
        clone.parent = parentClone;
        if (this.subGroups != null){
            clone.subGroups = new HashMap<Object, AttributeGroup>();
            for (Entry<Object, AttributeGroup> group : this.subGroups.entrySet()){
                clone.subGroups.put(group.getKey(), group.getValue().toCopyGroup(cloneMap, copies));
            }
        }
        if (this.keyGroups != null){
            clone.keyGroups = new HashMap<Object, AttributeGroup>();
            for (Entry<Object, AttributeGroup> group : this.keyGroups.entrySet()){
                clone.keyGroups.put(group.getKey(), group.getValue().toCopyGroup(cloneMap, copies));
            }
        }
        return clone;
        
    }

    public AttributeItem toFetchGroup(Map<AttributeGroup, FetchGroup> cloneMap, FetchGroup parentClone){
        AttributeItem clone = new AttributeItem();
        clone.attributeName = this.attributeName;
        if (this.group != null){
            clone.group = this.group.toFetchGroup(cloneMap);
        }
        if (clone.keyGroup != null){
            clone.keyGroup = this.keyGroup.toFetchGroup(cloneMap);
        }
        clone.parent = parentClone;
        if (this.subGroups != null){
            clone.subGroups = new HashMap<Object, AttributeGroup>();
            for (Entry<Object, AttributeGroup> group : this.subGroups.entrySet()){
                clone.subGroups.put(group.getKey(), group.getValue().toFetchGroup(cloneMap));
            }
        }
        if (this.keyGroups != null){
            clone.keyGroups = new HashMap<Object, AttributeGroup>();
            for (Entry<Object, AttributeGroup> group : this.keyGroups.entrySet()){
                clone.keyGroups.put(group.getKey(), group.getValue().toFetchGroup(cloneMap));
            }
        }
        return clone;
        
    }

    public AttributeItem toLoadGroup(Map<AttributeGroup, LoadGroup> cloneMap, LoadGroup parentClone, boolean loadOnly){
        AttributeItem clone = new AttributeItem();
        clone.attributeName = this.attributeName;
        if (this.group != null){
            clone.group = this.group.toLoadGroup(cloneMap, loadOnly);
        }
        if (clone.keyGroup != null){
            clone.keyGroup = this.keyGroup.toLoadGroup(cloneMap, loadOnly);
        }
        clone.parent = parentClone;
        if (this.subGroups != null){
            clone.subGroups = new HashMap<Object, AttributeGroup>();
            for (Entry<Object, AttributeGroup> group : this.subGroups.entrySet()){
                clone.subGroups.put(group.getKey(), group.getValue().toLoadGroup(cloneMap, loadOnly));
            }
        }
        if (this.keyGroups != null){
            clone.keyGroups = new HashMap<Object, AttributeGroup>();
            for (Entry<Object, AttributeGroup> group : this.keyGroups.entrySet()){
                clone.keyGroups.put(group.getKey(), group.getValue().toLoadGroup(cloneMap, loadOnly));
            }
        }
        return clone;
        
    }

    public AttributeGroup getParent() {
        return super.getParent();
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
            
            if(this.subGroups != null) {
                if (anotherItem.subGroups == null){
                    return false;
                }
                if (this.subGroups.size() == anotherItem.subGroups.size()){
                    for (Map.Entry<Object, AttributeGroup> entry : this.subGroups.entrySet()){
                        AttributeGroup anotherGroup = anotherItem.subGroups.get(entry.getKey());
                        if (! entry.getValue().equals(anotherGroup)){
                            return false;
                        }
                    }
                }else{
                    return false;
                }
            } else if (anotherItem.subGroups != null){
                return false;
            }
            
            if(this.keyGroups != null) {
                if (anotherItem.keyGroups == null){
                    return false;
                }
                if (this.keyGroups.size() == anotherItem.keyGroups.size()){
                    for (Map.Entry<Object, AttributeGroup> entry : this.keyGroups.entrySet()){
                        AttributeGroup anotherGroup = anotherItem.keyGroups.get(entry.getKey());
                        if (! entry.getValue().equals(anotherGroup)){
                            return false;
                        }
                    }
                }else{
                    return false;
                }
            } else if (anotherItem.keyGroups != null){
                return false;
            }
        }
        return true;
    }
    
    public AttributeGroup getGroup() {
        return super.getGroup();
    }
    
    public void setGroup(AttributeGroup group) {
        this.group = group;
    }


}
