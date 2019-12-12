/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Matt MacIvor - 2.5 - initial implementation
package org.eclipse.persistence.core.queries;

import java.io.Serializable;
import java.io.StringWriter;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.core.descriptors.CoreDescriptor;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.core.queries.CoreAttributeConverter;
import org.eclipse.persistence.internal.helper.StringHelper;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;

/**
 * INTERNAL
 * <b>Purpose</b>: A generic super class for AttributeGroup and other subclasses
 *
 * @see org.eclipse.persistence.queries.AttributeGroup

 * @author Matt MacIvor
 * @since EclipseLink 2.5
 */
public class CoreAttributeGroup<
    ATTRIBUTE_ITEM extends CoreAttributeItem,
    DESCRIPTOR extends CoreDescriptor> implements Serializable, Cloneable {

    /**
     *  Name parts separator. Used in {@link #toStringItems()} method to build output string.
     */
    private static final String FIELD_SEP = ", ";

    private int toStringLoopCount = 0;

    /**
     * Name of the group. This is used in subclasses where the groups are stored
     * and can be used within a query by name as with FetchGroup. For dynamic
     * groups the name has no functional value.
     */
    protected String name;

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
    protected CoreAttributeGroup superClassGroup;

    /**
     * This attribute references the immediate subclass groups for this attributeGroup.  This is not required but acts
     * as a means to support adding inheritance branches into the an established tree.
     */
    protected transient Set<CoreAttributeGroup> subClasses;

    /**
     * This attribute is used to store all of the classes in this hierarchy keyed by type.  It is used to find the correct graph
     * for polymorphic groups.
     */
    protected Map<Object, CoreAttributeGroup> allsubclasses;

    /**
     * Specified attributes in the group mapped to their AttributeItems
     */
    protected Map<String, ATTRIBUTE_ITEM> items;

    /**
     * Marks this AttributeGroup as having been validated by the builder and does not require further validation
     */
    protected boolean isValidated;

    public CoreAttributeGroup(String name) {
        this.name = name;
    }

    /*
     * INTERNAL:
     * Used to create an attribute with a name of the class type.
     */
    public CoreAttributeGroup(String name, String type, boolean isValidated) {
        this(name);
        this.typeName = type;
        this.isValidated = isValidated;

    }

    /**
     * INTERNAL:
     * This constructer is to only be used by EclipseLink internally
     * @param name
     * @param type
     */
    public CoreAttributeGroup(String name, Class type, boolean isValidated) {
        this(name);
        this.type = type;
        this.isValidated = isValidated;

    }

    public CoreAttributeGroup() {
        this("");
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
     * @param attributeNameOrPath
     *            A simple attribute, array or attributes forming a path
     */
    public void addAttribute(String attributeNameOrPath) {
        addAttribute(attributeNameOrPath, (CoreAttributeGroup)null);
    }

    /**
     * Add an attribute and the corresponding list of AttributeGroups.
     * Multiple groups are added in the case of inheritance
     * <p>
     * @param attributeNameOrPath
     *            A simple attribute, array or attributes forming a path
     * @param groups - a collection of AttributeGroups to be added.
     */
    public void addAttribute(String attributeNameOrPath, Collection<? extends CoreAttributeGroup> groups) {
        CoreAttributeItem item = getItem(CoreAttributeConverter.convert(attributeNameOrPath), true);
        item.addGroups(groups);
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
     * @param attributeNameOrPath
     *            A simple attribute, array or attributes forming a path
     * @param group - an AttributeGroup to be added.
     */
    public void addAttribute(String attributeNameOrPath, CoreAttributeGroup group) {
        CoreAttributeItem item = getItem(CoreAttributeConverter.convert(attributeNameOrPath), true);
        item.addSubGroup(group);
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
     * @param attributeNameOrPath
     *            A simple attribute, array or attributes forming a path to a
     *            Map key
     * @param group
     *            - an AttributeGroup to be added.
     */
    public void addAttributeKey(String attributeNameOrPath, CoreAttributeGroup group) {
        CoreAttributeItem item = getItem(CoreAttributeConverter.convert(attributeNameOrPath), true);
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

    @Override
    public CoreAttributeGroup clone() {
       Map<CoreAttributeGroup<ATTRIBUTE_ITEM, DESCRIPTOR>, CoreAttributeGroup<ATTRIBUTE_ITEM, DESCRIPTOR>> cloneMap = new IdentityHashMap<CoreAttributeGroup<ATTRIBUTE_ITEM, DESCRIPTOR>, CoreAttributeGroup<ATTRIBUTE_ITEM, DESCRIPTOR>>();
       return clone(cloneMap);
    }

    /**
     * INTERNAL:
     *    This method is used internally in the clone processing.
     * @param cloneMap
     * @return
     */
    public CoreAttributeGroup clone(Map<CoreAttributeGroup<ATTRIBUTE_ITEM, DESCRIPTOR>, CoreAttributeGroup<ATTRIBUTE_ITEM, DESCRIPTOR>> cloneMap){
        CoreAttributeGroup clone = cloneMap.get(this);
        if (clone != null) {
            return clone;
        }
        try {
            clone = (CoreAttributeGroup) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
        clone.name = this.name;
        clone.type = this.type;
        clone.typeName = this.typeName;
        clone.isValidated = this.isValidated;
        cloneMap.put(this,clone);
        if (this.allsubclasses != null){
            for (CoreAttributeGroup group : this.allsubclasses.values()){
                clone.getSubClassGroups().put(group.getType(), group.clone(cloneMap));
            }
        }
        if (this.superClassGroup != null){
            clone.superClassGroup = this.superClassGroup.clone(cloneMap);
        }
        if (this.subClasses != null){
            clone.subClasses = new HashSet<CoreAttributeGroup>();
            for (CoreAttributeGroup group : this.subClasses){
                clone.subClasses.add(group.clone(cloneMap));
            }
        }
        // all attributes and nested groups should be cloned, too
        clone.items = null;
        if (hasItems()) {
            clone.items = new HashMap<String, ATTRIBUTE_ITEM>();
            for (ATTRIBUTE_ITEM item : this.items.values()){
                clone.items.put(item.getAttributeName(), item.clone(cloneMap, clone));
            }
        }
        return clone;
    }

    /**
     * Return if the attribute is defined in the group.
     */
    public boolean containsAttribute(String attributeNameOrPath) {
        String[] path = CoreAttributeConverter.convert(attributeNameOrPath);

        if (getItem(path, false) != null){
            return true;
        }
        if (this.hasInheritance() && this.superClassGroup != null){
            return this.superClassGroup.containsAttribute(attributeNameOrPath);
        }
        return false;
    }

    /**
     * INTERNAL:
     * Return if the attribute is defined in the group.
     * Only local attribute names are checked.
     */
    public boolean containsAttributeInternal(String attributeName) {
        if (this.items != null && this.items.containsKey(attributeName)){
            return true;
        }
        if (this.hasInheritance() && this.superClassGroup != null){
            return this.superClassGroup.containsAttributeInternal(attributeName);
        }
        return false;
    }

    /**
     * Convert a provided name or path which could be a single attributeName, a
     * single string with dot separated attribute names, or an array of
     * attribute names defining the path.
     * @throws IllegalArgumentException if name is not valid attribute name or path.
     */
    // Old prototype to keep 2.5 API. Use CoreAttributeConverter.convert internally.
    protected String[] convert(String... nameOrPath) {
        return CoreAttributeConverter.convert(nameOrPath);
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
                        this.type = AccessController.doPrivileged(new PrivilegedClassForName(this.typeName, true, classLoader));
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
                for (ATTRIBUTE_ITEM item : this.items.values()){
                    item.convertClassNamesToClasses(classLoader);
                }
            }
            if (this.allsubclasses != null){
                Map<Object, CoreAttributeGroup> allGroups = new HashMap<Object, CoreAttributeGroup>();
                this.subClasses = new HashSet<CoreAttributeGroup>();
                for (CoreAttributeGroup subClass : allsubclasses.values()){
                    subClass.convertClassNamesToClasses(classLoader);
                    allGroups.put(subClass.getType(), subClass);
                }
                this.allsubclasses = allGroups;
                for (CoreAttributeGroup subClass : allsubclasses.values()){
                    if (CoreAttributeItem.orderInheritance(subClass, allGroups)){
                        this.insertSubClass(subClass);
                    }
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
            CoreAttributeGroup anotherGroup = null;
            try {
                anotherGroup = (CoreAttributeGroup) obj;
            } catch (ClassCastException cce) {
                return false;
            }
            if (hasItems()) {
                if (anotherGroup.hasItems()) {
                    if (!getItems().equals(anotherGroup.getItems())){
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (anotherGroup.hasItems()) {
                    return false;
                }
            }
            if (this.superClassGroup != null){
                if (anotherGroup.superClassGroup != null){
                    return this.superClassGroup.equals(anotherGroup.superClassGroup);
                }else{
                    return false;
                }
            }else{
                if (anotherGroup.superClassGroup != null){
                    return false;
                }
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public int hashCode() {
        int result = superClassGroup != null ? superClassGroup.hashCode() : 0;
        for (String attribute : getItems().keySet()) {
            // avoid processing values in getItems map to avoid infinite recursion
            // as ATTRIBUTE_ITEM.hashCode() calls group.hashCode()
            result = 31 * result + attribute.hashCode();
        }
        return 31 * result;
    }

    public CoreAttributeGroup findGroup(DESCRIPTOR type){
        if (this.type == null || this.type.equals(type.getJavaClass())){
            return this;
        }
        if (this.hasInheritance()){
            CoreAttributeGroup result = getSubClassGroups().get(type.getJavaClass());
            while (result == null && type.getInheritancePolicy().getParentDescriptor() != null){
                type = (DESCRIPTOR) type.getInheritancePolicy().getParentDescriptor();
                result = getSubClassGroups().get(type.getJavaClass());
            }
            if (result != null){
                return result;
            }
        }
        return this;
    }

    /**
     * INTERNAL:
     */
    public Map<String, ATTRIBUTE_ITEM> getAllItems() {
        Map<String, ATTRIBUTE_ITEM> allItems = new HashMap<String, ATTRIBUTE_ITEM>();
        if (this.superClassGroup != null){
            allItems.putAll(this.superClassGroup.getAllItems());
        }
        allItems.putAll(getItems());
        return allItems;
    }

    public Set<String> getAttributeNames() {
        Set<String> attributes = new HashSet<String>();

        if (this.superClassGroup != null && this.superClassGroup != this){
            attributes.addAll(this.superClassGroup.getAttributeNames());
        }
        attributes.addAll(getItems().keySet());
        return attributes;
    }

    /**
     * Returns AttributeGroup corresponding to the passed (possibly nested)
     * attribute.
     */
    public CoreAttributeGroup getGroup(String attributeNameOrPath) {
        CoreAttributeItem item = getItem(CoreAttributeConverter.convert(attributeNameOrPath), false);
        if (item != null) {
            return item.getGroup();
        }
        if (hasInheritance()){
            return this.superClassGroup.getGroup(attributeNameOrPath);
        }
        return null;
    }

    /**
     * INTERNAL:
     * Lookup the {@link org.eclipse.persistence.internal.queries.AttributeItem AttributeItem} for the provided attribute name or path.
     *
     * @return item or null
     * @throws IllegalArgumentException if name is not valid attribute name or path
     */
    public ATTRIBUTE_ITEM getItem(String attributeNameOrPath) {
        return getItem(CoreAttributeConverter.convert(attributeNameOrPath), false);
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
    protected ATTRIBUTE_ITEM getItem(String[] attributePath, boolean create) {
        ATTRIBUTE_ITEM item = null;
        CoreAttributeGroup<ATTRIBUTE_ITEM, DESCRIPTOR> currentGroup = this;

        for (int index = 0; index < attributePath.length; index++) {
            String attrName = attributePath[index];

            item = currentGroup.getItems().get(attrName);

            // Add missing AttributeGroup
            if (item == null) {
                // If not creating missing AttributeGroups then return null
                if (!create) {
                    if (this.superClassGroup != null){
                        return (ATTRIBUTE_ITEM)this.superClassGroup.getItem(attributePath, create);
                    }
                    return null;
                }

                item = (ATTRIBUTE_ITEM)newItem(currentGroup, attrName);
                currentGroup.getItems().put(attrName, item);
            }

            // Add a AttributeGroup if not at the end of the attributes path
            if (item.getGroup() == null && index < (attributePath.length - 1)) {
                if (!create) {
                    return null;
                }
                //XXX-dclarke: Converting the attribute[] into a string and then re-parsing it seems odd
                CoreAttributeGroup newGroup = newGroup(attrName, currentGroup);
                item.setRootGroup(newGroup);
            }

            currentGroup = item.getGroup();
        }

        return item;
    }

    /**
     * INTERNAL:
     * @return Non-null Map of attributes in the group mapped to their AttributeItems
     */
    public Map<String, ATTRIBUTE_ITEM> getItems() {
        if (this.items == null) {
            this.items = new HashMap();
        }
        return this.items;
    }

    public String getName() {
        return this.name;
    }

    /**
     * INTERNAL:
     */
    public Map<Object, CoreAttributeGroup> getSubClassGroups(){
        if (this.allsubclasses == null){
            this.allsubclasses = new HashMap<Object, CoreAttributeGroup>();
        }
        return this.allsubclasses;
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

    /**
     * Indicates whether this group is part of an inheritance hierarchy
     */
    public boolean hasInheritance(){
        return (this.subClasses != null && !this.subClasses.isEmpty()) || (this.superClassGroup != null);
    }

    /**
     * Indicates whether the group has at least one attribute.
     */
    public boolean hasItems() {
        return this.items != null && !this.items.isEmpty();
    }

    /**
     * INTERNAL:
     * This method will insert the group into the entity hierarchy just below this AttributeGroup.
     * @param group
     */
    public void insertSubClass(CoreAttributeGroup group){
        if (this == group){
            return;
        }
        group.superClassGroup = this;
        if (subClasses != null){
            for (Iterator<CoreAttributeGroup> subClasses = this.subClasses.iterator(); subClasses.hasNext();){
                CoreAttributeGroup subClass = subClasses.next();
                if (group != subClass && group.getType().isAssignableFrom(subClass.getType())){
                    group.subClasses.add(subClass);
                    subClass.superClassGroup = group;
                    subClasses.remove();
                }
            }
        }else{
            this.subClasses = new HashSet<CoreAttributeGroup>();
        }
        this.subClasses.add(group);
    }

    /**
     * INTERNAL:
     * Only LoadGroups allow concurrency.
     */
    public boolean isConcurrent() {
        return false;
    }

    /**
     * INTERNAL:
     *    This method is used internally when converting to a copy group.
     * @return
     */
    public boolean isCopyGroup() {
        return false;
    }

    public boolean isFetchGroup() {
        return false;
    }

    public boolean isLoadGroup() {
        return false;
    }

    /**
     * Return true if this AttributeGroup is a super-set of the passed in
     * AttributeGroup.
     */
    public boolean isSupersetOf(CoreAttributeGroup<ATTRIBUTE_ITEM, DESCRIPTOR> anotherGroup) {
        // TODO: should handle the case when the current group has all
        // attributes - then its equivalent to null
        if (anotherGroup == null) {
            return false;
        }
        if (anotherGroup != this) {
            if (hasItems()) {
                if (anotherGroup.hasItems()) {
                    Iterator<Map.Entry<String, ATTRIBUTE_ITEM>> otherItemEntries = anotherGroup.getItems().entrySet().iterator();
                    while (otherItemEntries.hasNext()) {
                        Map.Entry<String, ATTRIBUTE_ITEM> otherItemEntry = otherItemEntries.next();
                        String otherAttributeName = otherItemEntry.getKey();
                        CoreAttributeItem item = this.items.get(otherAttributeName);
                        if (item == null) {
                            return false;
                        }
                        CoreAttributeGroup<ATTRIBUTE_ITEM, DESCRIPTOR> group = item.getGroup();
                        CoreAttributeGroup<ATTRIBUTE_ITEM, DESCRIPTOR> otherGroup = otherItemEntry.getValue().getGroup();
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
                            for (Object next : item.getGroups().values()){
                                CoreAttributeGroup<ATTRIBUTE_ITEM, DESCRIPTOR> element = (CoreAttributeGroup<ATTRIBUTE_ITEM, DESCRIPTOR>)next;
                                CoreAttributeGroup<ATTRIBUTE_ITEM, DESCRIPTOR> otherElement = (CoreAttributeGroup<ATTRIBUTE_ITEM, DESCRIPTOR>)otherItemEntry.getValue().getGroups().get(element.getType());
                                if (!element.isSupersetOf(otherElement)) {
                                    return false;
                                }
                            }
                        }
                        if (item.getKeyGroups() != null){
                            if (otherItemEntry.getValue().getKeyGroups() == null){
                                return true;
                            }
                            for (Object next : item.getKeyGroups().values()){
                                CoreAttributeGroup element = (CoreAttributeGroup)next;
                                CoreAttributeGroup otherElement = (CoreAttributeGroup)otherItemEntry.getValue().getKeyGroups().get(element.getType());
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
     * INTERNAL:
     * @return the isValidated
     */
    public boolean isValidated() {
        return isValidated;
    }

    /**
     * Subclass may create different types.
     */
    protected CoreAttributeGroup newGroup(String name, CoreAttributeGroup parent) {
        return new CoreAttributeGroup<ATTRIBUTE_ITEM, DESCRIPTOR>(name);
    }

    /**
     * Subclass may create different types.
     */
    protected CoreAttributeItem newItem(CoreAttributeGroup group, String attrName) {
        return new CoreAttributeItem(group, attrName);
    }

    /**
     * Remove an attribute from the group.
     */
    public void removeAttribute(String attributeNameOrPath) {
        CoreAttributeItem item = getItem(attributeNameOrPath);
        if (item != null) {
            item.getParent().getItems().remove(item.getAttributeName());
        }
    }

    /**
     * INTERNAL:
     *
     */
    public void setAllSubclasses(Map<Object, CoreAttributeGroup> subclasses){
        this.allsubclasses = subclasses;
    }

    // XXX-dclarke: Should this be public?
    public void setAttributeNames(Set attributeNames) {
        Iterator it = attributeNames.iterator();
        while (it.hasNext()) {
            this.addAttribute((String) it.next());
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    //changed for EclipseLink 415779 to avoid stack overflows when using graphs with circular references
    @Override
    public String toString() {
        String className = StringHelper.nonNullString(getClass().getSimpleName());
        String name = StringHelper.nonNullString(getName());
        if (toStringLoopCount >1) {
            return className+StringHelper.LEFT_BRACKET+name+ " Loop detected "+ StringHelper.RIGHT_BRACKET;
        }
        try {
            toStringLoopCount++;
            String items = StringHelper.nonNullString(toStringItems());
            String additionalInfo = StringHelper.nonNullString(toStringAdditionalInfo());
            StringBuilder str = new StringBuilder(className.length() + name.length()
                    + additionalInfo.length() + items.length() + 4);
            str.append(className);
            str.append(StringHelper.LEFT_BRACKET).append(name).append(StringHelper.RIGHT_BRACKET);
            str.append(additionalInfo);
            str.append(StringHelper.LEFT_BRACE).append(items).append(StringHelper.RIGHT_BRACE);
            return str.toString();
        } finally {
            toStringLoopCount--;
        }
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
        // Calculate name length to avoid StringBuilder resizing
        int length = 0;
        String superClassGroupItems;
        if (this.superClassGroup != null) {
            superClassGroupItems = this.superClassGroup.toStringItems();
            length += FIELD_SEP.length();
            length += superClassGroupItems.length();
        } else {
            superClassGroupItems = null;
        }
        Collection<ATTRIBUTE_ITEM> values = null;
        if (this.items != null) {
            values = this.items.values();
            length += (values != null && values.size() > 0
                    ?  (values.size() - 1) * FIELD_SEP.length() : 0);
            if (values != null) {
                for (Iterator<ATTRIBUTE_ITEM> it = values.iterator(); it.hasNext();) {
                    length += it.next().toStringNoClassName().length();
                }
            }
        }
        // Build string to be returned
        StringBuilder str = new StringBuilder(length > 0 ? length : 0);
        if (values != null) {
            for (Iterator<ATTRIBUTE_ITEM> it = values.iterator(); it.hasNext();) {
                str.append(it.next().toStringNoClassName());
                if (it.hasNext()) {
                    str.append(FIELD_SEP);
                }
            }
        }
        if (this.superClassGroup != null) {
            str.append(FIELD_SEP);
            str.append(superClassGroupItems);
        }
        return str.toString();
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
}
