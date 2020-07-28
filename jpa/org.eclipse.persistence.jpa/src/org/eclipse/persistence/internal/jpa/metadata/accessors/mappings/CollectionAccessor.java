/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2018 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     05/16/2008-1.0M8 Guy Pelletier
//       - 218084: Implement metadata merging functionality between mapping files
//     06/20/2008-1.0 Guy Pelletier
//       - 232975: Failure when attribute type is generic
//     01/28/2009-2.0 Guy Pelletier
//       - 248293: JPA 2.0 Element Collections (part 1)
//     02/06/2009-2.0 Guy Pelletier
//       - 248293: JPA 2.0 Element Collections (part 2)
//     02/26/2009-2.0 Guy Pelletier
//       - 264001: dot notation for mapped-by and order-by
//     03/27/2009-2.0 Guy Pelletier
//       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
//     04/03/2009-2.0 Guy Pelletier
//       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
//     04/24/2009-2.0 Guy Pelletier
//       - 270011: JPA 2.0 MappedById support
//     05/1/2009-2.0 Guy Pelletier
//       - 249033: JPA 2.0 Orphan removal
//     06/02/2009-2.0 Guy Pelletier
//       - 278768: JPA 2.0 Association Override Join Table
//     06/09/2009-2.0 Guy Pelletier
//       - 249037: JPA 2.0 persisting list item index
//     09/29/2009-2.0 Guy Pelletier
//       - 282553: JPA 2.0 JoinTable support for OneToOne and ManyToOne
//     11/06/2009-2.0 Guy Pelletier
//       - 286317: UniqueConstraint xml element is changing (plus couple other fixes, see bug)
//     03/08/2010-2.1 Guy Pelletier
//       - 303632: Add attribute-type for mapping attributes to EclipseLink-ORM
//     03/29/2010-2.1 Guy Pelletier
//       - 267217: Add Named Access Type to EclipseLink-ORM
//     04/27/2010-2.1 Guy Pelletier
//       - 309856: MappedSuperclasses from XML are not being initialized properly
//     06/14/2010-2.2 Guy Pelletier
//       - 264417: Table generation is incorrect for JoinTables in AssociationOverrides
//     01/25/2011-2.3 Guy Pelletier
//       - 333913: @OrderBy and <order-by/> without arguments should order by primary
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     10/09/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     11/19/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support) (foreign key metadata support)
//     11/28/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     07/16/2013-2.5.1 Guy Pelletier
//       - 412384: Applying Converter for parameterized basic-type for joda-time's DateTime does not work
//     07/01/2014-2.5.3 Rick Curtis
//       - 375101: Date and Calendar should not require @Temporal.
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.annotations.DeleteAll;
import org.eclipse.persistence.annotations.MapKeyConvert;
import org.eclipse.persistence.eis.mappings.EISOneToManyMapping;
import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

import org.eclipse.persistence.internal.jpa.metadata.columns.AssociationOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.AttributeOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.ForeignKeyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.OrderColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.ConvertMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.EnumeratedMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.TemporalMetadata;
import org.eclipse.persistence.internal.jpa.metadata.mappings.MapKeyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.mappings.OrderByMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.JoinTableMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.EmbeddableMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;

import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ATTRIBUTE_OVERRIDE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ATTRIBUTE_OVERRIDES;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ASSOCIATION_OVERRIDE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ASSOCIATION_OVERRIDES;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_FETCH_LAZY;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_MAP_KEY;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_MAP_KEY_CLASS;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_MAP_KEY_COLUMN;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_MAP_KEY_ENUMERATED;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_MAP_KEY_JOIN_COLUMN;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_MAP_KEY_JOIN_COLUMNS;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_MAP_KEY_TEMPORAL;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ORDER_BY;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ORDER_COLUMN;

/**
 * INTERNAL:
 * A relational collection mapping accessor.
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
 * @since TopLink EJB 3.0 Reference Implementation
 */
public abstract class CollectionAccessor extends RelationshipAccessor implements MappedKeyMapAccessor {
    private Boolean m_deleteAll;

    private ColumnMetadata m_mapKeyColumn;
    private EnumeratedMetadata m_mapKeyEnumerated;
    private ForeignKeyMetadata m_mapKeyForeignKey;

    private List<ConvertMetadata> m_mapKeyConverts;
    private List<AssociationOverrideMetadata> m_mapKeyAssociationOverrides = new ArrayList<AssociationOverrideMetadata>();
    private List<AttributeOverrideMetadata> m_mapKeyAttributeOverrides = new ArrayList<AttributeOverrideMetadata>();
    private List<JoinColumnMetadata> m_mapKeyJoinColumns = new ArrayList<JoinColumnMetadata>();

    private MapKeyMetadata m_mapKey;
    private MetadataClass m_mapKeyClass;
    private OrderByMetadata m_orderBy;
    private OrderColumnMetadata m_orderColumn;

    private String m_mapKeyConvert;
    private String m_mapKeyClassName;

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

        setMappedBy((annotation == null) ? "" : annotation.getAttributeString("mappedBy"));

        // Set the order if one is present.
        if (isAnnotationPresent(JPA_ORDER_BY)) {
            m_orderBy = new OrderByMetadata(getAnnotation(JPA_ORDER_BY), this);
        }

        // Set the map key if one is present.
        if (isAnnotationPresent(JPA_MAP_KEY)) {
            m_mapKey = new MapKeyMetadata(getAnnotation(JPA_MAP_KEY), this);
        }

        // Set the map key class if one is defined.
        if (isAnnotationPresent(JPA_MAP_KEY_CLASS)) {
            m_mapKeyClass = getMetadataClass(getAnnotation(JPA_MAP_KEY_CLASS).getAttributeString("value"));
        }

        // Set the map key column if one is defined.
        if (isAnnotationPresent(JPA_MAP_KEY_COLUMN)) {
            m_mapKeyColumn = new ColumnMetadata(getAnnotation(JPA_MAP_KEY_COLUMN), this);
        }

        // Set the map key join columns if some are present.
        // Process all the map key join columns first.
        if (isAnnotationPresent(JPA_MAP_KEY_JOIN_COLUMNS)) {
            MetadataAnnotation mapKeyJoinColumns = getAnnotation(JPA_MAP_KEY_JOIN_COLUMNS);

            for (Object mapKeyJoinColumn : mapKeyJoinColumns.getAttributeArray("value")) {
                m_mapKeyJoinColumns.add(new JoinColumnMetadata((MetadataAnnotation) mapKeyJoinColumn, this));
            }

            // Set the map key foreign key metadata if one is specified.
            if (mapKeyJoinColumns.hasAttribute("foreignKey")) {
                setMapKeyForeignKey(new ForeignKeyMetadata(mapKeyJoinColumns.getAttributeAnnotation("foreignKey"), this));
            }
        }

        // Process the single map key key join column second.
        if (isAnnotationPresent(JPA_MAP_KEY_JOIN_COLUMN)) {
            JoinColumnMetadata mapKeyJoinColumn = new JoinColumnMetadata(getAnnotation(JPA_MAP_KEY_JOIN_COLUMN), this);
            m_mapKeyJoinColumns.add(mapKeyJoinColumn);

            // Set the map key foreign key metadata.
            setMapKeyForeignKey(mapKeyJoinColumn.getForeignKey());
        }

        // Set the attribute overrides if some are present.
        // Process the attribute overrides first.
        if (isAnnotationPresent(JPA_ATTRIBUTE_OVERRIDES)) {
            for (Object attributeOverride : (Object[]) getAnnotation(JPA_ATTRIBUTE_OVERRIDES).getAttributeArray("value")) {
                addAttributeOverride(new AttributeOverrideMetadata((MetadataAnnotation) attributeOverride, this));
            }
        }

        // Process the single attribute override second.
        if (isAnnotationPresent(JPA_ATTRIBUTE_OVERRIDE)) {
            addAttributeOverride(new AttributeOverrideMetadata(getAnnotation(JPA_ATTRIBUTE_OVERRIDE), this));
        }

        // Set the association overrides if some are present.
        // Process the attribute overrides first.
        if (isAnnotationPresent(JPA_ASSOCIATION_OVERRIDES)) {
            for (Object associationOverride : (Object[]) getAnnotation(JPA_ASSOCIATION_OVERRIDES).getAttributeArray("value")) {
                addAssociationOverride(new AssociationOverrideMetadata((MetadataAnnotation) associationOverride, this));
            }
        }

        // Process the single attribute override second.
        if (isAnnotationPresent(JPA_ASSOCIATION_OVERRIDE)) {
            addAssociationOverride(new AssociationOverrideMetadata(getAnnotation(JPA_ASSOCIATION_OVERRIDE), this));
        }

        // Set the order column if one is defined.
        if (isAnnotationPresent(JPA_ORDER_COLUMN)) {
            m_orderColumn = new OrderColumnMetadata(getAnnotation(JPA_ORDER_COLUMN), this);
        }

        // Set the map key enumerated if one is defined.
        if (isAnnotationPresent(JPA_MAP_KEY_ENUMERATED)) {
            m_mapKeyEnumerated = new EnumeratedMetadata(getAnnotation(JPA_MAP_KEY_ENUMERATED), this);
        }

        // Set the map key temporal if one is defined.
        if (isAnnotationPresent(JPA_MAP_KEY_TEMPORAL)) {
            m_mapKeyTemporal = new TemporalMetadata(getAnnotation(JPA_MAP_KEY_TEMPORAL), this);
        }

        // Set the convert key if one is defined.
        if (isAnnotationPresent(MapKeyConvert.class)) {
            m_mapKeyConvert = getAnnotation(MapKeyConvert.class).getAttributeString("value");
        }

        if (isAnnotationPresent(DeleteAll.class)) {
            if (isPrivateOwned()){
                m_deleteAll = Boolean.TRUE;
            }
        }
    }

    /**
     * INTERNAL:
     * Add the association override to our map key attribute overrides list. If
     * it uses the 'key.' notation rip it off, otherwise use what is specified.
     */
    protected void addAssociationOverride(AssociationOverrideMetadata associationOverride) {
        if (associationOverride.getName().startsWith(KEY_DOT_NOTATION)) {
            associationOverride.setName(associationOverride.getName().substring(KEY_DOT_NOTATION.length()));
        }

        m_mapKeyAssociationOverrides.add(associationOverride);
    }

    /**
     * INTERNAL:
     * Add the attribute override to our map key attribute overrides list. If
     * it uses the 'key.' notation rip it off, otherwise use what is specified.
     */
    protected void addAttributeOverride(AttributeOverrideMetadata attributeOverride) {
        if (attributeOverride.getName().startsWith(KEY_DOT_NOTATION)) {
            attributeOverride.setName(attributeOverride.getName().substring(KEY_DOT_NOTATION.length()));
        }

        m_mapKeyAttributeOverrides.add(attributeOverride);
    }

    /**
     * INTERNAL:
     * A map key convert from an annotation specification. In XML, this list
     * is populated using the map-key-convert element. In annotations there is
     * only a single Convert annotation and map key converts are identified
     * with an attribute name on the convert beginning with "key".
     */
    @Override
    public void addMapKeyConvert(ConvertMetadata convert) {
        if (m_mapKeyConverts == null) {
            m_mapKeyConverts = new ArrayList<ConvertMetadata>();
        }

        m_mapKeyConverts.add(convert);
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof CollectionAccessor) {
            CollectionAccessor collectionAccessor = (CollectionAccessor) objectToCompare;

            if (! valuesMatch(m_mapKeyColumn, collectionAccessor.getMapKeyColumn())) {
                return false;
            }

            if (! valuesMatch(m_mapKeyEnumerated, collectionAccessor.getMapKeyEnumerated())) {
                return false;
            }

            if (! valuesMatch(m_mapKeyAssociationOverrides, collectionAccessor.getMapKeyAssociationOverrides())) {
                return false;
            }

            if (! valuesMatch(m_mapKeyAttributeOverrides, collectionAccessor.getMapKeyAttributeOverrides())) {
                return false;
            }

            if (! valuesMatch(m_mapKeyJoinColumns, collectionAccessor.getMapKeyJoinColumns())) {
                return false;
            }

            if (! valuesMatch(m_mapKeyForeignKey, collectionAccessor.getMapKeyForeignKey())) {
                return false;
            }

            if (! valuesMatch(m_mapKey, collectionAccessor.getMapKey())) {
                return false;
            }

            if (! valuesMatch(m_orderBy, collectionAccessor.getOrderBy())) {
                return false;
            }

            if (! valuesMatch(m_orderColumn, collectionAccessor.getOrderColumn())) {
                return false;
            }

            if (! valuesMatch(m_mapKeyConverts, collectionAccessor.getMapKeyConverts())) {
                return false;
            }

            if (! valuesMatch(m_mapKeyClassName, collectionAccessor.getMapKeyClassName())) {
                return false;
            }

            if (! valuesMatch(m_deleteAll, collectionAccessor.getDeleteAll())) {
                return false;
            }

            return valuesMatch(m_mapKeyTemporal, collectionAccessor.getMapKeyTemporal());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (m_deleteAll != null ? m_deleteAll.hashCode() : 0);
        result = 31 * result + (m_mapKeyColumn != null ? m_mapKeyColumn.hashCode() : 0);
        result = 31 * result + (m_mapKeyEnumerated != null ? m_mapKeyEnumerated.hashCode() : 0);
        result = 31 * result + (m_mapKeyForeignKey != null ? m_mapKeyForeignKey.hashCode() : 0);
        result = 31 * result + (m_mapKeyConverts != null ? m_mapKeyConverts.hashCode() : 0);
        result = 31 * result + (m_mapKeyAssociationOverrides != null ? m_mapKeyAssociationOverrides.hashCode() : 0);
        result = 31 * result + (m_mapKeyAttributeOverrides != null ? m_mapKeyAttributeOverrides.hashCode() : 0);
        result = 31 * result + (m_mapKeyJoinColumns != null ? m_mapKeyJoinColumns.hashCode() : 0);
        result = 31 * result + (m_mapKey != null ? m_mapKey.hashCode() : 0);
        result = 31 * result + (m_orderBy != null ? m_orderBy.hashCode() : 0);
        result = 31 * result + (m_orderColumn != null ? m_orderColumn.hashCode() : 0);
        result = 31 * result + (m_mapKeyClassName != null ? m_mapKeyClassName.hashCode() : 0);
        result = 31 * result + (m_mapKeyTemporal != null ? m_mapKeyTemporal.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     * Return the map key column for this accessor. Default one if necessary.
     */
    @Override
    protected ColumnMetadata getColumn(String loggingCtx) {
        if (loggingCtx.equals(MetadataLogger.MAP_KEY_COLUMN)) {
            if (m_mapKeyColumn == null) {
                return new ColumnMetadata(getAnnotation(JPA_MAP_KEY_COLUMN), this);
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
        return JPA_FETCH_LAZY;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getDeleteAll() {
        return m_deleteAll;
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
     * Future: this method is where we would provide a more explicit reference
     * class to support an auto-apply jpa converter. Per the spec auto-apply
     * converters are applied against basics only.
     */
    public MetadataClass getMapKeyClassWithGenerics() {
        return getMapKeyClass();
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
     */
    public String getMapKeyConvert() {
        return m_mapKeyConvert;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<ConvertMetadata> getMapKeyConverts() {
        return m_mapKeyConverts;
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
    public ForeignKeyMetadata getMapKeyForeignKey() {
        return m_mapKeyForeignKey;
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
    public OrderByMetadata getOrderBy() {
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

    @Override
    public void setTemporal(TemporalMetadata metadata, boolean isForMapKey) {
        m_mapKeyTemporal = metadata;
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
     * Used by our XML writing facility.
     * Returns false unless m_deleteAll is both set and true
     */
    public boolean isDeleteAll() {
        return m_deleteAll != null && m_deleteAll;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);

        // Init the list of ORMetadata objects.
        initXMLObjects(m_mapKeyConverts, accessibleObject);
        initXMLObjects(m_mapKeyJoinColumns, accessibleObject);
        initXMLObjects(m_mapKeyAssociationOverrides, accessibleObject);
        initXMLObjects(m_mapKeyAttributeOverrides, accessibleObject);

        // Initialize single ORMetadata objects.
        initXMLObject(m_mapKey, accessibleObject);
        initXMLObject(m_mapKeyColumn, accessibleObject);
        initXMLObject(m_orderBy, accessibleObject);
        initXMLObject(m_orderColumn, accessibleObject);
        initXMLObject(m_mapKeyForeignKey, accessibleObject);

        // Initialize the map key class name we read from XML.
        m_mapKeyClass = initXMLClassName(m_mapKeyClassName);

        // Initialize a previous text element from a list of elements (legacy)
        m_mapKeyConvert = initXMLTextObject(m_mapKeyConverts);
    }

    /**
     * INTERNAL:
     * This process should do any common validation processing of collection
     * accessors.
     */
    @Override
    public void process() {
        super.process();

        if (! getAccessibleObject().isSupportedToManyCollectionClass(getRawClass())) {
            throw ValidationException.invalidCollectionTypeForRelationship(getJavaClass(), getRawClass(), getAttributeName());
        }
    }

    /**
     * Configure the CollectionMapping properties based on the metadata.
     */
    protected void process(CollectionMapping mapping) {
        // Process common properties first.
        processRelationshipMapping(mapping);

        // Process an OrderBy if there is one.
        if (m_orderBy != null) {
            m_orderBy.process(mapping, getReferenceDescriptor(), getJavaClass());
        }

        // Process the order column if specified.
        if (m_orderColumn != null) {
            m_orderColumn.process(mapping, getDescriptor());
        }

        // Process the delete all setting
        if (m_deleteAll != null && mapping.isPrivateOwned()) {
            mapping.setMustDeleteReferenceObjectsOneByOne(! m_deleteAll);
        }

        // Set the correct indirection on the collection mapping. Process the
        // map metadata for a map key value to set on the indirection policy.
        // ** Note the reference class or reference class name needs to be set
        // on the mapping before setting the indirection policy.
        processContainerPolicyAndIndirection(mapping);

        // Process a @ReturnInsert and @ReturnUpdate (to log a warning message)
        processReturnInsertAndUpdate();
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

            // Set the override mapping which will have the correct metadata
            // set. This is the metadata any non-owning relationship accessor
            // referring to this accessor will need.
            setOverrideMapping(overrideMapping);
        } else {
            super.processAssociationOverride(associationOverride, embeddableMapping, owningDescriptor);
        }
    }

    /**
     * INTERNAL:
     * Configure the EISOneToManyMapping properties based on the metadata.
     */
    protected void processEISOneToManyMapping(EISOneToManyMapping mapping) {
        // EIS mappings always use foreign keys.
        String defaultFKFieldName = getDefaultAttributeName() + "_" + getReferenceDescriptor().getPrimaryKeyFieldName();

        // Get the join columns (directly or through an association override),
        // init them and validate.
        List<JoinColumnMetadata> joinColumns = getJoinColumns(getJoinColumns(), getReferenceDescriptor());

        // Get the foreign key (directly or through an association override) and
        // make sure it is initialized for processing.
        ForeignKeyMetadata foreignKey = getForeignKey(getForeignKey(), getReferenceDescriptor());

        // Now process the foreign key relationship metadata.
        processForeignKeyRelationship(mapping, joinColumns, foreignKey, getReferenceDescriptor(), defaultFKFieldName, getDescriptor().getPrimaryTable());

        if (getReferenceDescriptor().getPrimaryKeyFields().size() > 1) {
            mapping.setForeignKeyGroupingElement((getDatabaseField(getDescriptor().getPrimaryTable(), MetadataLogger.COLUMN)));
        }
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDeleteAll(Boolean deleteAll) {
        m_deleteAll = deleteAll;
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
    public void setMapKeyConverts(List<ConvertMetadata> mapKeyConverts) {
        m_mapKeyConverts = mapKeyConverts;
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
    public void setMapKeyForeignKey(ForeignKeyMetadata mapKeyForeignKey) {
        m_mapKeyForeignKey = mapKeyForeignKey;
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
    public void setOrderBy(OrderByMetadata orderBy) {
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
