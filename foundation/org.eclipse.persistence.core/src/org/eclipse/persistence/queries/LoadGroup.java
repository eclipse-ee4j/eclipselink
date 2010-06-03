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

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;

import org.eclipse.persistence.internal.descriptors.DescriptorIterator;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.queries.AttributeItem;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;

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
    
    public void load(Object object, AbstractSession session) {
        DescriptorIterator iterator = new DescriptorIterator() {
            public void iterate(Object object) {
                if(this.getCurrentGroup() != null) {
                    ObjectBuilder builder = this.getCurrentDescriptor().getObjectBuilder();
                    Iterator<String> it = this.getCurrentGroup().getItems().keySet().iterator();
                    while(it.hasNext()) {
                        DatabaseMapping mapping = builder.getMappingForAttributeName(it.next());
                        // instantiate indirection
                        mapping.instantiateAttribute(object, session);
                    }
                }
            }
        };
        iterator.setSession(session);
        iterator.setVisitedObjects(new IdentityHashMap());
        iterator.setShouldTrackCurrentGroup(true);
        
        if(object instanceof Collection) {
            Iterator it = ((Collection)object).iterator();
            while(it.hasNext()) {
                iterator.startIterationOn(it.next(), this);
            }
        } else {
            iterator.startIterationOn(object, this);
        }
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
    public AttributeItem addAttribute(String attributeNameOrPath, AttributeGroup group) {
        return super.addAttribute(attributeNameOrPath, (group != null ? group.toLoadGroup() : null));
    }

    public AttributeItem addAttribute(String attributeNameOrPath, LoadGroup group) {
        return super.addAttribute(attributeNameOrPath, group);
    }
 }
