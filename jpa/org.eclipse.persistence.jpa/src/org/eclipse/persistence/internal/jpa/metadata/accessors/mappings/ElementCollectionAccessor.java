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
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1) 
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 *     02/25/2009-2.0 Guy Pelletier 
 *       - 265359: JPA 2.0 Element Collections - Metadata processing portions
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/03/2009-2.0 Guy Pelletier
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     06/02/2009-2.0 Guy Pelletier 
 *       - 278768: JPA 2.0 Association Override Join Table
 *     06/09/2009-2.0 Guy Pelletier 
 *       - 249037: JPA 2.0 persisting list item index
 *     06/25/2009-2.0 Michael O'Brien 
 *       - 266912: change MappedSuperclass handling in stage2 to pre process accessors
 *          in support of the custom descriptors holding mappings required by the Metamodel 
 *     09/29/2009-2.0 Guy Pelletier 
 *       - 282553: JPA 2.0 JoinTable support for OneToOne and ManyToOne
 *     11/06/2009-2.0 Guy Pelletier 
 *       - 286317: UniqueConstraint xml element is changing (plus couple other fixes, see bug)
 *     03/08/2010-2.1 Guy Pelletier 
 *       - 303632: Add attribute-type for mapping attributes to EclipseLink-ORM
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     05/31/2010-2.1 Guy Pelletier 
 *       - 314941: multiple joinColumns without referenced column names defined, no error
 *     06/14/2010-2.2 Guy Pelletier 
 *       - 264417: Table generation is incorrect for JoinTables in AssociationOverrides
 *     09/03/2010-2.2 Guy Pelletier 
 *       - 317286: DB column lenght not in sync between @Column and @JoinColumn
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 *     06/03/2011-2.3.1 Guy Pelletier 
 *       - 347563: transient field/property in embeddable entity 
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.MapKey;
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.MapKeyJoinColumns;
import javax.persistence.MapKeyTemporal;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;

import org.eclipse.persistence.annotations.CompositeMember;
import org.eclipse.persistence.annotations.DeleteAll;
import org.eclipse.persistence.annotations.MapKeyConvert;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
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
import org.eclipse.persistence.internal.jpa.metadata.tables.CollectionTableMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.mappings.AggregateCollectionMapping;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.EmbeddableMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;

/**
 * An element collection accessor.
 * 
 * Used to support DirectCollection, DirectMap, AggregateCollection.
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
public class ElementCollectionAccessor extends DirectCollectionAccessor implements MappedKeyMapAccessor {
    private ColumnMetadata m_column;
    private ColumnMetadata m_mapKeyColumn;
    
    private EnumeratedMetadata m_mapKeyEnumerated;
    
    private List<AssociationOverrideMetadata> m_associationOverrides = new ArrayList<AssociationOverrideMetadata>();
    private List<AssociationOverrideMetadata> m_mapKeyAssociationOverrides = new ArrayList<AssociationOverrideMetadata>();
    private List<AttributeOverrideMetadata> m_attributeOverrides = new ArrayList<AttributeOverrideMetadata>();
    private List<AttributeOverrideMetadata> m_mapKeyAttributeOverrides = new ArrayList<AttributeOverrideMetadata>();
    private List<JoinColumnMetadata> m_mapKeyJoinColumns = new ArrayList<JoinColumnMetadata>(); 
    
    private MapKeyMetadata m_mapKey;
    private MetadataClass m_targetClass;
    private MetadataClass m_mapKeyClass;
    private MetadataClass m_referenceClass;
    
    private OrderColumnMetadata m_orderColumn;
    
    private String m_mapKeyConvert;
    private String m_mapKeyClassName;
    private String m_targetClassName;
    private String m_orderBy;
    private String m_compositeMember;
    
    private TemporalMetadata m_mapKeyTemporal;

    private Boolean m_deleteAll;

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public ElementCollectionAccessor() {
        super("<element-collection>");
    }
    
    /**
     * INTERNAL:
     */
    public ElementCollectionAccessor(MetadataAnnotation elementCollection, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(elementCollection, accessibleObject, classAccessor);
        
        // Set the target class.
        m_targetClass = getMetadataClass((String) elementCollection.getAttribute("targetClass"));
        
        // Set the attribute overrides if some are present.
        // Set the attribute overrides first if defined.
        if (isAnnotationPresent(AttributeOverrides.class)) {
            for (Object attributeOverride : (Object[]) getAnnotation(AttributeOverrides.class).getAttributeArray("value")) {
                addAttributeOverride(new AttributeOverrideMetadata((MetadataAnnotation)attributeOverride, this));
            }
        }
        
        // Set the single attribute override second if defined.
        if (isAnnotationPresent(AttributeOverride.class)) {
            addAttributeOverride(new AttributeOverrideMetadata(getAnnotation(AttributeOverride.class), this));
        }
        
        // Set the association overrides if some are present.
        // Set the association overrides first if defined.
        if (isAnnotationPresent(AssociationOverrides.class)) {
            for (MetadataAnnotation associationOverride : (MetadataAnnotation[]) getAnnotation(AssociationOverrides.class).getAttribute("value")) {
                addAssociationOverride(new AssociationOverrideMetadata(associationOverride, this));
            }
        }
        
        // Set the single association override second if defined.
        if (isAnnotationPresent(AssociationOverride.class)) {
            addAssociationOverride(new AssociationOverrideMetadata(getAnnotation(AssociationOverride.class), this));
        }
        
        // Set the column if one if defined.
        if (isAnnotationPresent(Column.class)) {
            m_column = new ColumnMetadata(getAnnotation(Column.class), this);
        }
        
        // Set the collection table if one is defined.
        if (isAnnotationPresent(CollectionTable.class)) {
            setCollectionTable(new CollectionTableMetadata(getAnnotation(CollectionTable.class), this, true));
        }
        
        if (isAnnotationPresent(CompositeMember.class)) {
            m_compositeMember = (String) getAnnotation(CompositeMember.class).getAttributeString("value");
        }
        
        // Set the order if one is present.
        if (isAnnotationPresent(OrderBy.class)) {
            m_orderBy = (String) getAnnotation(OrderBy.class).getAttributeString("value");
        }
        
        // Set the map key if one is defined.
        if (isAnnotationPresent(MapKey.class)) {
            m_mapKey = new MapKeyMetadata(getAnnotation(MapKey.class), this);
        }
        
        // Set the map key class if one is defined.
        if (isAnnotationPresent(MapKeyClass.class)) {
            m_mapKeyClass = getMetadataClass((String) getAnnotation(MapKeyClass.class).getAttribute("value"));
        }
        
        // Set the map key enumerated if one is defined.
        if (isAnnotationPresent(MapKeyEnumerated.class)) {
            m_mapKeyEnumerated = new EnumeratedMetadata(getAnnotation(MapKeyEnumerated.class), this);
        }
        
        // Set the map key temporal if one is defined.
        if (isAnnotationPresent(MapKeyTemporal.class)) {
            m_mapKeyTemporal = new TemporalMetadata(getAnnotation(MapKeyTemporal.class), this);
        }
        
        // Set the map key join columns if some are present.
        // Process all the map key join columns first.
        if (isAnnotationPresent(MapKeyJoinColumns.class)) {
            for (Object joinColumn : (Object[]) getAnnotation(MapKeyJoinColumns.class).getAttributeArray("value")) {
                m_mapKeyJoinColumns.add(new JoinColumnMetadata((MetadataAnnotation) joinColumn, this));
            }
        }
        
        if (isAnnotationPresent(MapKeyJoinColumn.class)) {
            m_mapKeyJoinColumns.add(new JoinColumnMetadata(getAnnotation(MapKeyJoinColumn.class), this));
        }
        
        // Set the map key column if one is defined.
        if (isAnnotationPresent(MapKeyColumn.class)) {
            m_mapKeyColumn = new ColumnMetadata(getAnnotation(MapKeyColumn.class), this);
        }
        
        // Set the convert key if one is defined.
        if (isAnnotationPresent(MapKeyConvert.class)) {
            m_mapKeyConvert = (String) getAnnotation(MapKeyConvert.class).getAttribute("value");
        }
        
        // Set the order column if one is defined.
        if (isAnnotationPresent(OrderColumn.class)) {
            m_orderColumn = new OrderColumnMetadata(getAnnotation(OrderColumn.class), this);
        }
        
        if (isAnnotationPresent(DeleteAll.class)) {
            m_deleteAll = Boolean.TRUE;
        }
    }
    
    /**
     * INTERNAL:
     */
    protected void addAttributeOverride(AttributeOverrideMetadata attributeOverride) {
        if (attributeOverride.getName().startsWith(KEY_DOT_NOTATION)) {
            attributeOverride.setName(attributeOverride.getName().substring(KEY_DOT_NOTATION.length()));
            m_mapKeyAttributeOverrides.add(attributeOverride);
        } else {
            if (attributeOverride.getName().startsWith(VALUE_DOT_NOTATION)) {
                attributeOverride.setName(attributeOverride.getName().substring(VALUE_DOT_NOTATION.length()));
            }
            
            m_attributeOverrides.add(attributeOverride);
        }
    }
    
    /**
     * INTERNAL:
     */
    protected void addAssociationOverride(AssociationOverrideMetadata associationOverride) {
        if (associationOverride.getName().startsWith(KEY_DOT_NOTATION)) {
            associationOverride.setName(associationOverride.getName().substring(KEY_DOT_NOTATION.length()));
            m_mapKeyAssociationOverrides.add(associationOverride);
        } else {
            if (associationOverride.getName().startsWith(VALUE_DOT_NOTATION)) {
                associationOverride.setName(associationOverride.getName().substring(VALUE_DOT_NOTATION.length()));
            }
            
            m_associationOverrides.add(associationOverride);
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof ElementCollectionAccessor) {
            ElementCollectionAccessor elementCollectionAccessor = (ElementCollectionAccessor) objectToCompare;
            
            if (! valuesMatch(m_column, elementCollectionAccessor.getColumn())) {
                return false;
            }
            
            if (! valuesMatch(m_compositeMember, elementCollectionAccessor.getCompositeMember())) {
                return false;
            }
            
            if (! valuesMatch(m_mapKeyColumn, elementCollectionAccessor.getMapKeyColumn())) {
                return false;
            }
            
            if (! valuesMatch(m_mapKeyEnumerated, elementCollectionAccessor.getMapKeyEnumerated())) {
                return false;
            }
            
            if (! valuesMatch(m_associationOverrides, elementCollectionAccessor.getAssociationOverrides())) {
                return false;
            }
            
            if (! valuesMatch(m_mapKeyAssociationOverrides, elementCollectionAccessor.getMapKeyAssociationOverrides())) {
                return false;
            }
            
            if (! valuesMatch(m_attributeOverrides, elementCollectionAccessor.getAttributeOverrides())) {
                return false;
            }
            
            if (! valuesMatch(m_mapKeyAttributeOverrides, elementCollectionAccessor.getMapKeyAttributeOverrides())) {
                return false;
            }
            
            if (! valuesMatch(m_mapKeyJoinColumns, elementCollectionAccessor.getMapKeyJoinColumns())) {
                return false;
            }
            
            if (! valuesMatch(m_mapKey, elementCollectionAccessor.getMapKey())) {
                return false;
            }
            
            if (! valuesMatch(m_orderColumn, elementCollectionAccessor.getOrderColumn())) {
                return false;
            }
            
            if (! valuesMatch(m_mapKeyConvert, elementCollectionAccessor.getMapKeyConvert())) {
                return false;
            }
            
            if (! valuesMatch(m_mapKeyClassName, elementCollectionAccessor.getMapKeyClassName())) {
                return false;
            }
            
            if (! valuesMatch(m_targetClassName, elementCollectionAccessor.getTargetClassName())) {
                return false;
            }
            
            if (! valuesMatch(m_orderBy, elementCollectionAccessor.getOrderBy())) {
                return false;
            }
            
            return valuesMatch(m_mapKeyTemporal, elementCollectionAccessor.getMapKeyTemporal());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<AssociationOverrideMetadata> getAssociationOverrides() {
        return m_associationOverrides;
    } 
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<AttributeOverrideMetadata> getAttributeOverrides() {
        return m_attributeOverrides;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public ColumnMetadata getColumn() {
        return m_column;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public String getCompositeMember() {
        return m_compositeMember;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    protected ColumnMetadata getColumn(String loggingCtx) {
        if (loggingCtx.equals(MetadataLogger.VALUE_COLUMN)) {
            return m_column == null ? super.getColumn(loggingCtx) : m_column;
        } else {
            return m_mapKeyColumn == null ? super.getColumn(loggingCtx) : m_mapKeyColumn;
        }
    }

    /**
     * INTERNAL:
     * Return the default table to hold the foreign key of a MapKey when
     * and Entity is used as the MapKey
     * @return
     */
    protected DatabaseTable getDefaultTableForEntityMapKey(){
        return getCollectionTable().getDatabaseTable();
    }
    
    public Boolean getDeleteAll() {
        return this.m_deleteAll;
    }
    
    /**
     * INTERNAL:
     */
    public EmbeddableAccessor getEmbeddableAccessor() {
        return getProject().getEmbeddableAccessor(getReferenceClass());
    }
    
    /**
     * INTERNAL:
     * Return the enumerated metadata for this accessor.
     */
    @Override
    public EnumeratedMetadata getEnumerated(boolean isForMapKey) {
        if (isForMapKey) {
            return getMapKeyEnumerated();
        } else {
            return super.getEnumerated(isForMapKey);
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    protected String getKeyConverter() {
        return m_mapKeyConvert;
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
     * Return the map key class on this element collection accessor.
     */
    public MetadataClass getMapKeyClass() {
        return m_mapKeyClass;
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
    public String getOrderBy() {
        return m_orderBy; 
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public OrderColumnMetadata getOrderColumn() {
        return m_orderColumn;
    }
    
    /**
     * INTERNAL:
     * If a targetEntity is specified in metadata, it will be set as the 
     * reference class, otherwise we will look to extract one from generics.
     * <p>
     * MappedSuperclass descriptors return Void when their parameterized generic reference class is null
     */
    @Override
    public MetadataClass getReferenceClass() {
        if (m_referenceClass == null) {
            m_referenceClass = getTargetClass();
        
            if (m_referenceClass == null || m_referenceClass.isVoid()) {
                // This call will attempt to extract the reference class from generics.
                m_referenceClass = getReferenceClassFromGeneric();
        
                if (m_referenceClass == null) {
                    // 266912: We do not handle the resolution of parameterized 
                    // generic types when the accessor is a MappedSuperclasses.
                    // the validation exception is relaxed in this case and
                    // void metadata class is returned.
                    if (getClassAccessor().isMappedSuperclass()) {
                        return getMetadataClass(Void.class);
                    }
                    
                    // Throw an exception. An element collection accessor must 
                    // have a reference class either through generics or a 
                    // specified target class on the mapping metadata.
                    throw ValidationException.unableToDetermineTargetClass(getAttributeName(), getJavaClass());
                } else {
                    // Log the defaulting contextual reference class.
                    getLogger().logConfigMessage(getLogger().ELEMENT_COLLECTION_MAPPING_REFERENCE_CLASS, getAnnotatedElement(), m_referenceClass);
                }
            }
        }
            
        return m_referenceClass;
    }
    
    /**
     * INTERNAL:
     * In an element collection case, when the collection is not an embeddable
     * collection, there is no notion of a reference descriptor, therefore we
     * return this accessors descriptor
     */
    @Override
    public MetadataDescriptor getReferenceDescriptor() {
        if (isDirectEmbeddableCollection()) {
            return getEmbeddableAccessor().getDescriptor();
        } else {
            return super.getReferenceDescriptor();
        }
    }
    
    /**
     * INTERNAL:
     * Return the target class for this accessor.
     */
    protected MetadataClass getTargetClass() {
        return m_targetClass;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    protected String getTargetClassName() {
        return m_targetClassName;
    }

    /**
     * INTERNAL:
     * Return the temporal metadata for this accessor.
     * @see DirectAccessor
     * @see CollectionAccessor
     */
    @Override
    public TemporalMetadata getTemporal(boolean isForMapKey) {
        if (isForMapKey) {
            return getMapKeyTemporal();
        } else {
            return super.getTemporal(isForMapKey);
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    protected boolean hasConvert(boolean isForMapKey) {
        if (isForMapKey) {
            return m_mapKeyConvert != null;
        } else {
            return super.hasConvert(isForMapKey);
        }
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor has enumerated metadata.
     */
    @Override
    public boolean hasEnumerated(boolean isForMapKey) {
        if (isForMapKey) {
            return m_mapKeyEnumerated != null;
        } else {
            return super.hasEnumerated(isForMapKey);
        }
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor has lob metadata.
     */
    @Override
    public boolean hasLob(boolean isForMapKey) {
        if (isForMapKey) {
            return false;
        } else {
            return super.hasLob(isForMapKey);
        }
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
     * Return true if this accessor has a map key class specified.
     */
    @Override
    protected boolean hasMapKeyClass() {
        return m_mapKeyClass != null && ! m_mapKeyClass.equals(void.class);
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor has temporal metadata.
     */
    @Override
    public boolean hasTemporal(boolean isForMapKey) {
        if (isForMapKey) {
            return this.m_mapKeyTemporal != null;
        } else {
            return super.hasTemporal(isForMapKey);
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        // Initialize lists of ORMetadata objects.
        initXMLObjects(m_attributeOverrides, accessibleObject);
        initXMLObjects(m_associationOverrides, accessibleObject);
        initXMLObjects(m_mapKeyAssociationOverrides, accessibleObject);
        initXMLObjects(m_mapKeyAttributeOverrides, accessibleObject);
        
        // Initialize single objects.
        initXMLObject(m_column, accessibleObject);
        initXMLObject(m_mapKey, accessibleObject);
        initXMLObject(m_mapKeyColumn, accessibleObject);
        initXMLObject(m_orderColumn, accessibleObject);
        
        // Initialize the any class names we read from XML.
        m_targetClass = initXMLClassName(m_targetClassName);
        m_mapKeyClass = initXMLClassName(m_mapKeyClassName);
    }
    
    /**
     * INTERNAL:
     * Return true if this element collection contains embeddable objects.
     */
    @Override
    public boolean isDirectEmbeddableCollection() {
        return getEmbeddableAccessor() != null;
    }
    
    /**
     * INTERNAL:
     * Used by our XML writing facility.
     * Returns false unless m_deleteAll is both set and true
     * @return
     */
    public boolean isDeleteAll(){
        if (m_deleteAll != null && m_deleteAll){
            return true;
        }
        return false;
    }
    
    /**
     * INTERNAL: 
     * Process the element collection metadata.
     */
    @Override
    public void process() {
        if (isDirectEmbeddableCollection()) { 
            processDirectEmbeddableCollectionMapping(getReferenceDescriptor());
        } else if (isValidDirectCollectionType()) {
            processDirectCollectionMapping();
            if (m_compositeMember != null) {
                ((CollectionMapping)this.getMapping()).setSessionName(m_compositeMember);
            }
        } else if (isValidDirectMapType()) {
            processDirectMapMapping();
            if (m_compositeMember != null) {
                ((CollectionMapping)this.getMapping()).setSessionName(m_compositeMember);
            }
        } else {
            throw ValidationException.invalidTargetClass(getAttributeName(), getJavaClass());
        }
        
        if (m_orderColumn != null) {
            m_orderColumn.process((CollectionMapping) getMapping(), getDescriptor());
        }
    }
    
    /**
     * INTERNAL:
     * Process a MetadataCollectionTable.
     */
    @Override
    protected void processCollectionTable(CollectionMapping mapping) {
        super.processCollectionTable(mapping);
        
        // Add all the joinColumns (reference key fields) to the mapping. Join 
        // column validation is performed in the getJoinColumns call.
        for (JoinColumnMetadata joinColumn : getJoinColumns(getCollectionTable().getJoinColumns(), getOwningDescriptor())) {
            // Look up the primary key field from the referenced column name.
            DatabaseField pkField = getReferencedField(joinColumn.getReferencedColumnName(), getOwningDescriptor(), MetadataLogger.PK_COLUMN, mapping.isAggregateCollectionMapping());
            
            // The default name is the primary key of the owning entity.
            DatabaseField fkField = joinColumn.getForeignKeyField(pkField);
            
            setFieldName(fkField, getOwningDescriptor().getAlias() + "_" + getOwningDescriptor().getPrimaryKeyFieldName(), MetadataLogger.FK_COLUMN);
            fkField.setTable(getReferenceDatabaseTable());
                
            if (mapping.isDirectCollectionMapping()) {
                // Add the reference key field for the direct collection mapping.
                ((DirectCollectionMapping) mapping).addReferenceKeyField(fkField, pkField);
            } else {
                ((AggregateCollectionMapping) mapping).addTargetForeignKeyField(fkField, pkField);
            }
        }
        
        if (m_deleteAll != null && mapping.isPrivateOwned()){
            mapping.setMustDeleteReferenceObjectsOneByOne(!m_deleteAll);
        }
    }
    
    /**
     * INTERNAL:
     */
    protected void processDirectEmbeddableCollectionMapping(MetadataDescriptor referenceDescriptor) {
        // Initialize our mapping.
        AggregateCollectionMapping mapping = new AggregateCollectionMapping();
        
        // Process common direct collection metadata. This must be done 
        // before any field processing since field processing requires that 
        // the collection table be processed before hand.
        process(mapping);
        
        // Process the fetch type and set the correct indirection on the mapping.
        processContainerPolicyAndIndirection(mapping);
        
        // Make sure to mark the descriptor as an embeddable collection descriptor.
        referenceDescriptor.setIsEmbeddable();
        
        // Process the mappings from the embeddable to setup the field name 
        // translations. Before we do that lets process the attribute and
        // association overrides that are available to us and that may be used
        // to override any field name translations.
        processMappingsFromEmbeddable(referenceDescriptor, null, mapping, getAttributeOverrides(m_attributeOverrides), getAssociationOverrides(m_associationOverrides), "");
    }
    
    /**
     * INTERNAL:
     */
    protected void processMappingsFromEmbeddable(MetadataDescriptor embeddableDescriptor, AggregateObjectMapping nestedAggregateObjectMapping, EmbeddableMapping embeddableMapping, Map<String, AttributeOverrideMetadata> attributeOverrides, Map<String, AssociationOverrideMetadata> associationOverrides, String dotNotationName) {        
        for (MappingAccessor mappingAccessor : embeddableDescriptor.getMappingAccessors()) {
            // Ignore XML mapped transient attributes.
            if (! mappingAccessor.isTransient()) {
                // Fast track any mapping accessor that hasn't been processed at
                // this point. The only accessors that can't be processed here 
                // are nested embedded or element collection accessors.
                if (! mappingAccessor.isProcessed()) {
                    mappingAccessor.process();
                }
                
                // Now you can safely grab the mapping off the accessor.
                DatabaseMapping mapping = mappingAccessor.getMapping();
                
                // Figure out what our override name is to ensure we find and 
                // apply the correct override metadata.
                String overrideName = (dotNotationName.equals("")) ? mapping.getAttributeName() : dotNotationName + "." + mapping.getAttributeName();
                
                if (mapping.isDirectToFieldMapping()) {
                    // Regardless if we have an attribute override or not we
                    // add field name translations for every mapping to ensure
                    // we have the correct table name set for each field.
                    DirectToFieldMapping directMapping = (DirectToFieldMapping) mapping;
                    
                    DatabaseField overrideField;
                    if (attributeOverrides.containsKey(overrideName)) {
                        // We have an explicit attribute override we must apply.
                        overrideField = attributeOverrides.get(overrideName).getOverrideField();
                    } else {
                        // If the nested aggregate object mapping defined an 
                        // attribute override use the override field it set (and 
                        // qualify it with our collection table. Otherwise, default 
                        // a field name translation using the name of the field on 
                        // the mapping.
                        overrideField = directMapping.getField().clone();
                        
                        if (nestedAggregateObjectMapping != null && nestedAggregateObjectMapping.getAggregateToSourceFields().containsKey(overrideField.getName())) {
                            overrideField = nestedAggregateObjectMapping.getAggregateToSourceFields().get(overrideField.getName());
                        } 
                    }
                    
                    // Add the aggregate collection table field if one hasn't 
                    // already been set.
                    if (! overrideField.hasTableName()) {
                        overrideField.setTable(getReferenceDatabaseTable());
                    }
                    
                    addFieldNameTranslation(embeddableMapping, overrideName, overrideField, mappingAccessor);
                } else if (mapping.isOneToOneMapping()) {
                    OneToOneMapping oneToOneMapping = (OneToOneMapping) mapping;
                    
                    if (oneToOneMapping.isForeignKeyRelationship()) {
                        AssociationOverrideMetadata associationOverride = associationOverrides.get(overrideName);
                        
                        if (associationOverride == null) {
                            for (DatabaseField fkField : oneToOneMapping.getForeignKeyFields()) {
                                DatabaseField collectionTableField = fkField.clone();
                                collectionTableField.setTable(getReferenceDatabaseTable());
                                embeddableMapping.addFieldTranslation(collectionTableField, fkField.getName());
                            }
                        } else {
                            ((ObjectAccessor) mappingAccessor).processAssociationOverride(associationOverride, embeddableMapping, getReferenceDatabaseTable(), getDescriptor());
                        }
                    } else {
                        // Section 2.6 of the spec states: "An embeddable class (including an embeddable class within 
                        // another embeddable class) contained within an element collection must not contain an element 
                        // collection, nor may it contain a relationship to an entity other than a many-to-one or 
                        // one-to-one relationship. The embeddable class must be on the owning side of such a 
                        // relationship and the relationship must be mapped by a foreign key mapping."
                        throw ValidationException.invalidEmbeddableClassForElementCollection(embeddableDescriptor.getJavaClass(), getAttributeName(), getJavaClass(), mapping.getAttributeName());
                    }
                } else if (mapping.isAggregateObjectMapping()) {
                    MappingAccessor accessor = embeddableDescriptor.getMappingAccessor(mapping.getAttributeName());
                    processMappingsFromEmbeddable(accessor.getReferenceDescriptor(), (AggregateObjectMapping) mapping, embeddableMapping, attributeOverrides, associationOverrides, overrideName);
                } else {
                    // TODO: mapping.isAggregateCollectionMapping. We could 
                    // handle this case however not obligated by the spec.
                    // See comment above about section 2.6
                    throw ValidationException.invalidEmbeddableClassForElementCollection(embeddableDescriptor.getJavaClass(), getAttributeName(), getJavaClass(), mapping.getAttributeName());
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setAssociationOverrides(List<AssociationOverrideMetadata> associationOverrides) {
        m_associationOverrides = associationOverrides;
    } 
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setAttributeOverrides(List<AttributeOverrideMetadata> attributeOverrides) {
        m_attributeOverrides = attributeOverrides;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setColumn(ColumnMetadata column) {
        m_column = column;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setCompositeMember(String compositeMember) {
        m_compositeMember = compositeMember;
    }
    
    public void setDeleteAll(Boolean m_deleteAll) {
        this.m_deleteAll = m_deleteAll;
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

    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setTargetClassName(String targetClassName) {
        m_targetClassName = targetClassName;
    }
}
