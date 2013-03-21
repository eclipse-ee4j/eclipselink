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
 *     ailitchev - Bug 244124 - Added AttributeGroup base class for nesting 
 *     09 Jan 2013-2.5 Gordon Yorke
 *       - 397772: JPA 2.1 Entity Graph Support
 ******************************************************************************/
package org.eclipse.persistence.queries;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;

import org.eclipse.persistence.core.queries.CoreAttributeGroup;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.queries.AttributeItem;
import org.eclipse.persistence.sessions.CopyGroup;

/**
 * <b>Purpose</b>: An AttributeGroup represents a set of mappings and nested
 * AttributeGroups for relationship mappings for an entity type.
 * <b>Responsibilities</b>:
 * <ul>
 * <li>Defines which attributes should be fetched from the database within a
 * {@link FetchGroup}.
 * <li>Define which relationship attributes should be populated in a resulting
 * entity graph within a {@link LoadGroup}
 * <li>Define which attributes should be copied within a {@link CopyGroup}
 * </ul>
 * <p>
 * To reference nested attributes a dot ('.') notation is used to reference
 * related attributes. All attribute names provided are assumed to be correct
 * until processed against the mappings during usage of the group.
 * 
 * @see FetchGroup
 * @see LoadGroup
 * @see CopyGroup
 * 
 * @author ailitchev
 * @since EclipseLink 2.1
 */
public class AttributeGroup extends CoreAttributeGroup<AttributeItem> implements Serializable, Cloneable {

    public AttributeGroup(String name) {
        this.name = name;
    }

    /**
     * INTERNAL:
     * This constructer is to only be used by EclipseLink internally
     * @param name
     * @param type
     */
    public AttributeGroup(String name, Class type, boolean isValidated) {
        this(name);
        this.type = type;
        this.isValidated = isValidated;
        
    }

    /*
     * INTERNAL:
     * Used to create an attribute with a name of the class type.
     */
    public AttributeGroup(String name, String type, boolean isValidated) {
        this(name);
        this.typeName = type;
        this.isValidated = isValidated;
        
    }

    public AttributeGroup() {
        this("");
    }

    /**
     * Add a basic attribute or nested attribute with each String representing
     * an attribute on the path to what needs to be included in the
     * AttributeGroup.
     * <p>
     * Example: <code>
     *    group.addAttribute("firstName", group1);<br>
     *    group.addAttribute("manager.address", group2);
     * </code>
     * 
     * Note that existing group corresponding to attributeNameOrPath
     * will be overridden with the passed group. 
     * 
     * @param attrPathOrName
     *            A simple attribute, array or attributes forming a path
     * @param group - an AttributeGroup to be added.
     */
    public void addAttribute(String attributeNameOrPath, AttributeGroup group) {
        super.addAttribute(attributeNameOrPath, group);
    }
    
    /**
     * Returns AttributeGroup corresponding to the passed (possibly nested)
     * attribute.
     */
    public AttributeGroup getGroup(String attributeNameOrPath) {
        return (AttributeGroup)super.getGroup(attributeNameOrPath);
    }

    /**
     * Return true if this AttributeGroup is a super-set of the passed in
     * AttributeGroup.
     */
    public boolean isSupersetOf(AttributeGroup anotherGroup) {
        return super.isSupersetOf(anotherGroup);
    }
    
    /**
     * INTERNAL:
     * Lookup the {@link AttributeItem}for the provided attribute name or path.
     * 
     * @return item or null
     * @throws IllegalArgumentException if name is not valid attribute name or path
     */
    public AttributeItem getItem(String attributeNameOrPath) {
        return super.getItem(attributeNameOrPath);
    }
    
    /**
     * Subclass may create different types.
     */
    protected AttributeItem newItem(AttributeGroup group, String attrName) {
        return new AttributeItem(group, attrName);
    }

    @Override
    public AttributeGroup findGroup(ClassDescriptor type){
        return (AttributeGroup)super.findGroup(type);
    }
    
    /**
     * Convert the group to a FetchGroup for usage with queries.
     */
    public FetchGroup toFetchGroup() {
        if (isFetchGroup()) {
            return (FetchGroup) this;
        }
        return toFetchGroup(new HashMap<AttributeGroup, FetchGroup>());
    }

    /**
     * INTERNAL:
     *    This method is used internally when converting to a copy group.
     * @param cloneMap
     * @return
     */
    
    public FetchGroup toFetchGroup(Map<AttributeGroup, FetchGroup> cloneMap){
        FetchGroup clone = cloneMap.get(this);
        if (clone != null) {
            return clone;
        }
        clone = new FetchGroup(this.name);
        
        clone.type = this.type;
        clone.typeName = this.typeName;
        clone.isValidated = this.isValidated;
        cloneMap.put(this,clone);
        if (this.superClassGroup != null){
            clone.superClassGroup = ((AttributeGroup)this.superClassGroup).toFetchGroup(cloneMap);
        }
        if (this.allsubclasses != null){
            for (CoreAttributeGroup group : this.allsubclasses.values()){
                clone.getSubClassGroups().put(group.getType(), ((AttributeGroup)group).toFetchGroup(cloneMap));
            }
        }
        if (this.subClasses != null){
            clone.subClasses = new HashSet<CoreAttributeGroup>();
            for (CoreAttributeGroup group : this.subClasses){
                clone.subClasses.add(((AttributeGroup)group).toFetchGroup(cloneMap));
            }
        }
        // all attributes and nested groups should be cloned, too
        clone.items = null;
        if (hasItems()) {
            clone.items = new HashMap<String, AttributeItem>();
            for (AttributeItem item : this.items.values()){
                clone.items.put(item.getAttributeName(), item.toFetchGroup(cloneMap, clone));
            }
        }
        return clone;
    }

    /**
     * INTERNAL:
     *    This method is used internally when converting to a copy group.
     * @param cloneMap
     * @return
     */
    
    public boolean isCopyGroup() {
        return false;
    }

    /**
     * Convert the group to a CopyGroup for usage with the copy() API.
     */
    public CopyGroup toCopyGroup() {
        if (isCopyGroup()) {
            return (CopyGroup) this;
        }
        Map<AttributeGroup, CopyGroup> cloneMap = new IdentityHashMap<AttributeGroup, CopyGroup>();
        return toCopyGroup(cloneMap, new HashMap<Object, Object>());
    }

        /**
         * INTERNAL:
         *    This method is used internally when converting to a copy group.
         * @param cloneMap
         * @return
         */
        
        public CopyGroup toCopyGroup(Map<AttributeGroup, CopyGroup> cloneMap, Map copies){
            CopyGroup clone = cloneMap.get(this);
            if (clone != null) {
                return clone;
            }
            clone = new CopyGroup(this.name);
            clone.cascadeTree();
            clone.setCopies(copies);
            
            clone.type = this.type;
            clone.typeName = this.typeName;
            clone.isValidated = this.isValidated;
            cloneMap.put(this,clone);
            
            if (this.allsubclasses != null){
                for (CoreAttributeGroup group : this.allsubclasses.values()){
                    clone.getSubClassGroups().put(group.getType(), ((AttributeGroup)group).toCopyGroup(cloneMap, copies));
                }
            }
            if (this.superClassGroup != null){
                clone.superClassGroup = ((AttributeGroup)this.superClassGroup).toCopyGroup(cloneMap, copies);
            }
            if (this.subClasses != null){
                clone.subClasses = new HashSet<CoreAttributeGroup>();
                for (CoreAttributeGroup group : this.subClasses){
                    clone.subClasses.add(((AttributeGroup)group).toCopyGroup(cloneMap, copies));
                }
            }
            // all attributes and nested groups should be cloned, too
            clone.items = null;
            if (hasItems()) {
                clone.items = new HashMap<String, AttributeItem>();
                for (AttributeItem item : this.items.values()){
                    clone.items.put(item.getAttributeName(), item.toCopyGroup(cloneMap, clone, copies));
                }
            }
            return clone;
        }


    public boolean isLoadGroup() {
        return false;
    }

    /**
     * Convert the group to a LoadGroup for usage with queries.
     */
    public LoadGroup toLoadGroup() {
        if (this.isLoadGroup()) {
            return (LoadGroup) this;
        }
        return toLoadGroup(new HashMap<AttributeGroup, LoadGroup>(), false);
    }

    public LoadGroup toLoadGroup(Map<AttributeGroup, LoadGroup> cloneMap, boolean loadOnly){
        LoadGroup clone = cloneMap.get(this);
        if (clone != null) {
            return clone;
        }
        clone = new LoadGroup(this.name);
        
        clone.type = this.type;
        clone.typeName = this.typeName;
        clone.isValidated = this.isValidated;
        cloneMap.put(this,clone);
        if (this.allsubclasses != null){
            for (CoreAttributeGroup group : this.allsubclasses.values()){
                clone.getSubClassGroups().put(group.getType(), ((AttributeGroup)group).toLoadGroup(cloneMap, loadOnly));
            }
        }
        if (this.superClassGroup != null){
            clone.superClassGroup = ((AttributeGroup)this.superClassGroup).toLoadGroup(cloneMap, loadOnly);
        }
        if (this.subClasses != null){
            clone.subClasses = new HashSet<CoreAttributeGroup>();
            for (CoreAttributeGroup group : this.subClasses){
                clone.subClasses.add(((AttributeGroup)group).toLoadGroup(cloneMap, loadOnly));
            }
        }
        // all attributes and nested groups should be cloned, too
        clone.items = null;
        if (hasItems()) {
            clone.items = new HashMap<String, AttributeItem>();
            for (AttributeItem item : this.items.values()){
                clone.items.put(item.getAttributeName(), item.toLoadGroup(cloneMap, clone, loadOnly));
            }
        }
        return clone;
    }

    @Override
    public AttributeGroup clone() {
        return (AttributeGroup)super.clone();
    }

    /**
     * INTERNAL:
     * Only LoadGroups allow concurrency.
     */
    public boolean isConcurrent() {
        return false;
    }
    /**
     * Subclass may create different types.
     */
    @Override
    protected AttributeItem newItem(CoreAttributeGroup group, String attrName) {
        return new AttributeItem((AttributeGroup)group, attrName);
    }

    /**
     * Subclass may create different types.
     */
    @Override
    protected AttributeGroup newGroup(String name, CoreAttributeGroup parent) {
        return new AttributeGroup(name);
    }  

   
}
