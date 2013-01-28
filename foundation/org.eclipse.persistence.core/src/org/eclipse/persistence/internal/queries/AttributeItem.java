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
 *     09 Jan 2013-2.5 Gordon Yorke
 *       - 397772: JPA 2.1 Entity Graph Support
 ******************************************************************************/
package org.eclipse.persistence.internal.queries;

import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
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
public class AttributeItem implements Serializable {

    private String attributeName;

    private AttributeGroup parent;

    private AttributeGroup group;
    
    protected AttributeGroup keyGroup;

    private Map<Object, AttributeGroup> subGroups;
    
    protected Map<Object, AttributeGroup> keyGroups;

//    private transient DatabaseMapping mapping;

    protected AttributeItem() {        
    }
    
    public AttributeItem(AttributeGroup parent, String attributeName) {
        this.parent = parent;
        this.attributeName = attributeName;
    }

    public String getAttributeName() {
        return this.attributeName;
    }

    public AttributeGroup getGroup() {
        if (this.group == null){
            return null;
        }
        return this.group;
    }

    public AttributeGroup getGroup(Class type) {
        if (this.subGroups == null || type == null){
            return null;
        }
        AttributeGroup result = this.subGroups.get(type);
        while(result == null && !type.equals(ClassConstants.Object_Class)){
            type = type.getSuperclass();
            if (type == null){
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("subclass_sought_not_a_managed_type", new Object[]{type.toString(), this.attributeName}));
            }
            result = this.subGroups.get(type);
        }
        return result;
    }

    public Map<Object, AttributeGroup> getGroups(){
        return this.subGroups;
    }

    public void setRootGroup(AttributeGroup group) {
        this.group = group;
        this.addSubGroup(group);
    }

    public void addSubGroup(AttributeGroup group) {
        if (group != null){
            if (this.subGroups == null){
                this.subGroups = new HashMap<Object, AttributeGroup>();
            }
            if (this.group == null){
                this.group = group;
            }
            Object type = group.getType();
            if (type == null){
                type = group.getTypeName();
            }
            if (type == null){
                type = ClassConstants.Object_Class;
                if (this.subGroups.containsKey(type)){
                    throw new IllegalArgumentException(ExceptionLocalization.buildMessage("only_one_root_subgraph"));
                }
            }
            this.subGroups.put(type, group);
            if (orderInheritance(group, this.subGroups)){
                group.insertSubClass(this.group);
                this.group = group;
            }
        }
    }

    /**
     * Will order the subGroups based on hierarchy.  Returns true if the group is the new root.
     * @param group
     * @param subGroups
     * @return true if the group is the new root.
     */
    protected boolean orderInheritance(AttributeGroup group, Map<Object, AttributeGroup> subGroups) {
        Class type = group.getType();
        if (type != null){
            AttributeGroup superClass = null;
            while (!type.equals(ClassConstants.Object_Class) && superClass == null){
                type = type.getSuperclass();
                superClass = subGroups.get(type);
            }
            if (superClass != null){
                superClass.insertSubClass(group);
            }else{
                return true;
            }
        }
        return false;
    }

    public AttributeGroup getKeyGroup() {
        if (this.keyGroups == null){
            return null;
        }
        return this.keyGroups.get(ClassConstants.Object_Class);
    }

    public AttributeGroup getKeyGroup(Class type) {
        if (this.keyGroups == null || type == null){
            return null;
        }
        AttributeGroup result = this.keyGroups.get(type);
        while(result == null && !type.equals(ClassConstants.Object_Class)){
            type = type.getSuperclass();
            if (type == null){
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("subclass_sought_not_a_managed_type", new Object[]{type.toString(), this.attributeName}));
            }
            result = this.keyGroups.get(type);
        }
        return result;
    }

    public Map<Object, AttributeGroup> getKeyGroups(){
        return this.keyGroups;
    }

    public void addKeyGroup(AttributeGroup keyGroup) {
        if (keyGroup != null){
            if (this.keyGroups == null){
                this.keyGroups = new HashMap<Object, AttributeGroup>();
            }
            if (this.keyGroup == null){
                this.keyGroup = group;
            }
            Object type = keyGroup.getType();
            if (type == null){
                type = keyGroup.getTypeName();
            }
            if (type == null){
                type = ClassConstants.Object_Class;
                if (this.keyGroups.containsKey(type)){
                    throw new IllegalArgumentException(ExceptionLocalization.buildMessage("only_one_root_subgraph"));
                }
            }
            this.keyGroups.put(type, keyGroup);
            if (orderInheritance(keyGroup, this.keyGroups)){
                keyGroup.insertSubClass(this.keyGroup);
                this.keyGroup = keyGroup;
            }
        }
    }

    public void addKeyGroups(Collection<AttributeGroup> keyGroups) {
        for (AttributeGroup group : keyGroups){
            this.addKeyGroup(group);
        }
    }

    public AttributeItem clone(Map<AttributeGroup, AttributeGroup> cloneMap, AttributeGroup parentClone){
        AttributeItem clone = new AttributeItem();
        clone.attributeName = this.attributeName;
        if (this.group != null){
            clone.group = this.group.clone(cloneMap);
        }
        if (clone.keyGroup != null){
            clone.keyGroup = this.keyGroup.clone(cloneMap);
        }
        clone.parent = parentClone;
        if (this.subGroups != null){
            clone.subGroups = new HashMap<Object, AttributeGroup>();
            for (Entry<Object, AttributeGroup> group : this.subGroups.entrySet()){
                clone.subGroups.put(group.getKey(), group.getValue().clone(cloneMap));
            }
        }
        if (this.keyGroups != null){
            clone.keyGroups = new HashMap<Object, AttributeGroup>();
            for (Entry<Object, AttributeGroup> group : this.keyGroups.entrySet()){
                clone.keyGroups.put(group.getKey(), group.getValue().clone(cloneMap));
            }
        }
        return clone;
        
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

    public AttributeItem toLoadGroup(Map<AttributeGroup, LoadGroup> cloneMap, LoadGroup parentClone){
        AttributeItem clone = new AttributeItem();
        clone.attributeName = this.attributeName;
        if (this.group != null){
            clone.group = this.group.toLoadGroup(cloneMap);
        }
        if (clone.keyGroup != null){
            clone.keyGroup = this.keyGroup.toLoadGroup(cloneMap);
        }
        clone.parent = parentClone;
        if (this.subGroups != null){
            clone.subGroups = new HashMap<Object, AttributeGroup>();
            for (Entry<Object, AttributeGroup> group : this.subGroups.entrySet()){
                clone.subGroups.put(group.getKey(), group.getValue().toLoadGroup(cloneMap));
            }
        }
        if (this.keyGroups != null){
            clone.keyGroups = new HashMap<Object, AttributeGroup>();
            for (Entry<Object, AttributeGroup> group : this.keyGroups.entrySet()){
                clone.keyGroups.put(group.getKey(), group.getValue().toLoadGroup(cloneMap));
            }
        }
        return clone;
        
    }
    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this Descriptor to actual class-based
     * settings. This method is used when converting a project that has been built
     * with class names to a project with classes.
     * @param classLoader 
     */
    public void convertClassNamesToClasses(ClassLoader classLoader){
        Map<Object, AttributeGroup> newMap = new HashMap<Object, AttributeGroup>();
        if (this.subGroups != null){
            for (Map.Entry<Object, AttributeGroup> entry : this.subGroups.entrySet()){
                Class key = null;
                try{
                    if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                        try {
                            key = (Class)AccessController.doPrivileged(new PrivilegedClassForName((String) entry.getKey(), true, classLoader));
                        } catch (PrivilegedActionException exception) {
                            throw ValidationException.classNotFoundWhileConvertingClassNames((String) entry.getKey(), exception.getException());
                        }
                    } else {
                        key = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName((String) entry.getKey(), true, classLoader);
                    }
                } catch (ClassNotFoundException exc){
                    throw ValidationException.classNotFoundWhileConvertingClassNames((String) entry.getKey(), exc);
                }
                newMap.put(key, entry.getValue());
            }
        }
        this.subGroups = newMap;
        
        newMap = new HashMap<Object, AttributeGroup>();
        if (this.keyGroups != null){
            for (Map.Entry<Object, AttributeGroup> entry : this.keyGroups.entrySet()){
                Class key = null;
                try{
                    if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                        try {
                            key = (Class)AccessController.doPrivileged(new PrivilegedClassForName((String) entry.getKey(), true, classLoader));
                        } catch (PrivilegedActionException exception) {
                            throw ValidationException.classNotFoundWhileConvertingClassNames((String) entry.getKey(), exception.getException());
                        }
                    } else {
                        key = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName((String) entry.getKey(), true, classLoader);
                    }
                } catch (ClassNotFoundException exc){
                    throw ValidationException.classNotFoundWhileConvertingClassNames((String) entry.getKey(), exc);
                }
                newMap.put(key, entry.getValue());
                entry.getValue().convertClassNamesToClasses(classLoader);
            }
        }
        this.keyGroups = newMap;
        for (AttributeGroup group : this.subGroups.values()){
            if (orderInheritance(group, this.subGroups)){
                group.insertSubClass(this.group);
                this.group = group;
            }
        }
        for (AttributeGroup group : this.keyGroups.values()){
            if (orderInheritance(group, this.keyGroups)){
                group.insertSubClass(this.keyGroup);
                this.keyGroup = group;
            }
            
        }
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
        return getClass().getSimpleName() + "(" + getAttributeName() + ")" + (this.subGroups!=null ? " => " + this.subGroups.toString() : "") + (this.keyGroups!=null ? " => " + this.keyGroups.toString() : "");
    }

    public String toStringNoClassName() {        
        return getAttributeName() + (this.subGroups!=null ? " => " + this.subGroups.toString() : "")+ (this.keyGroups!=null ? " => " + this.keyGroups.toString() : "");
    }

    /**
     * INTERNAL:
     * Adds the list of groups as to the item
     */
    public void addGroups(Collection<AttributeGroup> groups) {
        for (AttributeGroup group : groups){
            addSubGroup(group);
        }
    }
}
