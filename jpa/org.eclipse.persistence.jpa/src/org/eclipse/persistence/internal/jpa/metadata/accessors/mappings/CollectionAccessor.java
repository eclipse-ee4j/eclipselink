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
 *     06/20/2008-1.0 Guy Pelletier 
 *       - 232975: Failure when attribute type is generic
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 *     02/26/2009-2.0 Guy Pelletier 
 *       - 264001: dot notation for mapped-by and order-by
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/03/2009-2.0 Guy Pelletier
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/24/2009-2.0 Guy Pelletier 
 *       - 270011: JPA 2.0 MappedById support
 *     05/1/2009-2.0 Guy Pelletier 
 *       - 249033: JPA 2.0 Orphan removal       
 *     06/02/2009-2.0 Guy Pelletier 
 *       - 278768: JPA 2.0 Association Override Join Table
 *     06/09/2009-2.0 Guy Pelletier 
 *       - 249037: JPA 2.0 persisting list item index
 *     09/29/2009-2.0 Guy Pelletier 
 *       - 282553: JPA 2.0 JoinTable support for OneToOne and ManyToOne
 *     11/06/2009-2.0 Guy Pelletier 
 *       - 286317: UniqueConstraint xml element is changing (plus couple other fixes, see bug)
 *     03/08/2010-2.1 Guy Pelletier 
 *       - 303632: Add attribute-type for mapping attributes to EclipseLink-ORM
 *     03/29/2010-2.1 Guy Pelletier 
 *       - 267217: Add Named Access Type to EclipseLink-ORM
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.FetchType;
import javax.persistence.MapKey;
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.MapKeyJoinColumns;
import javax.persistence.MapKeyTemporal;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;

import org.eclipse.persistence.annotations.MapKeyConvert;
import org.eclipse.persistence.annotations.OrderCorrection;
import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

import org.eclipse.persistence.internal.jpa.metadata.columns.AssociationOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.AttributeOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.OrderColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.EnumeratedMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.TemporalMetadata;
import org.eclipse.persistence.internal.jpa.metadata.mappings.MapKeyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.JoinTableMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.EmbeddableMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;

/**
 * INTERNAL:
 * A relational collection mapping accessor.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public abstract class CollectionAccessor extends RelationshipAccessor implements MappedKeyMapAccessor {
    // Order by constants
    private static final String ASCENDING = "ASC";
    private static final String DESCENDING = "DESC";
    
    private ColumnMetadata m_mapKeyColumn;
    private EnumeratedMetadata m_mapKeyEnumerated;
    
    private List<AssociationOverrideMetadata> m_mapKeyAssociationOverrides;
    private List<AttributeOverrideMetadata> m_mapKeyAttributeOverrides;
    private List<JoinColumnMetadata> m_mapKeyJoinColumns;
    
    private MapKeyMetadata m_mapKey;
    private MetadataClass m_mapKeyClass;
    private OrderColumnMetadata m_orderColumn;
    
    private String m_mapKeyConvert;
    private String m_mapKeyClassName;
    private String m_mappedBy;
    private String m_orderBy;
    
    private TemporalMetadata m_mapKeyTemporal;
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    protected CollectionAccessor(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    protected CollectionAccessor(MetadataAnnotation annotation, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(annotation, accessibleObject, classAccessor);
        
        m_mappedBy = (annotation == null) ? "" : (String) annotation.getAttribute("mappedBy");
        
        // Set the order if one is present.
        if (isAnnotationPresent(OrderBy.class)) {
            m_orderBy = (String) getAnnotation(OrderBy.class).getAttributeString("value");
        }
        
        // Set the map key if one is present.
        if (isAnnotationPresent(MapKey.class)) {
            m_mapKey = new MapKeyMetadata(getAnnotation(MapKey.class), accessibleObject);
        }
        
        // Set the map key class if one is defined.
        if (isAnnotationPresent(MapKeyClass.class)) {
            m_mapKeyClass = getMetadataClass((String)getAnnotation(MapKeyClass.class).getAttribute("value"));
        }
        
        // Set the map key column if one is defined.
        if (isAnnotationPresent(MapKeyColumn.class)) {
            m_mapKeyColumn = new ColumnMetadata(getAnnotation(MapKeyColumn.class), accessibleObject);
        }
        
        // Set the map key join columns if some are present.
        m_mapKeyJoinColumns = new ArrayList<JoinColumnMetadata>();
        // Process all the map key join columns first.
        if (isAnnotationPresent(MapKeyJoinColumns.class)) {
            for (Object jColumn : (Object[]) getAnnotation(MapKeyJoinColumns.class).getAttributeArray("value")) {
                m_mapKeyJoinColumns.add(new JoinColumnMetadata((MetadataAnnotation)jColumn, accessibleObject));
            }
        }
        
        // Process the single map key key join column second.
        if (isAnnotationPresent(MapKeyJoinColumn.class)) {
            m_mapKeyJoinColumns.add(new JoinColumnMetadata(getAnnotation(MapKeyJoinColumn.class), accessibleObject));
        }
        
        // Set the attribute overrides if some are present.
        m_mapKeyAttributeOverrides = new ArrayList<AttributeOverrideMetadata>();
        // Process the attribute overrides first.
        if (isAnnotationPresent(AttributeOverrides.class)) {
            for (Object attributeOverride : (Object[]) getAnnotation(AttributeOverrides.class).getAttributeArray("value")) {
                m_mapKeyAttributeOverrides.add(new AttributeOverrideMetadata((MetadataAnnotation)attributeOverride, accessibleObject));
            }
        }
        
        // Process the single attribute override second.  
        if (isAnnotationPresent(AttributeOverride.class)) {
            m_mapKeyAttributeOverrides.add(new AttributeOverrideMetadata(getAnnotation(AttributeOverride.class), accessibleObject));
        }
        
        // Set the association overrides if some are present.
        m_mapKeyAssociationOverrides = new ArrayList<AssociationOverrideMetadata>();
        // Process the attribute overrides first.
        if (isAnnotationPresent(AssociationOverrides.class)) {
            for (Object associationOverride : (Object[]) getAnnotation(AssociationOverrides.class).getAttributeArray("value")) {
                m_mapKeyAssociationOverrides.add(new AssociationOverrideMetadata((MetadataAnnotation)associationOverride, accessibleObject));
            }
        }
        
        // Process the single attribute override second.  
        if (isAnnotationPresent(AssociationOverride.class)) {
            m_mapKeyAssociationOverrides.add(new AssociationOverrideMetadata(getAnnotation(AssociationOverride.class), accessibleObject));
        }
        
        // Set the order column if one is defined.
        if (isAnnotationPresent(OrderColumn.class)) {
            String correctionType = null;
            if (isAnnotationPresent(OrderCorrection.class)) {
                correctionType = getAnnotation(OrderCorrection.class).getAttribute("value").toString();
            }
            m_orderColumn = new OrderColumnMetadata(getAnnotation(OrderColumn.class), accessibleObject, correctionType);
        }
        
        // Set the map key enumerated if one is defined.
        if (isAnnotationPresent(MapKeyEnumerated.class)) {
            m_mapKeyEnumerated = new EnumeratedMetadata(getAnnotation(MapKeyEnumerated.class), accessibleObject);
        }
        
        // Set the map key temporal if one is defined.
        if (isAnnotationPresent(MapKeyTemporal.class)) {
            m_mapKeyTemporal = new TemporalMetadata(getAnnotation(MapKeyTemporal.class), accessibleObject);
        }
        
        // Set the convert key if one is defined.
        if (isAnnotationPresent(MapKeyConvert.class)) {
            m_mapKeyConvert = (String) getAnnotation(MapKeyConvert.class).getAttribute("value");
        }
    }
    
    /**
     * INTERNAL:
     * Return the map key column for this accessor. Default one if necessary.
     */
    @Override
    protected ColumnMetadata getColumn(String loggingCtx) {
        if (loggingCtx.equals(MetadataLogger.MAP_KEY_COLUMN)) {
            if (m_mapKeyColumn == null) {
                return new ColumnMetadata(getAnnotation(MapKeyColumn.class), getAccessibleObject());
            } else {
                return m_mapKeyColumn;
            }
        } else {
            return super.getColumn(loggingCtx);
        }
    }

    
    
    /**
     * INTERNAL:
     * Return the default fetch type for a collection mapping.
     */
    public String getDefaultFetchType() {
        return FetchType.LAZY.name();
    }
    
    /**
     * INTERNAL:
     * Return the enumerated metadata for this accessor.
     */
    @Override
    public EnumeratedMetadata getEnumerated(boolean isForMapKey) {
        return getMapKeyEnumerated();
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    @Override
    public MapKeyMetadata getMapKey() {
        return m_mapKey;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<AssociationOverrideMetadata> getMapKeyAssociationOverrides() {
        return m_mapKeyAssociationOverrides;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<AttributeOverrideMetadata> getMapKeyAttributeOverrides() {
        return m_mapKeyAttributeOverrides;
    }
    
    /**
     * INTERNAL: 
     */
    public MetadataClass getMapKeyClass() {
        return m_mapKeyClass;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public String getMapKeyClassName() {
        return m_mapKeyClassName;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public ColumnMetadata getMapKeyColumn() {
        return m_mapKeyColumn;
    } 
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getMapKeyConvert() {
        return m_mapKeyConvert;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping. 
     */
    public EnumeratedMetadata getMapKeyEnumerated() {
        return m_mapKeyEnumerated;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public List<JoinColumnMetadata> getMapKeyJoinColumns() {
        return m_mapKeyJoinColumns;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping. 
     */
    public TemporalMetadata getMapKeyTemporal() {
        return m_mapKeyTemporal;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getMappedBy() {
        return m_mappedBy;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getOrderBy() {
        return m_orderBy; 
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    protected OrderColumnMetadata getOrderColumn() {
        return m_orderColumn;
    }
    
    /**
     * INTERNAL:
     * If a targetEntity is specified in metadata, it will be set as the 
     * reference class, otherwise we will look to extract one from generics.
     */
    @Override
    public MetadataClass getReferenceClass() {
        if (m_referenceClass == null) {
            m_referenceClass = getTargetEntity();
        
            if (m_referenceClass.isVoid()) {
                // This call will attempt to extract the reference class from generics.
                m_referenceClass = getReferenceClassFromGeneric();
        
                if (m_referenceClass == null) {
                    // Throw an exception. A relationship accessor must have a 
                    // reference class either through generics or a specified
                    // target entity on the mapping metadata.
                    throw ValidationException.unableToDetermineTargetEntity(getAttributeName(), getJavaClass());
                } else {
                    // Log the defaulting contextual reference class.
                    getLogger().logConfigMessage(getLoggingContext(), getAnnotatedElement(), m_referenceClass);
                }
            } 
        }
        
        return m_referenceClass;
    }
    
    /**
     * INTERNAL:
     * Return the reference table for this accessor. If it is a many to many
     * mapping, return the join table otherwise return the reference descriptors
     * primary key table.
     */
    @Override
    protected DatabaseTable getReferenceDatabaseTable() {
        if (getMapping().isManyToManyMapping()) {
            return ((ManyToManyMapping) getMapping()).getRelationTable();
        } else {
            return super.getReferenceDatabaseTable();
        }
    }
    
    /**
     * INTERNAL:
     * Return the temporal metadata for this accessor.
     */
    @Override
    public TemporalMetadata getTemporal(boolean isForMapKey) {
        return getMapKeyTemporal();
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor has map key convert metadata.
     */
    @Override
    protected boolean hasConvert(boolean isForMapKey) {
        return (isForMapKey) ? m_mapKeyConvert != null : super.hasConvert(isForMapKey); 
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor has enumerated metadata.
     */
    @Override
    public boolean hasEnumerated(boolean isForMapKey) {
        return (isForMapKey) ? m_mapKeyEnumerated != null : super.hasEnumerated(isForMapKey); 
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean hasMapKey() {
        return m_mapKey != null;
    }
    
    /**
     * INTERNAL:
     * Return true if the mapped by has been specified.
     */
    protected boolean hasMappedBy() {
        return m_mappedBy != null && ! m_mappedBy.equals("");
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor has temporal metadata.
     */
    @Override
    public boolean hasTemporal(boolean isForMapKey) {
        return (isForMapKey) ? m_mapKeyTemporal != null : super.hasTemporal(isForMapKey);  
    }
    
    /** 
     * INTERNAL:
     * Return true if this accessor represents a collection accessor.
     */
    @Override
    public boolean isCollectionAccessor() {
        return true;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        // Init the list of ORMetadata objects.
        initXMLObjects(m_mapKeyJoinColumns, accessibleObject);
        initXMLObjects(m_mapKeyAssociationOverrides, accessibleObject);
        initXMLObjects(m_mapKeyAttributeOverrides, accessibleObject);
        
        // Initialize single ORMetadata objects.
        initXMLObject(m_mapKey, accessibleObject);
        initXMLObject(m_mapKeyColumn, accessibleObject);
        initXMLObject(m_orderColumn, accessibleObject);
        
        // Initialize the map key class name we read from XML.
        m_mapKeyClass = initXMLClassName(m_mapKeyClassName);
    }
    
    /**
     * INTERNAL:
     * This process should do any common validation processing of collection 
     * accessors.
     */
    @Override
    public void process() {
        if (! getAccessibleObject().isSupportedToManyCollectionClass(getRawClass())) {
            throw ValidationException.invalidCollectionTypeForRelationship(getJavaClass(), getRawClass(), getAttributeName());
        }
    }
    
    /**
     * INTERNAL:
     */
    protected void process(CollectionMapping mapping) {
        // Set the mapping, this must be done first.
        setMapping(mapping);
        
        mapping.setIsReadOnly(false);
        mapping.setIsLazy(isLazy());
        mapping.setAttributeName(getAttributeName());
        mapping.setReferenceClassName(getReferenceClassName());
        
        // Process join fetch type.
        processJoinFetch(getJoinFetch(), mapping);
        
        // Process the batch fetch if specified.
        processBatchFetch(getBatchFetch(), mapping);
            
        // Process the orphanRemoval or PrivateOwned
        processOrphanRemoval(mapping);
        
        // Will check for PROPERTY access
        setAccessorMethods(mapping);

        // Process the cascade types.
        processCascadeTypes(mapping);
        
        // Process an OrderBy if there is one.
        processOrderBy(mapping);
        
        // Set the correct indirection on the collection mapping. Process the 
        // map metadata for a map key value to set on the indirection policy.
        // ** Note the reference class or reference class name needs to be set 
        // on the mapping before setting the indirection policy.
        processContainerPolicyAndIndirection(mapping);
        
        // Process a @ReturnInsert and @ReturnUpdate (to log a warning message)
        processReturnInsertAndUpdate();
        
        // Process the order column if specified.
        if (m_orderColumn != null) {
            m_orderColumn.process(mapping, getDescriptor());
        }
    }  
    
    /**
     * INTERNAL:
     * Process an association override for either an embedded object mapping, 
     * or a map mapping (element-collection, 1-M and M-M) containing an
     * embeddable object as the value or key. 
     */
    @Override
    protected void processAssociationOverride(AssociationOverrideMetadata associationOverride, EmbeddableMapping embeddableMapping, MetadataDescriptor owningDescriptor) {
        if (getMapping().isManyToManyMapping()) {
            JoinTableMetadata joinTable = associationOverride.getJoinTable();
        
            // Process any table defaults and log warning messages.
            String defaultName = owningDescriptor.getPrimaryTableName() + "_" + getReferenceDescriptor().getPrimaryTableName();
            processTable(joinTable, defaultName);
        
            // Create an override mapping and process the join table to it.
            ManyToManyMapping overrideMapping = new ManyToManyMapping();
            overrideMapping.setAttributeName(getAttributeName());
            processJoinTable(overrideMapping, overrideMapping.getRelationTableMechanism(), joinTable);
        
            // The override mapping will have the correct source, sourceRelation, 
            // target and targetRelation keys. Along with the correct relation table.
            embeddableMapping.addOverrideManyToManyMapping(overrideMapping);
        } else {
            super.processAssociationOverride(associationOverride, embeddableMapping, owningDescriptor);
        }
    }
    
    /**
     * INTERNAL:
     * Process an order by value (if specified) for the given collection 
     * mapping. Order by specifies the ordering of the elements of a collection 
     * valued association at the point when the association is retrieved.
     * 
     * The syntax of the value ordering element is an orderby_list, as follows:
     * 
     * orderby_list ::= orderby_item [, orderby_item]*
     * orderby_item ::= property_or_field_name [ASC | DESC]
     * 
     * When ASC or DESC is not specified, ASC is assumed.
     * 
     * If the ordering element is not specified, ordering by the primary key
     * of the associated entity is assumed.
     * 
     * The property or field name must correspond to that of a persistent
     * property or field of the associated class. The properties or fields 
     * used in the ordering must correspond to columns for which comparison
     * operators are supported.
     */
    protected void processOrderBy(CollectionMapping mapping) {
        if (m_orderBy != null) {
            MetadataDescriptor referenceDescriptor = getReferenceDescriptor();
            
            if (m_orderBy.equals("")) {
                // Default to the primary key field name(s).
                List<String> orderByAttributes = referenceDescriptor.getIdOrderByAttributeNames();
            
                if (referenceDescriptor.hasEmbeddedId()) {
                    String embeddedIdAttributeName = referenceDescriptor.getEmbeddedIdAttributeName();
                
                    for (String orderByAttribute : orderByAttributes) {
                        mapping.addAggregateOrderBy(embeddedIdAttributeName, orderByAttribute, false);
                    }
                } else {
                    for (String orderByAttribute : orderByAttributes) {
                        mapping.addOrderBy(orderByAttribute, false);
                    }
                }
            } else {
                StringTokenizer commaTokenizer = new StringTokenizer(m_orderBy, ",");
            
                while (commaTokenizer.hasMoreTokens()) {
                    StringTokenizer spaceTokenizer = new StringTokenizer(commaTokenizer.nextToken());
                    String propertyOrFieldName = spaceTokenizer.nextToken();
                    MappingAccessor referenceAccessor = referenceDescriptor.getAccessorFor(propertyOrFieldName);
                
                    if (referenceAccessor == null) {
                        throw ValidationException.invalidOrderByValue(propertyOrFieldName, referenceDescriptor.getJavaClass(), getAccessibleObjectName(), getJavaClass());
                    }

                    String attributeName = referenceAccessor.getAttributeName();                    
                    String ordering = (spaceTokenizer.hasMoreTokens()) ? spaceTokenizer.nextToken() : ASCENDING;

                    if (referenceAccessor.isEmbedded()) {
                        for (String orderByAttributeName : referenceDescriptor.getOrderByAttributeNames()) {
                            mapping.addAggregateOrderBy(m_orderBy, orderByAttributeName, ordering.equals(DESCENDING));        
                        }
                    } else if (referenceAccessor.getClassAccessor().isEmbeddableAccessor()) {
                        // We have a specific order by from an embeddable, we need to rip off 
                        // the last bit of a dot notation if specified and pass in the chained 
                        // string names of the nested embeddables only.
                        String embeddableChain = m_orderBy;
                        if (embeddableChain.contains(".")) {
                            embeddableChain = embeddableChain.substring(0, embeddableChain.lastIndexOf("."));
                        }
                        
                        mapping.addAggregateOrderBy(embeddableChain, attributeName, ordering.equals(DESCENDING)); 
                    } else {
                        mapping.addOrderBy(attributeName, ordering.equals(DESCENDING));    
                    }
                }
            }
        }
    } 
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setMapKey(MapKeyMetadata mapKey) {
        m_mapKey = mapKey;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setMapKeyAssociationOverrides(List<AssociationOverrideMetadata> mapKeyAssociationOverrides) {
        m_mapKeyAssociationOverrides = mapKeyAssociationOverrides;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setMapKeyAttributeOverrides(List<AttributeOverrideMetadata> mapKeyAttributeOverrides) {
        m_mapKeyAttributeOverrides = mapKeyAttributeOverrides;
    }
    
    /**
     * INTERNAL: 
     */
    public void setMapKeyClass(MetadataClass mapKeyClass) {
        m_mapKeyClass = mapKeyClass;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setMapKeyClassName(String mapKeyClassName) {
        m_mapKeyClassName = mapKeyClassName;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setMapKeyColumn(ColumnMetadata mapKeyColumn) {
        m_mapKeyColumn = mapKeyColumn;
    } 
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setMapKeyConvert(String mapKeyConvert) {
        m_mapKeyConvert = mapKeyConvert;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping. 
     */
    public void setMapKeyEnumerated(EnumeratedMetadata mapKeyEnumerated) {
        m_mapKeyEnumerated = mapKeyEnumerated;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setMapKeyJoinColumns(List<JoinColumnMetadata> mapKeyJoinColumns) {
        m_mapKeyJoinColumns = mapKeyJoinColumns;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping. 
     */
    public void setMapKeyTemporal(TemporalMetadata mapKeyTemporal) {
        m_mapKeyTemporal = mapKeyTemporal;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setMappedBy(String mappedBy) {
        m_mappedBy = mappedBy;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setOrderBy(String orderBy) {
        m_orderBy = orderBy;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setOrderColumn(OrderColumnMetadata orderColumn) {
        m_orderColumn = orderColumn;
    }
}
