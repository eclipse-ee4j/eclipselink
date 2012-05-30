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
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 *     02/25/2009-2.0 Guy Pelletier 
 *       - 265359: JPA 2.0 Element Collections - Metadata processing portions
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     06/02/2009-2.0 Guy Pelletier 
 *       - 278768: JPA 2.0 Association Override Join Table
 *     11/06/2009-2.0 Guy Pelletier 
 *       - 286317: UniqueConstraint xml element is changing (plus couple other fixes, see bug)
 *     03/08/2010-2.1 Guy Pelletier 
 *       - 303632: Add attribute-type for mapping attributes to EclipseLink-ORM
 *     03/29/2010-2.1 Guy Pelletier 
 *       - 267217: Add Named Access Type to EclipseLink-ORM
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     12/30/2010-2.3 Guy Pelletier  
 *       - 312253: Descriptor exception with Embeddable on DDL gen
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 *     07/03/2011-2.3.1 Guy Pelletier 
 *       - 348756: m_cascadeOnDelete boolean should be changed to Boolean
 *     25/05/2012-2.4 Guy Pelletier  
 *       - 354678: Temp classloader is still being used during metadata processing
 ******************************************************************************/ 
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.CascadeOnDelete;
import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.Noncacheable;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.tables.CollectionTableMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import org.eclipse.persistence.mappings.AggregateCollectionMapping;
import org.eclipse.persistence.mappings.AggregateMapping;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.ContainerMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeDirectCollectionMapping;

import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_FETCH_LAZY;

/**
 * An abstract direct collection accessor.
 * 
 * Used to support DirectCollection, DirectMap, AggregateCollection through 
 * the ElementCollection, BasicCollection and BasicMap metadata.
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
 * @since EclipseLink 1.2
 */
public abstract class DirectCollectionAccessor extends DirectAccessor {
    private Boolean m_cascadeOnDelete;
    private Boolean m_nonCacheable;
    private String m_joinFetch;
    private String m_batchFetch;
    private Integer m_batchFetchSize;
    private CollectionTableMetadata m_collectionTable;
    
    /**
     * INTERNAL:
     */
    protected DirectCollectionAccessor(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    protected DirectCollectionAccessor(MetadataAnnotation annotation, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(annotation, accessibleObject, classAccessor);
        
        // Set the fetch type. A basic map may have no annotation (will default).
        if (annotation != null) {
            // Set the fetch type.
            setFetch((String) annotation.getAttribute("fetch"));
        }
        
        // Set the join fetch if one is present.
        MetadataAnnotation joinFetch = getAnnotation(JoinFetch.class);
        if (joinFetch != null) {
            m_joinFetch = (String) joinFetch.getAttribute("value");
        }
        
        // Set the batch fetch if one is present.
        MetadataAnnotation batchFetch = getAnnotation(BatchFetch.class);
        if (batchFetch != null) {
            // Get attribute string will return the default ""
            m_batchFetch = (String) batchFetch.getAttributeString("value");
            m_batchFetchSize = (Integer) batchFetch.getAttribute("size");
        }
        
        // Set the cascade on delete if specified.
        m_cascadeOnDelete = isAnnotationPresent(CascadeOnDelete.class);
        
        // Set the non cacheable if specified.
        m_nonCacheable = isAnnotationPresent(Noncacheable.class);
        
        // Since BasicCollection and ElementCollection look for different
        // collection tables, we will not initialize/look for one here. Those
        // accessors will be responsible for loading their collection table.
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof DirectCollectionAccessor) {
            DirectCollectionAccessor directCollectionAccessor = (DirectCollectionAccessor) objectToCompare;
            
            if (! valuesMatch(m_joinFetch, directCollectionAccessor.getJoinFetch())) {
                return false;
            }
            
            if (! valuesMatch(m_batchFetch, directCollectionAccessor.getBatchFetch())) {
                return false;
            }
            
            if (! valuesMatch(m_batchFetchSize, directCollectionAccessor.getBatchFetchSize())) {
                return false;
            }
            
            if (! valuesMatch(m_cascadeOnDelete, directCollectionAccessor.getCascadeOnDelete())) {
                return false;
            }
            
            if (! valuesMatch(m_nonCacheable, directCollectionAccessor.getNonCacheable())) {
                return false;
            }
            
            return valuesMatch(m_collectionTable, directCollectionAccessor.getCollectionTable());
        }
        
        return false;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public String getBatchFetch() {
        return m_batchFetch;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getCascadeOnDelete() {
        return m_cascadeOnDelete;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Integer getBatchFetchSize() {
        return m_batchFetchSize;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setBatchFetchSize(Integer batchFetchSize) {
        m_batchFetchSize = batchFetchSize;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public CollectionTableMetadata getCollectionTable() {
        return m_collectionTable;
    }
    
    /**
     * INTERNAL:
     */
    protected String getDefaultCollectionTableName() {
        return getOwningDescriptor().getAlias() + "_" + getDefaultAttributeName(); 
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public String getDefaultFetchType() {
        return JPA_FETCH_LAZY; 
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public String getJoinFetch() {
        return m_joinFetch;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public String getPrivateOwned() {
        return null;
    }
    
    /**
     * INTERNAL:
     * The reference table in a direct collection case is the collection table.
     */
    @Override
    protected DatabaseTable getReferenceDatabaseTable() {
        if (m_collectionTable == null) {
            return null;
        }
        return m_collectionTable.getDatabaseTable();
    }
    
    /**
     * INTERNAL:
     * In a direct collection case, there is no notion of a reference 
     * descriptor, therefore we return this accessors descriptor.
     */
    @Override
    public MetadataDescriptor getReferenceDescriptor() {
        return getDescriptor();
    }
    
    /**
     * INTERNAL:
     * Return the converter name for a map key.
     */
    protected abstract String getKeyConverter();
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getNonCacheable(){
        return m_nonCacheable;
    }
    
    /**
     * INTERNAL:
     * Return the converter name for a collection value.
     * @see BasicMapAccessor for override details. An EclipseLink 
     * BasicMapAccessor can specify a value converter within the BasicMap
     * metadata. Otherwise for all other cases, BasicCollectionAccessor and
     * ElementCollectionAccessor, the value converter is returned from a Convert
     * metadata specification.
     */
    protected String getValueConverter() {
        return getConvert();
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor has a map key class specified.
     * @see ElementCollectionAccessor
     */
    protected boolean hasMapKeyClass() {
        return false; 
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        // Initialize single objects.
        initXMLObject(m_collectionTable, accessibleObject);
    }
    
    /**
     * INTERNAL:
     */
    public Boolean isCascadeOnDelete() {
        return m_cascadeOnDelete != null && m_cascadeOnDelete.booleanValue();
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a direct collection mapping, 
     * which include basic collection, basic map and element collection 
     * accessors.
     */
    @Override
    public boolean isDirectCollection() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public boolean isNonCacheable() {
        return m_nonCacheable != null && m_nonCacheable.booleanValue();
    }
    
    /**
     * INTERNAL:
     * Returns true if the given class is a valid basic collection type.
     */ 
    protected boolean isValidDirectCollectionType() {
        return getAccessibleObject().isSupportedCollectionClass(getRawClass());
    }
    
    /**
     * INTERNAL:
     * Returns true if the given class is a valid basic map type.
     */ 
    protected boolean isValidDirectMapType() {
        return getAccessibleObject().isSupportedMapClass(getRawClass());
    }
    
    /**
     * INTERNAL:
     */
    protected void process(DatabaseMapping mapping) {
        // Add the mapping to the descriptor.
        setMapping(mapping);
        
        // Set the attribute name.
        mapping.setAttributeName(getAttributeName());
        
        // Will check for PROPERTY access
        setAccessorMethods(mapping);
        
        if (mapping instanceof CollectionMapping) {
            CollectionMapping collectionMapping = (CollectionMapping)mapping;

            // Set the reference class name.
            collectionMapping.setReferenceClassName(getReferenceClassName());
            
            // Process join fetch type.
            processJoinFetch(getJoinFetch(), collectionMapping);
            
            // Process the batch fetch if specified.
            processBatchFetch(getBatchFetch(), collectionMapping);
            
            // Process the collection table.
            processCollectionTable(collectionMapping);
    
            // The spec. requires pessimistic lock to be extend-able to CollectionTable
            collectionMapping.setShouldExtendPessimisticLockScope(true);
            
            // Set the cascade on delete if specified.
            collectionMapping.setIsCascadeOnDeleteSetOnDatabase(isCascadeOnDelete());
        } else if (mapping instanceof AggregateMapping) {
            AggregateMapping aggregateMapping = (AggregateMapping)mapping;

            // Set the reference class name.
            aggregateMapping.setReferenceClassName(getReferenceClassName());
        }
        
        // Set the non cacheable if specified.
        mapping.setIsCacheable(!isNonCacheable());
        
        // Process a @ReturnInsert and @ReturnUpdate (to log a warning message)
        processReturnInsertAndUpdate();

        // Process any partitioning policies.
        processPartitioning();
    }
    
    /**
     * INTERNAL:
     * Process a MetadataCollectionTable. Sub classes should call this to
     * ensure a table is defaulted.
     */
    protected void processCollectionTable(CollectionMapping mapping) {
        // Check that we loaded a collection table otherwise default one.        
        if (m_collectionTable == null) {
            m_collectionTable = new CollectionTableMetadata(this);
        }
        
        // Process any table defaults and log warning messages.
        processTable(m_collectionTable, getDefaultCollectionTableName());
        
        // Set the reference table on the mapping.
        if (isDirectEmbeddableCollection()) {
            ((AggregateCollectionMapping) mapping).setDefaultSourceTable(m_collectionTable.getDatabaseTable());
        } else {
            ((DirectCollectionMapping) mapping).setReferenceTable(m_collectionTable.getDatabaseTable());
        }
    }
    
    /**
     * INTERNAL:
     */
    protected void processDirectCollectionMapping() {
        // Initialize our mapping.
        DatabaseMapping mapping = getOwningDescriptor().getClassDescriptor().newDirectCollectionMapping();
        
        // Process common direct collection metadata. This must be done 
        // before any field processing since field processing requires that 
        // the collection table be processed before hand.
        process(mapping);

        processContainerPolicyAndIndirection((ContainerMapping)mapping);
        
        if (mapping instanceof DirectCollectionMapping) {
            DirectCollectionMapping directCollectionMapping = (DirectCollectionMapping)mapping;
            // Process the container and indirection policies.
            
            // Process the value column (we must process this field before the 
            // call to processConverter, since it may set a field classification)
            directCollectionMapping.setDirectField(getDatabaseField(getReferenceDatabaseTable(), MetadataLogger.VALUE_COLUMN));
    
            // To resolve any generic types (or respect an attribute type 
            // specification) we need to set the attribute classification on the 
            // mapping to ensure we do the right conversions.
            if (hasAttributeType() || getAccessibleObject().isGenericCollectionType()) {
                directCollectionMapping.setDirectFieldClassificationName(getReferenceClassName());
            }
        } else if (mapping.isAbstractCompositeDirectCollectionMapping()) {
            ((AbstractCompositeDirectCollectionMapping)mapping).setField(getDatabaseField(getDescriptor().getPrimaryTable(), MetadataLogger.COLUMN));
        }
        
        // Process a converter for this mapping. We will look for a convert
        // value. If none is found then we'll look for a JPA converter, that 
        // is, Enumerated, Lob and Temporal. With everything falling into 
        // a serialized mapping if no converter whatsoever is found.
        processMappingValueConverter(mapping, getValueConverter(), getReferenceClass());        
    }
    
    /**
     * INTERNAL:
     */
    protected void processDirectMapMapping() {
        // Initialize and process common direct collection metadata. This must 
        // be done before any field processing since field processing requires 
        // that the collection table be processed before hand.
        DirectMapMapping mapping = new DirectMapMapping();
        process(mapping);
        
        // Process the container and indirection policies.
        processContainerPolicyAndIndirection(mapping);
        
        // Process the key column (we must process this field before the 
        // call to processConverter, since it may set a field classification)
        mapping.setDirectKeyField(getDatabaseField(getReferenceDatabaseTable(), MetadataLogger.MAP_KEY_COLUMN));
        
        // Only process the key converter if this is a basic map accessor. The
        // key converter for an element collection case will be taken care of
        // in the processContainerPolicyAndIndirection call above.
        if (isBasicMap()) {
            // To resolve any generic types (or respect an attribute type 
            // specification) we need to set the attribute classification on the 
            // mapping to ensure we do the right conversions.
            if (hasAttributeType() || getAccessibleObject().isGenericCollectionType()) {
                mapping.setDirectKeyFieldClassificationName(getJavaClassName(getMapKeyReferenceClass()));
            }
            
            // Process a converter for the key column of this mapping.
            processMappingKeyConverter(mapping, getKeyConverter(), getMapKeyReferenceClass());
        }
        
        // Process the value column (we must process this field before the call
        // to processConverter, since it may set a field classification)
        mapping.setDirectField(getDatabaseField(getReferenceDatabaseTable(), MetadataLogger.VALUE_COLUMN));
        
        // To resolve any generic types (or respect an attribute type 
        // specification) we need to set the attribute classification on the 
        // mapping to ensure we do the right conversions.
        if (hasAttributeType() || getAccessibleObject().isGenericCollectionType()) {
            mapping.setDirectFieldClassificationName(getJavaClassName(getReferenceClass()));
        }
        
        // Process a converter for value column of this mapping.
        processMappingValueConverter(mapping, getValueConverter(), getReferenceClass());
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setBatchFetch(String batchFetch) {
        m_batchFetch = batchFetch;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCascadeOnDelete(Boolean cascadeOnDelete) {
        m_cascadeOnDelete = cascadeOnDelete;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setCollectionTable(CollectionTableMetadata collectionTable) {
        m_collectionTable = collectionTable;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setNonCacheable(Boolean nonCacheable){
        m_nonCacheable = nonCacheable;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setJoinFetch(String joinFetch) {
        m_joinFetch = joinFetch;
    }
}
