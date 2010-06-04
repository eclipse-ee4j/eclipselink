/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     ailitchev - Bug 244124 - New support for loading 
 ******************************************************************************/  
package org.eclipse.persistence.queries;

/**
 * <b>Purpose</b>: Used to load specified relationship attributes and nested
 * relationship attributes.
 * <p>
 * A LoadGroup can be configured for use on a query using
 * {@link ObjectLevelReadQuery#setLoadGroup(LoadGroup)} or in the case of JPA
 * users with LOAD_GROUP query hint. Alternatively a {@link FetchGroup} could be
 * used with {@link FetchGroup#shouldLoad()} set to true and the FetchGroup
 * configured on a query be executed.
 * 
 * @see FetchGroup
 * 
 * @author ailitchev
 * @since Eclipselink 2.1
 */
public class LoadGroup extends AttributeGroup {

    public LoadGroup() {
        super();
    }

    public LoadGroup(String name) {
        super(name);
    }

    @Override
    protected LoadGroup newGroup(String name, AttributeGroup parent) {
        return new LoadGroup(name);
    }
    
    @Override
    public boolean isLoadGroup() {
        return true;
    }
    
    @Override
    public LoadGroup clone() {
        return (LoadGroup)super.clone();
    }

    /**
     * Returns LoadGroup corresponding to the passed (possibly nested) attribute.
     */
    @Override
    public LoadGroup getGroup(String attributeNameOrPath) {
        return (LoadGroup)super.getGroup(attributeNameOrPath);
    }

    @Override
    public void addAttribute(String attributeNameOrPath, AttributeGroup group) {
        super.addAttribute(attributeNameOrPath, (group != null ? group.toLoadGroup() : null));
    }

    public void addAttribute(String attributeNameOrPath, LoadGroup group) {
        super.addAttribute(attributeNameOrPath, group);
    }
 }
