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
 ******************************************************************************/  
package org.eclipse.persistence.queries;

import java.util.*;

/**
 * <p><b>Purpose</b>: A fetch group is a performance enhancement that allows a group of
 * attributes of an object to be loaded on demand, which means that the data for an attribute
 * might not loaded from the underlying data source until an explicit access call for the
 * attribute first occurs. It avoids the wasteful practice of loading up all data of the object's
 * attributes, in which the user is interested in only partial of them.
 * <p>
 * A great deal of caution and careful system use case analysis should be use when using the fetch
 * group feature, as the extra round-trip would well offset the gain from the deferred loading in
 * many cases.
 * <p>
 * EclipseLink fetch group support is twofold: the pre-defined fetch groups at the descriptor level; and
 * dynamic (use case) fetch groups at the query level.
 * <p>
 * Every query can has at most one fetch group. There is an optional pre-defined default fetch group
 * at the descriptor level. If set, and the query has no fetch group being set, the default fetch group
 * would be used, unless query.setShouldUseDefaultFetchGroup(false) is also called. In the latter case,
 * the full object will be fetched after the query execution.
 *
 * @see org.eclipse.persistence.queries.FetchGroup
 * @see org.eclipse.persistence.queries.FetchGroupTracker
 *
 * @author King Wang
 * @since TopLink 10.1.3.
 */
public class FetchGroup implements java.io.Serializable {
    /** Fetch group name, default is empty if not set. */
    private String name;

    /** Specified attributes in the group. */
    private Set attributes;

    /**
     * Constructor.
     */
    public FetchGroup() {
        this("");
    }

    /**
     * Constructor with a group name.
     */
    public FetchGroup(String name) {
        this.name = name;
        this.attributes = new HashSet(10);
    }

    /**
     * Return if the attribute is defined in the group.
     */
    public boolean containsAttribute(String attribute) {
        return getAttributes().contains(attribute);
    }

    /**
     * Return attributes defined in the group.
     */
    public Set getAttributes() {
        return attributes;
    }

    /**
     * Add an attribute to the group.
     */
    public synchronized void addAttribute(String attrName) {
        // Synchronized because pk attributes are added in query prepare.
        attributes.add(attrName);
    }

    /**
     * Add a set of attributes to the group.
     */
    public void addAttributes(Collection newAttributes) {
        attributes.addAll(newAttributes);
    }

    /**
     * Remove an attribute from the group.
     */
    public void removeAttribute(String attrName) {
        attributes.remove(attrName);
    }

    /**
     * Return the group name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the group name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * INTERNAL:
     * Return true if this fetch group is a super-set of the passed in fetch group.
     */
    public boolean isSupersetOf(FetchGroup anotherGroup) {
        return (anotherGroup != null) && ((this == anotherGroup) || getAttributes().containsAll(anotherGroup.getAttributes()));
    }
}
