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
 *     ailitchev - Bug 244124 - Added AttributeGroup base class for nesting 
 *     09 Jan 2013-2.5 Gordon Yorke
 *       - 397772: JPA 2.1 Entity Graph Support
 ******************************************************************************/
package org.eclipse.persistence.queries;

import java.io.Serializable;
import java.io.StringWriter;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.queries.AttributeItem;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
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
public class AttributeGroup implements Serializable, Cloneable {

    /**
     * Name of the group. This is used in subclasses where the groups are stored
     * and can be used within a query by name as with FetchGroup. For dynamic
     * groups the name has no functional value.
     */
    private String name;
    
    /**
     * The name of the class represented by this AttrbuteGroup.  Used to specify overriding
     * groups for subclasses.
     */
    protected String typeName;
    /**
     * The class represented by this AttrbuteGroup.  Used to specify overriding
     * groups for subclasses.
     */
    protected Class type;
    
    /**
     * To add inheritance support the two following attrbutes are used to create a model of the inheritance tree
     * This attribute points to the parent AttributeGroup of this attribute group.
     */
    protected AttributeGroup superClassGroup;
    
    /**
     * This attribute references the immediate subclass groups for this attributeGroup.  This is not required but acts
     * as a means to support adding inheritance branches into the an established tree.
     */
    protected List<AttributeGroup> subClasses;

    /**
     * Specified attributes in the group mapped to their AttributeItems
     */
    protected Map<String, AttributeItem> items;

    public AttributeGroup(String name) {
        this.name = name;
    }

    public AttributeGroup(String name, Class type) {
        this(name);
        this.type = type;
        
    }

    /*
     * INTERNAL:
     * Used to create an attribute with a name of the class type.
     */
    public AttributeGroup(String name, String type) {
        this(name);
        this.typeName = type;
        
    }

    public AttributeGroup() {
        this("");
    }

    /**
     * Subclass may create different types.
     */
    protected AttributeItem newItem(AttributeGroup group, String attrName) {
        return new AttributeItem(group, attrName);
    }

    /**
     * Subclass may create different types.
     */
    protected AttributeGroup newGroup(String name, AttributeGroup parent) {
        return new AttributeGroup(name);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public Set<String> getAttributeNames() {
        return getItems().keySet();
    }

    public Class getType() {
        return type;
    }

    /**
     * INTERNAL:
     * Returns the name of the type this group represents
     */
    public String getTypeName() {
        return typeName;
    }

    // XXX-dclarke: Should this be public?
    public void setAttributeNames(Set attributeNames) {
        Iterator it = attributeNames.iterator();
        while (it.hasNext()) {
            this.addAttribute((String) it.next());
        }
    }

    /**
     * Indicates whether the group has at least one attribute.
     */
    public boolean hasItems() {
        return this.items != null && !this.items.isEmpty();
    }

    /**
     * INTERNAL:
     */
    public Map<String, AttributeItem> getItems() {
        if (this.items == null) {
            this.items = new HashMap();
        }
        return this.items;
    }

    /**
     * INTERNAL:
     * Return if the attribute is defined in the group.
     * Only local attribute names are checked.
     */
    public boolean containsAttributeInternal(String attributeName) {
        return (this.items != null) && this.items.containsKey(attributeName);
    }

    /**
     * Return if the attribute is defined in the group.
     */
    public boolean containsAttribute(String attributeNameOrPath) {
        String[] path = convert(attributeNameOrPath);

        return getItem(path, false) != null;
    }

    /**
     * Add a basic attribute or nested attribute with each String representing
     * an attribute on the path to what needs to be included in the
     * AttributeGroup.
     * <p>
     * Example: <code>
     *    group.addAttribute("firstName");<br>
     *    group.addAttribute("manager.address");
     * </code>
     * 
     * @param attrPathOrName
     *            A simple attribute, array or attributes forming a path
     */
    public void addAttribute(String attributeNameOrPath) {
        addAttribute(attributeNameOrPath, (AttributeGroup)null);
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
        AttributeItem item = getItem(convert(attributeNameOrPath), true);
        item.addSubGroup(group);
    }

    /**
     * Add an attribute and the corresponding list of AttributeGroups.
     * Multiple groups are added in the case of inheritance
     * <p>
     * @param attrPathOrName
     *            A simple attribute, array or attributes forming a path
     * @param group - a collection of AttributeGroups to be added.
     */
    public void addAttribute(String attributeNameOrPath, Collection<AttributeGroup> groups) {
        AttributeItem item = getItem(convert(attributeNameOrPath), true);
        item.addGroups(groups);
    }

    /**
     * Add a basic attribute or nested attribute with each String representing
     * the key of an attribute of type Map on the path to what needs to be
     * included in the AttributeGroup.
     * <p>
     * Example: <code>
     *    group.addAttribute("firstName", group1);<br>
     *    group.addAttribute("manager.address", group2);
     * </code>
     * 
     * Note that existing group corresponding to attributeNameOrPath will be
     * overridden with the passed group.
     * 
     * @param attrPathOrName
     *            A simple attribute, array or attributes forming a path to a
     *            Map key
     * @param group
     *            - an AttributeGroup to be added.
     */
    public void addAttributeKey(String attributeNameOrPath, AttributeGroup group) {
        AttributeItem item = getItem(convert(attributeNameOrPath), true);
        item.addKeyGroup(group);
    }

    /**
     * Add a set of attributes to the group.
     */
    public void addAttributes(Collection<String> attrOrPaths) {
        for (String attr : attrOrPaths) {
            addAttribute(attr);
        }
    }

    /**
     * Returns AttributeGroup corresponding to the passed (possibly nested)
     * attribute.
     */
    public AttributeGroup getGroup(String attributeNameOrPath) {
        AttributeItem item = getItem(convert(attributeNameOrPath), false);
        if (item != null) {
            return item.getGroup();
        }
        return null;
    }

    /**
     * INTERNAL:
     * Lookup the {@link AttributeItem}for the provided attribute name or path.
     * 
     * @return item or null
     * @throws IllegalArgumentException if name is not valid attribute name or path
     */
    public AttributeItem getItem(String attributeNameOrPath) {
        return getItem(convert(attributeNameOrPath), false);
    }

    /**
     * INTERNAL:
     * Lookup the {@link AttributeItem}for the provided attribute name or path.
     * 
     * @return item or null
     * @throws IllegalArgumentException if name is not valid attribute name or path
     */
    public AttributeItem addItem(String attributeName, AttributeItem item) {
        return this.items.put(attributeName, item);
    }

    /**
     * Locate the AttributeGroup where the leaf attribute in the path should be
     * applied to.
     * 
     * @param create
     *            indicates if intermediate AttributeGroup required within the
     *            specified path should be created as needed. When checking the
     *            state of the map callers should set this to false to avoid
     *            changing the state unexpectedly
     */
    protected AttributeItem getItem(String[] attributePath, boolean create) {
        AttributeItem item = null;
        AttributeGroup currentGroup = this;

        for (int index = 0; index < attributePath.length; index++) {
            String attrName = attributePath[index];

            item = currentGroup.getItems().get(attrName);

            // Add missing AttributeGroup
            if (item == null) {
                // If not creating missing AttributeGroups then return null
                if (!create) {
                    return null;
                }

                item = newItem(currentGroup, attrName);
                currentGroup.getItems().put(attrName, item);
            }

            // Add a AttributeGroup if not at the end of the attributes path
            if (item.getGroup() == null && index < (attributePath.length - 1)) {
                if (!create) {
                    return null;
                }
                //XXX-dclarke: Converting the attribute[] into a string and then re-parsing it seems odd
                AttributeGroup newGroup = newGroup(attrName, currentGroup);
                item.setRootGroup(newGroup);
            }

            currentGroup = item.getGroup();
        }

        return item;
    }

    /**
     * Remove an attribute from the group.
     */
    public void removeAttribute(String attributeNameOrPath) {
        AttributeItem item = getItem(attributeNameOrPath);
        if (item != null) {
            item.getParent().getItems().remove(item.getAttributeName());
        }
    }

    /**
     * Return true if this AttributeGroup is a super-set of the passed in
     * AttributeGroup.
     */
    public boolean isSupersetOf(AttributeGroup anotherGroup) {
        // TODO: should handle the case when the current group has all
        // attributes - then its equivalent to null
        if (anotherGroup == null) {
            return false;
        }
        if (anotherGroup != this) {
            if (hasItems()) {
                if (anotherGroup.hasItems()) {
                    Iterator<Map.Entry<String, AttributeItem>> otherItemEntries = anotherGroup.getItems().entrySet().iterator();
                    while (otherItemEntries.hasNext()) {
                        Map.Entry<String, AttributeItem> otherItemEntry = otherItemEntries.next();
                        String otherAttributeName = otherItemEntry.getKey();
                        AttributeItem item = this.items.get(otherAttributeName);
                        if (item == null) {
                            return false;
                        }
                        
                        AttributeGroup group = item.getGroup();
                        AttributeGroup otherGroup = otherItemEntry.getValue().getGroup();
                        if (group != null) {
                            if (!group.isSupersetOf(otherGroup)) {
                                return false;
                            }
                        } else {
                            if (otherGroup != null) {
                                return true;
                            }
                        }
                        group = item.getKeyGroup();
                        otherGroup = otherItemEntry.getValue().getKeyGroup();
                        if (group != null) {
                            if (!group.isSupersetOf(otherGroup)) {
                                return false;
                            }
                        } else {
                            if (otherGroup != null) {
                                return true;
                            }
                        }
                        if (item.getGroups() != null){
                            if (otherItemEntry.getValue().getGroups() == null){
                                return true;
                            }
                            for (AttributeGroup element : item.getGroups().values()){
                                AttributeGroup otherElement = otherItemEntry.getValue().getGroups().get(element.getType());
                                if (!element.isSupersetOf(otherElement)) {
                                    return false;
                                }
                            }
                        }
                        if (item.getKeyGroups() != null){
                            if (otherItemEntry.getValue().getKeyGroups() == null){
                                return true;
                            }
                            for (AttributeGroup element : item.getKeyGroups().values()){
                                AttributeGroup otherElement = otherItemEntry.getValue().getKeyGroups().get(element.getType());
                                if (!element.isSupersetOf(otherElement)) {
                                    return false;
                                }
                            }
                        }
                    }
                    return true;
                } else {
                    return true;
                }
            } else {
                if (anotherGroup.hasItems()) {
                    return false;
                } else {
                    return true;
                }
            }
        } else {
            return true;
        }
    }

    /**
     * Convert a provided name or path which could be a single attributeName, a
     * single string with dot separated attribute names, or an array of
     * attribute names defining the path.
     * 
     * @throws IllegalArgumentException if name is not valid attribute name or path
     */
    protected String[] convert(String... nameOrPath) {
        if (nameOrPath == null || nameOrPath.length == 0 || (nameOrPath.length == 1 && (nameOrPath[0] == null || nameOrPath[0].length() == 0))) {
            // TODO - improve error?
            throw new IllegalArgumentException("Inavlid name or path: " + (nameOrPath.length == 1 ? nameOrPath[0] : null));
        }

        String[] path = nameOrPath;
        if (nameOrPath.length > 1 || !nameOrPath[0].contains(".")) {
            path = nameOrPath;
        } else {
            if (nameOrPath[0].endsWith(".")) {
                throw new IllegalArgumentException("Invalid path: " + nameOrPath[0]);
            }
            path = nameOrPath[0].split("\\.");
        }

        if (path.length == 0) {
            throw new IllegalArgumentException("Invalid path: " + nameOrPath[0]);
        }

        for (int index = 0; index < path.length; index++) {
            if (path[index] == null || path[index].length() == 0 || !path[index].trim().equals(path[index])) {
                throw new IllegalArgumentException("Invalid path: " + nameOrPath[0]);
            }
        }
        return path;
    }

    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this Descriptor to actual class-based
     * settings. This method is used when converting a project that has been built
     * with class names to a project with classes.
     * @param classLoader 
     */
    public void convertClassNamesToClasses(ClassLoader classLoader){
        if (this.type == null){
            try{
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        this.type = (Class)AccessController.doPrivileged(new PrivilegedClassForName(this.typeName, true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(this.typeName, exception.getException());
                    }
                } else {
                    this.type = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(this.typeName, true, classLoader);
                }
            } catch (ClassNotFoundException exc){
                throw ValidationException.classNotFoundWhileConvertingClassNames(this.typeName, exc);
            }
            if (this.items != null){
                for (AttributeItem item : this.items.values()){
                    item.convertClassNamesToClasses(classLoader);
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this != obj) {
            if (obj == null) {
                return false;
            }
            AttributeGroup anotherGroup = null;
            try {
                anotherGroup = (AttributeGroup) obj;
            } catch (ClassCastException cce) {
                return false;
            }
            if (hasItems()) {
                if (anotherGroup.hasItems()) {
                    return getItems().equals(anotherGroup.getItems());
                } else {
                    return false;
                }
            } else {
                if (anotherGroup.hasItems()) {
                    return false;
                } else {
                    return true;
                }
            }
        } else {
            return true;
        }
    }

    public String toString() {
        return getClass().getSimpleName() + "(" + getName() + ")" + toStringAdditionalInfo() + "{" + toStringItems() + "}";
    }

    /**
     * Used by toString to print additional info for derived classes.
     */
    protected String toStringAdditionalInfo() {
        return "";
    }

    /**
     * Used by toString to print attribute items.
     */
    protected String toStringItems() {
        String str = "";
        if (this.items != null) {
            Iterator<AttributeItem> it = this.items.values().iterator();
            boolean isFirst = true;
            while (it.hasNext()) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    str += ", ";
                }
                str += it.next().toStringNoClassName();
            }
        }
        return str;
    }

    static protected String toStringPath(String[] attributePath, int position) {
        StringWriter writer = new StringWriter();
        for (int index = 0; index <= position; index++) {
            writer.write(attributePath[index]);
            if (index < position) {
                writer.write(".");
            }
        }
        return writer.toString();
    }

    public boolean isFetchGroup() {
        return false;
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
        cloneMap.put(this,clone);
        if (this.superClassGroup != null){
            clone.superClassGroup = this.superClassGroup.toFetchGroup(cloneMap);
        }
        if (this.subClasses != null){
            clone.subClasses = new ArrayList<AttributeGroup>();
            for (AttributeGroup group : this.subClasses){
                clone.subClasses.add(group.toFetchGroup(cloneMap));
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
            cloneMap.put(this,clone);
            
            if (this.superClassGroup != null){
                clone.superClassGroup = this.superClassGroup.toCopyGroup(cloneMap, copies);
            }
            if (this.subClasses != null){
                clone.subClasses = new ArrayList<AttributeGroup>();
                for (AttributeGroup group : this.subClasses){
                    clone.subClasses.add(group.toCopyGroup(cloneMap, copies));
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
        return toLoadGroup(new HashMap<AttributeGroup, LoadGroup>());
    }

    public LoadGroup toLoadGroup(Map<AttributeGroup, LoadGroup> cloneMap){
        LoadGroup clone = cloneMap.get(this);
        if (clone != null) {
            return clone;
        }
        clone = new LoadGroup(this.name);
        
        clone.type = this.type;
        clone.typeName = this.typeName;
        cloneMap.put(this,clone);
        if (this.superClassGroup != null){
            clone.superClassGroup = this.superClassGroup.toLoadGroup(cloneMap);
        }
        if (this.subClasses != null){
            clone.subClasses = new ArrayList<AttributeGroup>();
            for (AttributeGroup group : this.subClasses){
                clone.subClasses.add(group.toLoadGroup(cloneMap));
            }
        }
        // all attributes and nested groups should be cloned, too
        clone.items = null;
        if (hasItems()) {
            clone.items = new HashMap<String, AttributeItem>();
            for (AttributeItem item : this.items.values()){
                clone.items.put(item.getAttributeName(), item.toLoadGroup(cloneMap, clone));
            }
        }
        return clone;
    }

    public AttributeGroup clone() {
        Map<AttributeGroup, AttributeGroup> cloneMap = new IdentityHashMap<AttributeGroup, AttributeGroup>();
        return clone(cloneMap);
    }
    
    /**
     * INTERNAL:
     *    This method is used internally in the clone processing.
     * @param cloneMap
     * @return
     */
    
    public AttributeGroup clone(Map<AttributeGroup, AttributeGroup> cloneMap){
        AttributeGroup clone = cloneMap.get(this);
        if (clone != null) {
            return clone;
        }
        try {
            clone = (AttributeGroup) super.clone();
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        clone.name = this.name;
        clone.type = this.type;
        clone.typeName = this.typeName;
        cloneMap.put(this,clone);
        if (this.superClassGroup != null){
            clone.superClassGroup = this.superClassGroup.clone(cloneMap);
        }
        if (this.subClasses != null){
            clone.subClasses = new ArrayList<AttributeGroup>();
            for (AttributeGroup group : this.subClasses){
                clone.subClasses.add(group.clone(cloneMap));
            }
        }
        // all attributes and nested groups should be cloned, too
        clone.items = null;
        if (hasItems()) {
            clone.items = new HashMap<String, AttributeItem>();
            for (AttributeItem item : this.items.values()){
                clone.items.put(item.getAttributeName(), item.clone(cloneMap, clone));
            }
        }
        return clone;
    }
    
    /**
     * INTERNAL:
     * Only LoadGroups allow concurrency.
     */
    public boolean isConcurrent() {
        return false;
    }
    /**
     * This method will insert the group into the entity hierarchy just below this AttributeGroup.
     * @param group
     */
    public void insertSubClass(AttributeGroup group){
        group.superClassGroup = this;
        if (this.subClasses != null){
            for (Iterator<AttributeGroup> iterator = this.subClasses.iterator(); iterator.hasNext(); ){
                AttributeGroup subGroup = iterator.next();
                if(group.getType().isAssignableFrom(subGroup.getType())){
                    group.addSubClass(subGroup);
                    iterator.remove();
                }
            }
        }
        this.addSubClass(group);
    }

    protected void addSubClass(AttributeGroup subGroup) {
        if (this.subClasses == null){
            this.subClasses = new ArrayList<AttributeGroup>();
        }
        this.subClasses.add(subGroup);
        
    }
}
