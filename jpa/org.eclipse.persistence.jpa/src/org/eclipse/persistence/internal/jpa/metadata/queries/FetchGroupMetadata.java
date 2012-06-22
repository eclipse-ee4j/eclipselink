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
 *     01/19/2010-2.1 Guy Pelletier 
 *       - 211322: Add fetch-group(s) support to the EclipseLink-ORM.XML Schema
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.queries;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.descriptors.FetchGroupManager;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.queries.FetchGroup;

/**
 * INTERNAL:
 * Object to hold onto a named fetch group metadata.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.1
 */
public class FetchGroupMetadata extends ORMetadata {
    private List<FetchAttributeMetadata> m_fetchAttributes = new ArrayList<FetchAttributeMetadata>();
    private String m_name;
    private Boolean m_load;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public FetchGroupMetadata() {
        super("<fetch-group>");
        m_load = Boolean.FALSE;
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public FetchGroupMetadata(MetadataAnnotation fetchGroup, MetadataAccessor accessor) {
        super(fetchGroup, accessor);
        
        m_name = (String) fetchGroup.getAttribute("name");
        m_load = (Boolean) fetchGroup.getAttributeBooleanDefaultFalse("load");
         
        for (Object fetchAttribute : (Object[]) fetchGroup.getAttributeArray("attributes")) { 
            m_fetchAttributes.add(new FetchAttributeMetadata((MetadataAnnotation) fetchAttribute, accessor));
        }
    }
    
    /**
     * INTERNAL:
     * For merging and overriding to work properly, all ORMetadata must be able 
     * to compare themselves for metadata equality.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof FetchGroupMetadata) {
            FetchGroupMetadata fetchGroup = (FetchGroupMetadata) objectToCompare;
            
            if (! valuesMatch(m_name, fetchGroup.getName())) {
                return false;
            }

            if (! valuesMatch(m_load, fetchGroup.getLoad())) {
                return false;
            }
            
            return valuesMatch(m_fetchAttributes, fetchGroup.getFetchAttributes());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<FetchAttributeMetadata> getFetchAttributes() {
        return m_fetchAttributes;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getLoad() {
        return m_load;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * INTERNAL:
     */
    public void process(ClassAccessor accessor) {
        MetadataDescriptor descriptor = accessor.getDescriptor();
        
        FetchGroupManager fetchGroupManager;
        if (descriptor.getClassDescriptor().hasFetchGroupManager()) {
            fetchGroupManager = descriptor.getClassDescriptor().getFetchGroupManager();
        } else {
            fetchGroupManager = new FetchGroupManager();
            descriptor.getClassDescriptor().setFetchGroupManager(fetchGroupManager);
        }
        
        if (fetchGroupManager.hasFetchGroup(m_name)) {
            // We must be adding a fetch group from a mapped superclass.
            // Entity fetch groups are added first followed by those from
            // mapped superclasses. So if one already exists we need to ignore
            // it.
            getLogger().logConfigMessage(MetadataLogger.IGNORE_MAPPED_SUPERCLASS_FETCH_GROUP, descriptor.getJavaClass(), accessor.getJavaClass(), m_name);
        } else {
            FetchGroup fetchGroup = new FetchGroup();
            
            // Process the name of the fetch group.
            fetchGroup.setName(m_name);
            
            // Process all the attributes of the fetch group.
            for (FetchAttributeMetadata fetchAttribute : m_fetchAttributes) {
                fetchGroup.addAttribute(fetchAttribute.getName());
            }
        
            fetchGroupManager.addFetchGroup(fetchGroup);
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setFetchAttributes(List<FetchAttributeMetadata> attributes) {
        m_fetchAttributes = attributes;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setLoad(Boolean load) {
        m_load = load;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setName(String name) {
        m_name = name;
    }
}
