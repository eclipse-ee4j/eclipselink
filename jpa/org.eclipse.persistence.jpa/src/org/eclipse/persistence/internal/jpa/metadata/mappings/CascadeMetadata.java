/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.mappings;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;

import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

/**
 * INTERNAL:
 * Object to represent the cascade types specified for a relationship
 * mapping element.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class CascadeMetadata extends ORMetadata {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.

    private boolean m_cascadeAll;
    private boolean m_cascadePersist;
    private boolean m_cascadeMerge;
    private boolean m_cascadeRemove;
    private boolean m_cascadeRefresh;
    
    private List<String> m_types;
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public CascadeMetadata() {
        super("<cascade>");
    }
    
    /**
     * INTERNAL:
     */
    public CascadeMetadata(Object[] cascadeTypes, MetadataAccessibleObject accessibleObject) {
        super(null, accessibleObject);
        
        m_types = new ArrayList<String>();
        
        for (Object cascadeType : cascadeTypes) {
            m_types.add((String)cascadeType);
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof CascadeMetadata) {
            CascadeMetadata accessMethods = (CascadeMetadata) objectToCompare;
            
            if (! valuesMatch(m_cascadeAll, accessMethods.isCascadeAll())) {
                return false;
            }
            
            if (! valuesMatch(m_cascadePersist, accessMethods.isCascadePersist())) {
                return false;
            }
            
            if (! valuesMatch(m_cascadeMerge, accessMethods.isCascadeMerge())) {
                return false;
            }
            
            if (! valuesMatch(m_cascadeRemove, accessMethods.isCascadeRemove())) {
                return false;
            }
            
            return valuesMatch(m_cascadeRefresh, accessMethods.isCascadeRefresh());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getCascadeAll() {
        return null;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getCascadeMerge() {
        return null;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getCascadePersist() {
        return null;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getCascadeRefresh() {
        return null;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getCascadeRemove() {
        return null;
    }
    
    /**
     * INTERNAL:
     */
    public List<String> getTypes() {
        if (m_types == null) {
            m_types = new ArrayList<String>();
        
            if (isCascadeAll()) {
                m_types.add(CascadeType.ALL.name());
            }
        
            if (isCascadePersist()) {
                m_types.add(CascadeType.PERSIST.name());
            }
        
            if (isCascadeMerge()) {
                m_types.add(CascadeType.MERGE.name());
            }
        
            if (isCascadeRemove()) {
                m_types.add(CascadeType.REMOVE.name());
            }
        
            if (isCascadeRefresh()) {
                m_types.add(CascadeType.REFRESH.name());
            }
        }
        
        return m_types;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public boolean isCascadeAll() {
        return m_cascadeAll;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public boolean isCascadeMerge() {
        return m_cascadeMerge;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public boolean isCascadePersist() {
        return m_cascadePersist;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public boolean isCascadeRefresh() {
        return m_cascadeRefresh;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public boolean isCascadeRemove() {
        return m_cascadeRemove;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCascadeAll(String ignore) {
        m_cascadeAll = true;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCascadeMerge(String ignore) {
        m_cascadeMerge = true;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCascadePersist(String ignore) {
        m_cascadePersist = true;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCascadeRefresh(String ignore) {
        m_cascadeRefresh = true;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCascadeRemove(String ignore) {
        m_cascadeRemove = true;
    }    
}
