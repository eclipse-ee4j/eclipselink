/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     ailitchev - Bug 244124 - Added AttributeGroup base class for nesting 
 ******************************************************************************/
package org.eclipse.persistence.queries;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
public class AttributeGroup implements Serializable, Cloneable {

    /**
     * Name of the group. This is used in subclasses where the groups are stored
     * and can be used within a query by name as with FetchGroup. For dynamic
     * groups the name has no functional value.
     */
    private String name;

    /**
     * Specified attributes in the group mapped to their AttributeItems
     */
    protected Map<String, AttributeItem> items;

    public AttributeGroup(String name) {
        this.name = name;
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
        addAttribute(attributeNameOrPath, null);
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
        item.setGroup(group);
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
     * Locate the AttributeGroup where the leaf attribute in the path should be
     * applied to.
     * 
     * @param create
     *            indicates if intermediate AttributeGroup required within the
     *            specified path should be created as needed. When checking the
     *            state of the map callers should set this to false to avoid
     *            changing the state unexpectedly
     */
    private AttributeItem getItem(String[] attributePath, boolean create) {
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
                AttributeGroup newGroup = newGroup(toStringPath(attributePath, index), currentGroup);
                item.setGroup(newGroup);
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
        FetchGroup fetchGroup = new FetchGroup(getName());
        if (this.hasItems()) {
            Iterator<Map.Entry<String, AttributeItem>> it = getItems().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, AttributeItem> entry = it.next();
                AttributeGroup group = entry.getValue().getGroup();
                if (group == null) {
                    fetchGroup.addAttribute(entry.getKey());
                } else {
                    fetchGroup.addAttribute(entry.getKey(), group.toFetchGroup());
                }
            }
        }
        return fetchGroup;
    }

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
        CopyGroup copyGroup = new CopyGroup(getName());
        copyGroup.cascadeTree();
        if (this.hasItems()) {
            Iterator<Map.Entry<String, AttributeItem>> it = getItems().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, AttributeItem> entry = it.next();
                AttributeGroup group = entry.getValue().getGroup();
                if (group == null) {
                    copyGroup.addAttribute(entry.getKey());
                } else {
                    copyGroup.addAttribute(entry.getKey(), group.toCopyGroup());
                }
            }
        }
        return copyGroup;
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
        LoadGroup loadGroup = new LoadGroup(getName());
        if (this.hasItems()) {
            Iterator<Map.Entry<String, AttributeItem>> it = getItems().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, AttributeItem> entry = it.next();
                AttributeGroup group = entry.getValue().getGroup();
                if (group == null) {
                    loadGroup.addAttribute(entry.getKey());
                } else {
                    loadGroup.addAttribute(entry.getKey(), group.toLoadGroup());
                }
            }
        }
        return loadGroup;
    }

    public AttributeGroup clone() {
        AttributeGroup clone;
        try {
            clone = (AttributeGroup) super.clone();
        } catch (CloneNotSupportedException ex) {
            // should never happen
            throw new InternalError();
        }

        // all attributes and nested groups should be cloned, too
        clone.items = null;
        if (hasItems()) {
            Iterator<Map.Entry<String, AttributeItem>> it = getItems().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, AttributeItem> entry = it.next();
                AttributeGroup group = entry.getValue().getGroup();
                if (group != null) {
                    clone.addAttribute(entry.getKey(), group.clone());
                } else {
                    clone.addAttribute(entry.getKey());
                }
            }
        }

        return clone;
    }
}
