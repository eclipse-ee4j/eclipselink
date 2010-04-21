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
 *       - 218084: Implement metadata merging functionality between mapping files
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.classes;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;

import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicCollectionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicMapAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ElementCollectionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.EmbeddedAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.EmbeddedIdAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.IdAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.TransformationAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.TransientAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.VariableOneToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.VersionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

/**
 * Object to represent all the attributes of an XML defined entity,
 * mapped-superclass or embeddable.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class XMLAttributes extends ORMetadata {
    private EmbeddedIdAccessor m_embeddedId;
    
    private List<BasicAccessor> m_basics;
    private List<BasicCollectionAccessor> m_basicCollections;
    private List<BasicMapAccessor> m_basicMaps;
    private List<ElementCollectionAccessor> m_elementCollections;
    private List<EmbeddedAccessor> m_embeddeds;
    private List<IdAccessor> m_ids;
    private List<ManyToManyAccessor> m_manyToManys;
    private List<ManyToOneAccessor> m_manyToOnes;
    private List<MappingAccessor> m_allAccessors;
    private List<OneToManyAccessor> m_oneToManys;
    private List<OneToOneAccessor> m_oneToOnes;
    private List<VariableOneToOneAccessor> m_variableOneToOnes;
    private List<TransformationAccessor> m_transformations;
    private List<TransientAccessor> m_transients;
    private List<VersionAccessor> m_versions;
    
    /**
     * INTERNAL:
     */
    public XMLAttributes() {
        super("<attributes>");
    }

    /**
     * INTERNAL:
     */
    public List<MappingAccessor> getAccessors() {
        if (m_allAccessors == null) {
            m_allAccessors = new ArrayList<MappingAccessor>();
        
            if (m_embeddedId != null) {
                m_allAccessors.add(m_embeddedId);
            }
            
            m_allAccessors.addAll(m_ids); 
            m_allAccessors.addAll(m_basics);
            m_allAccessors.addAll(m_basicCollections);
            m_allAccessors.addAll(m_basicMaps);
            m_allAccessors.addAll(m_versions);
            m_allAccessors.addAll(m_embeddeds);
            m_allAccessors.addAll(m_elementCollections);
            m_allAccessors.addAll(m_transformations);
            m_allAccessors.addAll(m_transients);
            m_allAccessors.addAll(m_manyToManys);
            m_allAccessors.addAll(m_manyToOnes);
            m_allAccessors.addAll(m_oneToManys);
            m_allAccessors.addAll(m_oneToOnes);
            m_allAccessors.addAll(m_variableOneToOnes);
        }
        
        return m_allAccessors;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<BasicCollectionAccessor> getBasicCollections() {
        return m_basicCollections;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<BasicMapAccessor> getBasicMaps() {
        return m_basicMaps;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<BasicAccessor> getBasics() {
        return m_basics;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<ElementCollectionAccessor> getElementCollections() {
        return m_elementCollections;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public EmbeddedIdAccessor getEmbeddedId() {
        return m_embeddedId;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<EmbeddedAccessor> getEmbeddeds() {
        return m_embeddeds;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<IdAccessor> getIds() {
        return m_ids;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<ManyToManyAccessor> getManyToManys() {
        return m_manyToManys;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<ManyToOneAccessor> getManyToOnes() {
        return m_manyToOnes;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<OneToManyAccessor> getOneToManys() {
        return m_oneToManys;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<OneToOneAccessor> getOneToOnes() {
        return m_oneToOnes;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<TransformationAccessor> getTransformations() {
        return m_transformations;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<TransientAccessor> getTransients() {
        return m_transients;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<VariableOneToOneAccessor> getVariableOneToOnes() {
        return m_variableOneToOnes;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<VersionAccessor> getVersions() {
        return m_versions;
    }

    /**
     * INTERNAL:
     * This is going to initialize the accessible objects.
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        // For merging purposes we will initialize the accessors with the owning
        // classes accessible object. The actual accessible object (field or
        // method) for each accessor will be set during the actual metadata
        // processing stage.
        for (MappingAccessor accessor : getAccessors()) {
            accessor.initXMLObject(accessibleObject, entityMappings);
        }
    }
    
    /**
     * INTERNAL:
     * Since we are controlling the merging and we know we'll be comparing
     * apples with apples, the casting is safe to assume.
     */
    @Override
    public void merge(ORMetadata metadata) {
        if (metadata != null) {
            // ORMetadata list merging.
            m_allAccessors = mergeORObjectLists(getAccessors(), ((XMLAttributes) metadata).getAccessors());
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setBasicCollections(List<BasicCollectionAccessor> basicCollections) {
        m_basicCollections = basicCollections;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setBasicMaps(List<BasicMapAccessor> basicMaps) {
        m_basicMaps = basicMaps;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setBasics(List<BasicAccessor> basics) {
        m_basics = basics;
    }    
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setElementCollections(List<ElementCollectionAccessor> elementCollections) {
        m_elementCollections = elementCollections;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setEmbeddedId(EmbeddedIdAccessor embeddedId) {
        m_embeddedId = embeddedId;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setEmbeddeds(List<EmbeddedAccessor> embeddeds) {
        m_embeddeds = embeddeds;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setIds(List<IdAccessor> ids) {
        m_ids = ids;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setManyToManys(List<ManyToManyAccessor> manyToManys) {
        m_manyToManys = manyToManys;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setManyToOnes(List<ManyToOneAccessor> manyToOnes) {
        m_manyToOnes = manyToOnes;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setOneToManys(List<OneToManyAccessor> oneToManys) {
        m_oneToManys = oneToManys;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setOneToOnes(List<OneToOneAccessor> oneToOnes) {
        m_oneToOnes = oneToOnes;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setTransformations(List<TransformationAccessor> transformations) {
        m_transformations = transformations;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setTransients(List<TransientAccessor> transients) {
        m_transients = transients;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setVariableOneToOnes(List<VariableOneToOneAccessor> variableOneToOnes) {
        m_variableOneToOnes = variableOneToOnes;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setVersions(List<VersionAccessor> versions) {
        m_versions = versions;
    }
}
    
