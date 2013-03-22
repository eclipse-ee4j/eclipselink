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
 *     Matt MacIvor - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.core.queries;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;

/**
 * INTERNAL
 * <b>Purpose</b>: Generic superclass for AttributeItem. 
 * 
 * @author matt macivor
 * @since EclipseLink 2.5
 */
public class CoreAttributeItem<ATTRIBUTE_GROUP extends CoreAttributeGroup> implements Serializable, Cloneable {

    protected String attributeName;

    protected ATTRIBUTE_GROUP parent;

    protected ATTRIBUTE_GROUP group;
    
    protected ATTRIBUTE_GROUP keyGroup;

    protected Map<Object, ATTRIBUTE_GROUP> subGroups;
    
    protected Map<Object, ATTRIBUTE_GROUP> keyGroups;

//    private transient DatabaseMapping mapping;

    protected CoreAttributeItem() {        
    }
    
    public CoreAttributeItem(ATTRIBUTE_GROUP parent, String attributeName) {
        this.parent = parent;
        this.attributeName = attributeName;
    }

    /**
     * INTERNAL:
     * Adds the list of groups as to the item
     */
    public void addGroups(Collection<ATTRIBUTE_GROUP> groups) {
        for (ATTRIBUTE_GROUP group : groups){
            addSubGroup(group);
        }
    }
    
    public void addKeyGroup(ATTRIBUTE_GROUP keyGroup) {
        if (keyGroup != null){
            if (this.keyGroups == null){
                this.keyGroups = new HashMap<Object, ATTRIBUTE_GROUP>();
            }
            if (this.keyGroup == null){
                this.keyGroup = keyGroup;
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

    public void addKeyGroups(Collection<ATTRIBUTE_GROUP> keyGroups) {
        for (ATTRIBUTE_GROUP group : keyGroups){
            this.addKeyGroup(group);
        }
    } 

    public void addSubGroup(ATTRIBUTE_GROUP group) {
        if (group != null){
            if (this.subGroups == null){
                this.subGroups = new HashMap<Object, ATTRIBUTE_GROUP>();
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
    
    public CoreAttributeItem<ATTRIBUTE_GROUP> clone(Map<ATTRIBUTE_GROUP, ATTRIBUTE_GROUP> cloneMap, ATTRIBUTE_GROUP parentClone){
        CoreAttributeItem clone = null;
        try {
            clone = (CoreAttributeItem) super.clone();
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        clone.attributeName = this.attributeName;
        if (this.group != null){
            clone.group = this.group.clone(cloneMap);
        }
        if (clone.keyGroup != null){
            clone.keyGroup = this.keyGroup.clone(cloneMap);
        }
        clone.parent = parentClone;
        if (this.subGroups != null){
            clone.subGroups = new HashMap<Object, ATTRIBUTE_GROUP>();
            for (Entry<Object, ATTRIBUTE_GROUP> group : this.subGroups.entrySet()){
                clone.subGroups.put(group.getKey(), group.getValue().clone(cloneMap));
            }
        }
        if (this.keyGroups != null){
            clone.keyGroups = new HashMap<Object, ATTRIBUTE_GROUP>();
            for (Entry<Object, ATTRIBUTE_GROUP> group : this.keyGroups.entrySet()){
                clone.keyGroups.put(group.getKey(), group.getValue().clone(cloneMap));
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
        Map<Object, ATTRIBUTE_GROUP> newMap = new HashMap<Object, ATTRIBUTE_GROUP>();
        if (this.subGroups != null){
            for (ATTRIBUTE_GROUP entry : this.subGroups.values()){
                entry.convertClassNamesToClasses(classLoader);
                if(!(entry.getSubClassGroups().isEmpty())) {
                    newMap.putAll(entry.getSubClassGroups());
                }
                newMap.put(entry.getType(), entry);
                entry.setAllSubclasses(newMap);
            }
        }
        this.subGroups = newMap;
        
        newMap = new HashMap<Object, ATTRIBUTE_GROUP>();
        if (this.keyGroups != null){
            for (ATTRIBUTE_GROUP entry : this.keyGroups.values()){
                entry.convertClassNamesToClasses(classLoader);
                newMap.put(entry.getType(), entry);
                entry.setAllSubclasses(newMap);
            }
        }
        this.keyGroups = newMap;
        for (ATTRIBUTE_GROUP group : this.subGroups.values()){
            if (orderInheritance(group, this.subGroups)){
                group.insertSubClass(this.group);
                this.group = group;
            }
        }
        for (ATTRIBUTE_GROUP group : this.keyGroups.values()){
            if (orderInheritance(group, this.keyGroups)){
                group.insertSubClass(this.keyGroup);
                this.keyGroup = group;
            }
            
        }
    }

    public boolean equals(Object obj) {
        if (this != obj) {
            if(obj == null) {
                return false;
            }
            CoreAttributeItem anotherItem = null;
            try {
                anotherItem = (CoreAttributeItem)obj;
            } catch (ClassCastException cce) {
                return false;
            }
            
            if(this.subGroups != null) {
                if (anotherItem.subGroups == null){
                    return false;
                }
                if (this.subGroups.size() == anotherItem.subGroups.size()){
                    for (Map.Entry<Object, ATTRIBUTE_GROUP> entry : this.subGroups.entrySet()){
                        ATTRIBUTE_GROUP anotherGroup = (ATTRIBUTE_GROUP)anotherItem.subGroups.get(entry.getKey());
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
                    for (Map.Entry<Object, ATTRIBUTE_GROUP> entry : this.keyGroups.entrySet()){
                        ATTRIBUTE_GROUP anotherGroup = (ATTRIBUTE_GROUP)anotherItem.keyGroups.get(entry.getKey());
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
    
    public String getAttributeName() {
        return this.attributeName;
    }

    public ATTRIBUTE_GROUP getGroup() {
        if (this.group == null){
            return null;
        }
        return this.group;
    }

    public ATTRIBUTE_GROUP getGroup(Class type) {
        if (this.subGroups == null || type == null){
            return null;
        }
        ATTRIBUTE_GROUP result = this.subGroups.get(type);
        while(result == null && !type.equals(ClassConstants.Object_Class)){
            type = type.getSuperclass();
            if (type == null){
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("subclass_sought_not_a_managed_type", new Object[]{type.toString(), this.attributeName}));
            }
            result = this.subGroups.get(type);
        }
        return result;
    }

    public Map<Object, ATTRIBUTE_GROUP> getGroups(){
        return this.subGroups;
    }

    public ATTRIBUTE_GROUP getKeyGroup() {
        if (this.keyGroups == null){
            return null;
        }
        return this.keyGroups.get(ClassConstants.Object_Class);
    }

    public ATTRIBUTE_GROUP getKeyGroup(Class type) {
        if (this.keyGroups == null || type == null){
            return null;
        }
        ATTRIBUTE_GROUP result = this.keyGroups.get(type);
        while(result == null && !type.equals(ClassConstants.Object_Class)){
            type = type.getSuperclass();
            if (type == null){
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("subclass_sought_not_a_managed_type", new Object[]{type.toString(), this.attributeName}));
            }
            result = this.keyGroups.get(type);
        }
        return result;
    }

    public Map<Object, ATTRIBUTE_GROUP> getKeyGroups(){
        return this.keyGroups;
    }

    public ATTRIBUTE_GROUP getParent() {
        return this.parent;
    }

    /**
     * Will order the subGroups based on hierarchy.  Returns true if the group is the new root.
     * @param group
     * @param subGroups
     * @return true if the group is the new root.
     */
    protected static boolean orderInheritance(CoreAttributeGroup group, Map<Object, ? extends CoreAttributeGroup> subGroups) {
        Class type = group.getType();
        if (type != null){
            CoreAttributeGroup superClass = null;
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
    
    public void setRootGroup(ATTRIBUTE_GROUP group) {
        this.group = group;
        this.addSubGroup(group);
    }

    public String toString() {        
        return getClass().getSimpleName() + "(" + getAttributeName() + ")" + (this.subGroups!=null ? " => " + this.subGroups.toString() : "") + (this.keyGroups!=null ? " => " + this.keyGroups.toString() : "");
    }

    public String toStringNoClassName() {        
        return getAttributeName() + (this.subGroups!=null ? " => " + this.subGroups.toString() : "")+ (this.keyGroups!=null ? " => " + this.keyGroups.toString() : "");
    }
}
