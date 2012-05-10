/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     11/06/2009-2.0 Guy Pelletier 
 *       - 286317: UniqueConstraint xml element is changing (plus couple other fixes, see bug)
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 *     07/03/2011-2.3.1 Guy Pelletier 
 *       - 348756: m_cascadeOnDelete boolean should be changed to Boolean
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.mappings;

import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_CASCADE_ALL;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_CASCADE_DETACH;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_CASCADE_MERGE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_CASCADE_PERSIST;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_CASCADE_REFRESH;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_CASCADE_REMOVE;

/**
 * INTERNAL:
 * Object to represent the cascade types specified for a relationship
 * mapping element.
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
 * @since EclipseLink 1.0
 */
public class CascadeMetadata extends ORMetadata {
    private Boolean m_cascadeAll;
    private Boolean m_cascadePersist;
    private Boolean m_cascadeMerge;
    private Boolean m_cascadeRemove;
    private Boolean m_cascadeRefresh;
    private Boolean m_cascadeDetach;
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public CascadeMetadata() {
        super("<cascade>");
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public CascadeMetadata(Object[] cascadeTypes, MetadataAccessor accessor) {
        super(null, accessor);
        
        for (Object cascadeType : cascadeTypes) {
            if (cascadeType.equals(JPA_CASCADE_ALL)) {
                m_cascadeAll = true;
            } else if (cascadeType.equals(JPA_CASCADE_PERSIST)) {
                m_cascadePersist = true;
            } else if (cascadeType.equals(JPA_CASCADE_MERGE)) {
                m_cascadeMerge = true;
            } else if (cascadeType.equals(JPA_CASCADE_REMOVE)) {
                m_cascadeRemove = true;
            } else if (cascadeType.equals(JPA_CASCADE_REFRESH)) {
                m_cascadeRefresh = true; 
            } else if (cascadeType.equals(JPA_CASCADE_DETACH)) {
                m_cascadeDetach = true;
            }
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof CascadeMetadata) {
            CascadeMetadata cascade = (CascadeMetadata) objectToCompare;
            
            if (! valuesMatch(m_cascadeAll, cascade.getCascadeAll())) {
                return false;
            }
            
            if (! valuesMatch(m_cascadePersist, cascade.getCascadePersist())) {
                return false;
            }
            
            if (! valuesMatch(m_cascadeMerge, cascade.getCascadeMerge())) {
                return false;
            }
            
            if (! valuesMatch(m_cascadeRemove, cascade.getCascadeRemove())) {
                return false;
            }
            
            if (! valuesMatch(m_cascadeRefresh, cascade.getCascadeRefresh())) {
                return false;
            }
           
            return valuesMatch(m_cascadeDetach, cascade.getCascadeDetach());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getCascadeAll() {
        return m_cascadeAll;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getCascadeDetach() {
        return m_cascadeDetach;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getCascadeMerge() {
        return m_cascadeMerge;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getCascadePersist() {
        return m_cascadePersist;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getCascadeRefresh() {
        return m_cascadeRefresh;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getCascadeRemove() {
        return m_cascadeRemove;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public boolean isCascadeAll() {
        return m_cascadeAll != null && m_cascadeAll.booleanValue();
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public boolean isCascadeDetach() {
        return m_cascadeDetach != null && m_cascadeDetach.booleanValue();
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public boolean isCascadeMerge() {
        return m_cascadeMerge != null && m_cascadeMerge.booleanValue();
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public boolean isCascadePersist() {
        return m_cascadePersist != null && m_cascadePersist.booleanValue();
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public boolean isCascadeRefresh() {
        return m_cascadeRefresh != null && m_cascadeRefresh.booleanValue();
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public boolean isCascadeRemove() {
        return m_cascadeRemove != null && m_cascadeRemove.booleanValue();
    }
    
    /**
     * INTERNAL:
     * Process the cascade types for the given mapping.
     */
    public void process(ForeignReferenceMapping mapping) {
        if (isCascadeAll()) {
            mapping.setCascadeAll(true);
        } 
        
        if (isCascadeDetach()) {
            mapping.setCascadeDetach(true);
        }
        
        if (isCascadeMerge()) {
            mapping.setCascadeMerge(true);
        } 
            
        if (isCascadePersist()) {
            mapping.setCascadePersist(true);
        } 
        
        if (isCascadeRefresh()) {
            mapping.setCascadeRefresh(true);
        }
        
        if (isCascadeRemove()) {
            mapping.setCascadeRemove(true);
        } 
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCascadeAll(Boolean cascadeAll) {
        m_cascadeAll = cascadeAll;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCascadeDetach(Boolean cascadeDetach) {
        m_cascadeDetach = cascadeDetach;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCascadeMerge(Boolean cascadeMerge) {
        m_cascadeMerge = cascadeMerge;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCascadePersist(Boolean cascadePersist) {
        m_cascadePersist = cascadePersist;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCascadeRefresh(Boolean cascadeRefresh) {
        m_cascadeRefresh = cascadeRefresh;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCascadeRemove(Boolean cascadeRemove) {
        m_cascadeRemove = cascadeRemove;
    }    
}
