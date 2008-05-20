/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
 *       - 218084: Implement metadata merging functionality between mapping file
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.xml;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityListenerMetadata;

/**
 * INTERNAL:
 * Object to hold onto the XML persistence unit metadata.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class XMLPersistenceUnitMetadata extends ORMetadata {
    private boolean m_xmlMappingMetadataComplete;
    private XMLPersistenceUnitDefaults m_persistenceUnitDefaults;
    
    /**
     * INTERNAL:
     */
    public XMLPersistenceUnitMetadata() {
        super("<persistence-unit-metadata>");
    }

    /**
     * INTERNAL:
     * If equals returns false, call getConflict() for a finer grain reason why.
     */
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof XMLPersistenceUnitMetadata) {
            XMLPersistenceUnitMetadata persistenceUnitMetadata = (XMLPersistenceUnitMetadata) objectToCompare;
            
            // Check xmlMappingMetadataComplete.
            if (persistenceUnitMetadata.isXMLMappingMetadataComplete() != isXMLMappingMetadataComplete()) {
                return false;
            } 

            // Check the persistence unit defaults.
            XMLPersistenceUnitDefaults persistenceUnitDefaults = persistenceUnitMetadata.getPersistenceUnitDefaults();
            
            if (m_persistenceUnitDefaults != null && persistenceUnitDefaults != null) {
                if (! m_persistenceUnitDefaults.equals(persistenceUnitDefaults)) {
                    return false;
                }
            } else {
                return false;
            }
        
            return true;
        }
    
        return false;
    }
    
    /**
     * INTERNAL:
     */
    public String getCatalog() {
        return (m_persistenceUnitDefaults == null) ? "" : m_persistenceUnitDefaults.getCatalog();
    }
    
    /**
     * INTERNAL:
     */
    public List<EntityListenerMetadata> getDefaultListeners() {
        return (m_persistenceUnitDefaults == null) ? new ArrayList<EntityListenerMetadata>(): m_persistenceUnitDefaults.getEntityListeners(); 
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public XMLPersistenceUnitDefaults getPersistenceUnitDefaults() {
        return m_persistenceUnitDefaults;
    }
    
    /**
     * INTERNAL:
     */
    public String getSchema() {
        return (m_persistenceUnitDefaults == null) ? "" : m_persistenceUnitDefaults.getSchema();
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getXMLMappingMetadataComplete() {
        return null;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public boolean isXMLMappingMetadataComplete() {
        return m_xmlMappingMetadataComplete;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void merge(ORMetadata metadata) {
        XMLPersistenceUnitMetadata persistenceUnitMetadata = (XMLPersistenceUnitMetadata) metadata;
        
        // Primitive boolean merging.
        mergePrimitiveBoolean(m_xmlMappingMetadataComplete, persistenceUnitMetadata.isXMLMappingMetadataComplete(), persistenceUnitMetadata.getAccessibleObject(), "<xml-mapping-metadata-complete>");
        
        // Merge the persistence unit defaults.
        m_persistenceUnitDefaults.merge(persistenceUnitMetadata.getPersistenceUnitDefaults());
    }
    
    /**
     * INTERNAL:
     * Set the location if one is not already set.
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject) {
        super.initXMLObject(accessibleObject);
        
        initXMLObject(m_persistenceUnitDefaults, accessibleObject);
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setPersistenceUnitDefaults(XMLPersistenceUnitDefaults persistenceUnitDefaults) {
        m_persistenceUnitDefaults = persistenceUnitDefaults;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setXMLMappingMetadataComplete(String ignore) {
        m_xmlMappingMetadataComplete = true;
    }
}
