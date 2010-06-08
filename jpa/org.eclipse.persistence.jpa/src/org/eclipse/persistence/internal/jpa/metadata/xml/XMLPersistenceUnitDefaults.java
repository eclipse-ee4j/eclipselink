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
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping file
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     10/01/2008-1.1 Guy Pelletier 
 *       - 249329: To remain JPA 1.0 compliant, any new JPA 2.0 annotations should be referenced by name
 *     04/09/2010-2.1 Guy Pelletier 
 *       - 307050: Add defaults for access methods of a VIRTUAL access type
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.xml;

import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityListenerMetadata;
import org.eclipse.persistence.internal.jpa.metadata.mappings.AccessMethodsMetadata;

/**
 * Object to hold onto the XML persistence unit defaults.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class XMLPersistenceUnitDefaults extends ORMetadata {
    private AccessMethodsMetadata m_accessMethods;
    
    private boolean m_cascadePersist;
    private boolean m_delimitedIdentifiers;
    
    private List<EntityListenerMetadata> m_entityListeners;
    
    private String m_access;
    private String m_catalog;
    private String m_schema;

    /**
     * INTERNAL:
     */
    public XMLPersistenceUnitDefaults() {
        super("<persistence-unit-defaults>");
    }
    
    /**
     * INTERNAL:
     * If equals returns false, call getConflict() for a finer grain reason why.
     */
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof XMLPersistenceUnitDefaults) {
            XMLPersistenceUnitDefaults persistenceUnitDefaults = (XMLPersistenceUnitDefaults) objectToCompare; 
            
            if (! valuesMatch(persistenceUnitDefaults.getAccess(), getAccess())) {
                return false;
            }

            if (! valuesMatch(persistenceUnitDefaults.getCatalog(), getCatalog())) {
                return false;
            }
                
            if (! valuesMatch(persistenceUnitDefaults.getSchema(), getSchema())) {
                return false;
            }
                
            if (! valuesMatch(persistenceUnitDefaults.isCascadePersist(), isCascadePersist())) {
                return false;
            } 
            
            if (! valuesMatch(persistenceUnitDefaults.isDelimitedIdentifiers(), isDelimitedIdentifiers())) {
                return false;
            } 
        
            return true;
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getAccess() {
        return m_access;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public AccessMethodsMetadata getAccessMethods() {
        return m_accessMethods;
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
    public String getDelimitedIdentifiers() {
        return null;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getCatalog() {
        return m_catalog;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<EntityListenerMetadata> getEntityListeners() {
        return m_entityListeners;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getSchema() {
        return m_schema;
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasAccessMethods() {
        return m_accessMethods != null;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        initXMLObject(m_accessMethods, accessibleObject);
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
    public boolean isDelimitedIdentifiers(){
        return m_delimitedIdentifiers;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void merge(ORMetadata metadata) {
        XMLPersistenceUnitDefaults persistenceUnitDefaults = (XMLPersistenceUnitDefaults) metadata;
        if (persistenceUnitDefaults != null) {
            // Primitive boolean merging.
            mergePrimitiveBoolean(m_cascadePersist, persistenceUnitDefaults.isCascadePersist(), persistenceUnitDefaults, "cascade-persist");
            mergePrimitiveBoolean(m_delimitedIdentifiers, persistenceUnitDefaults.isDelimitedIdentifiers(), persistenceUnitDefaults, "delimited-identifiers");
            
            // Simple object merging.
            m_access = (String) mergeSimpleObjects(m_access, persistenceUnitDefaults.getAccess(), persistenceUnitDefaults, "<access>");
            m_catalog = (String) mergeSimpleObjects(m_catalog, persistenceUnitDefaults.getCatalog(), persistenceUnitDefaults, "<catalog>");
            m_schema = (String) mergeSimpleObjects(m_schema, persistenceUnitDefaults.getSchema(),  persistenceUnitDefaults, "<schema>");
            
            // ORMetadata object merging.        
            m_accessMethods = (AccessMethodsMetadata) mergeORObjects(m_accessMethods, persistenceUnitDefaults.getAccessMethods());
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setAccess(String access) {
        m_access = access;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setAccessMethods(AccessMethodsMetadata accessMethods){
        m_accessMethods = accessMethods;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCatalog(String catalog) {
        m_catalog = catalog;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setEntityListeners(List<EntityListenerMetadata> entityListeners) {
        m_entityListeners = entityListeners;
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
    public void setDelimitedIdentifiers(String ignore){
        m_delimitedIdentifiers = true;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setSchema(String schema) {
        m_schema = schema;
    }
}
