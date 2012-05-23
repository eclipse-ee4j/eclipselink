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
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     09/16/2010-2.2 Guy Pelletier 
 *       - 283028: Add support for letting an @Embeddable extend a @MappedSuperclass
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
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
import org.eclipse.persistence.internal.jpa.metadata.structures.ArrayAccessor;
import org.eclipse.persistence.internal.jpa.metadata.structures.StructureAccessor;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

/**
 * Object to represent all the attributes of an XML defined entity,
 * mapped-superclass or embeddable.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - any metadata mapped from XML to this class must be handled in the merge
 *   method. (merging is done at the accessor/mapping level)
 * - any metadata mapped from XML to this class must be initialized in the
 *   initXMLObject  method.
 * - methods should be preserved in alphabetical order.
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
    private List<OneToManyAccessor> m_oneToManys;
    private List<OneToOneAccessor> m_oneToOnes;
    private List<VariableOneToOneAccessor> m_variableOneToOnes;
    private List<TransformationAccessor> m_transformations;
    private List<TransientAccessor> m_transients;
    private List<VersionAccessor> m_versions;
    private List<StructureAccessor> m_structures;
    private List<ArrayAccessor> m_arrays;

    /**
     * INTERNAL:
     */
    public XMLAttributes() {
        super("<attributes>");
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof XMLAttributes) {
            XMLAttributes attributes = (XMLAttributes) objectToCompare;
            
            if (! valuesMatch(m_embeddedId, attributes.getEmbeddedId())) {
                return false;
            }
            
            if (! valuesMatch(m_basics, attributes.getBasics())) {
                return false;
            }
            
            if (! valuesMatch(m_basicCollections, attributes.getBasicCollections())) {
                return false;
            }
            
            if (! valuesMatch(m_basicMaps, attributes.getBasicMaps())) {
                return false;
            }
            
            if (! valuesMatch(m_elementCollections, attributes.getElementCollections())) {
                return false;
            }
            
            if (! valuesMatch(m_embeddeds, attributes.getEmbeddeds())) {
                return false;
            }
            
            if (! valuesMatch(m_ids, attributes.getIds())) {
                return false;
            }
            
            if (! valuesMatch(m_manyToManys, attributes.getManyToManys())) {
                return false;
            }
            
            if (! valuesMatch(m_manyToOnes, attributes.getManyToOnes())) {
                return false;
            }
            
            if (! valuesMatch(m_oneToManys, attributes.getOneToManys())) {
                return false;
            }
            
            if (! valuesMatch(m_oneToOnes, attributes.getOneToOnes())) {
                return false;
            }
            
            if (! valuesMatch(m_variableOneToOnes, attributes.getVariableOneToOnes())) {
                return false;
            }
            
            if (! valuesMatch(m_transformations, attributes.getTransformations())) {
                return false;
            }
            
            if (! valuesMatch(m_transients, attributes.getTransients())) {
                return false;
            }
            
            if (! valuesMatch(m_structures, attributes.getStructures())) {
                return false;
            }
            
            if (! valuesMatch(m_arrays, attributes.getArrays())) {
                return false;
            }
            
            return valuesMatch(m_versions, attributes.getVersions());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * This list is not cached and should not be cached since our accessors
     * may change in a merge. The accessors list gets rebuilt every time
     * this method is called (which should only ever be once!)
     */
    public List<MappingAccessor> getAccessors() {
        List<MappingAccessor> accessors = new ArrayList<MappingAccessor>();
        
        if (m_embeddedId != null) {
            accessors.add(m_embeddedId);
        }
            
        accessors.addAll(m_ids); 
        accessors.addAll(m_basics);
        accessors.addAll(m_basicCollections);
        accessors.addAll(m_basicMaps);
        accessors.addAll(m_versions);
        accessors.addAll(m_embeddeds);
        accessors.addAll(m_elementCollections);
        accessors.addAll(m_transformations);
        accessors.addAll(m_transients);
        accessors.addAll(m_manyToManys);
        accessors.addAll(m_manyToOnes);
        accessors.addAll(m_oneToManys);
        accessors.addAll(m_oneToOnes);
        accessors.addAll(m_variableOneToOnes);
        accessors.addAll(m_structures);
        accessors.addAll(m_arrays);
        
        return accessors;
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
        
        // Initialize single objects.
        initXMLObject(m_embeddedId, accessibleObject);
        
        // Initialize lists of objects.
        initXMLObjects(m_basics, accessibleObject);
        initXMLObjects(m_basicCollections, accessibleObject);
        initXMLObjects(m_basicMaps, accessibleObject);
        initXMLObjects(m_elementCollections, accessibleObject);
        initXMLObjects(m_embeddeds, accessibleObject);
        initXMLObjects(m_ids, accessibleObject);
        initXMLObjects(m_manyToManys, accessibleObject);
        initXMLObjects(m_manyToOnes, accessibleObject);
        initXMLObjects(m_oneToManys, accessibleObject);
        initXMLObjects(m_oneToOnes, accessibleObject);
        initXMLObjects(m_variableOneToOnes, accessibleObject);
        initXMLObjects(m_transformations, accessibleObject);
        initXMLObjects(m_transients, accessibleObject);
        initXMLObjects(m_versions, accessibleObject);
        initXMLObjects(m_structures, accessibleObject);
        initXMLObjects(m_arrays, accessibleObject);
    }
    
    /**
     * INTERNAL:
     * Since we are controlling the merging and we know we'll be comparing
     * apples with apples, the casting is safe to assume.
     */
    @Override
    public void merge(ORMetadata metadata) {
        if (metadata != null) {
            XMLAttributes attributes = (XMLAttributes) metadata;
            
            // ORMetadata object merging
            m_embeddedId = (EmbeddedIdAccessor) mergeORObjects(getEmbeddedId(), attributes.getEmbeddedId());
            
            // ORMetadata list merging.
            m_basics = mergeORObjectLists(m_basics, attributes.getBasics());
            m_basicCollections = mergeORObjectLists(m_basicCollections, attributes.getBasicCollections());
            m_basicMaps = mergeORObjectLists(m_basicMaps, attributes.getBasicMaps());
            m_elementCollections = mergeORObjectLists(m_elementCollections, attributes.getElementCollections());
            m_embeddeds = mergeORObjectLists(m_embeddeds, attributes.getEmbeddeds());
            m_ids = mergeORObjectLists(m_ids, attributes.getIds());
            m_manyToManys = mergeORObjectLists(m_manyToManys, attributes.getManyToManys());
            m_manyToOnes = mergeORObjectLists(m_manyToOnes, attributes.getManyToOnes());
            m_oneToManys = mergeORObjectLists(m_oneToManys, attributes.getOneToManys());
            m_oneToOnes = mergeORObjectLists(m_oneToOnes, attributes.getOneToOnes());
            m_variableOneToOnes = mergeORObjectLists(m_variableOneToOnes, attributes.getVariableOneToOnes());
            m_transformations = mergeORObjectLists(m_transformations, attributes.getTransformations());
            m_transients = mergeORObjectLists(m_transients, attributes.getTransients());
            m_versions = mergeORObjectLists(m_versions, attributes.getVersions());
            m_structures = mergeORObjectLists(m_structures, attributes.getStructures());
            m_arrays = mergeORObjectLists(m_arrays, attributes.getArrays());
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
    
    public List<StructureAccessor> getStructures() {
        return m_structures;
    }

    public void setStructures(List<StructureAccessor> structures) {
        m_structures = structures;
    }

    public List<ArrayAccessor> getArrays() {
        return m_arrays;
    }

    public void setArrays(List<ArrayAccessor> arrays) {
        m_arrays = arrays;
    }
    
}
    
