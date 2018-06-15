/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     01/28/2009-2.0 Guy Pelletier
//       - 248293: JPA 2.0 Element Collections (part 1)
//     02/06/2009-2.0 Guy Pelletier
//       - 248293: JPA 2.0 Element Collections (part 2)
//     02/25/2009-2.0 Guy Pelletier
//       - 265359: JPA 2.0 Element Collections - Metadata processing portions
//     03/27/2009-2.0 Guy Pelletier
//       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
//     04/03/2009-2.0 Guy Pelletier
//       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
//     06/02/2009-2.0 Guy Pelletier
//       - 278768: JPA 2.0 Association Override Join Table
//     06/09/2009-2.0 Guy Pelletier
//       - 249037: JPA 2.0 persisting list item index
//     06/25/2009-2.0 Michael O'Brien
//       - 266912: change MappedSuperclass handling in stage2 to pre process accessors
//          in support of the custom descriptors holding mappings required by the Metamodel
//     09/29/2009-2.0 Guy Pelletier
//       - 282553: JPA 2.0 JoinTable support for OneToOne and ManyToOne
//     11/06/2009-2.0 Guy Pelletier
//       - 286317: UniqueConstraint xml element is changing (plus couple other fixes, see bug)
//     03/08/2010-2.1 Guy Pelletier
//       - 303632: Add attribute-type for mapping attributes to EclipseLink-ORM
//     04/27/2010-2.1 Guy Pelletier
//       - 309856: MappedSuperclasses from XML are not being initialized properly
//     05/31/2010-2.1 Guy Pelletier
//       - 314941: multiple joinColumns without referenced column names defined, no error
//     06/14/2010-2.2 Guy Pelletier
//       - 264417: Table generation is incorrect for JoinTables in AssociationOverrides
//     09/03/2010-2.2 Guy Pelletier
//       - 317286: DB column lenght not in sync between @Column and @JoinColumn
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     06/03/2011-2.3.1 Guy Pelletier
//       - 347563: transient field/property in embeddable entity
//     04/09/2012-2.4 Guy Pelletier
//       - 374377: OrderBy with ElementCollection doesn't work
//     10/09/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     11/19/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support) (foreign key metadata support)
//     11/28/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     07/16/2013-2.5.1 Guy Pelletier
//       - 412384: Applying Converter for parameterized basic-type for joda-time's DateTime does not work
//     07/07/2014-2.5.3  Rick Curtis
//       - 375101: Date and Calendar should not require @Temporal.
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ASSOCIATION_OVERRIDE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ASSOCIATION_OVERRIDES;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ATTRIBUTE_OVERRIDE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ATTRIBUTE_OVERRIDES;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_COLLECTION_TABLE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_COLUMN;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_MAP_KEY;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_MAP_KEY_CLASS;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_MAP_KEY_COLUMN;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_MAP_KEY_ENUMERATED;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_MAP_KEY_JOIN_COLUMN;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_MAP_KEY_JOIN_COLUMNS;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_MAP_KEY_TEMPORAL;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ORDER_BY;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ORDER_COLUMN;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.eclipse.persistence.internal.jpa.metadata.columns.ForeignKeyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.OrderColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.ConvertMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.EnumeratedMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.TemporalMetadata;
import org.eclipse.persistence.internal.jpa.metadata.mappings.MapKeyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.mappings.OrderByMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.CollectionTableMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.mappings.converters.AttributeNameTokenizer;
import org.eclipse.persistence.mappings.AggregateCollectionMapping;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.ContainerMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.EmbeddableMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeCollectionMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;

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
    private Boolean m_deleteAll;

    private ColumnMetadata m_column;
    private ColumnMetadata m_mapKeyColumn;

    private EnumeratedMetadata m_mapKeyEnumerated;
    private ForeignKeyMetadata m_mapKeyForeignKey;

    private List<ConvertMetadata> m_mapKeyConverts;
    private List<AssociationOverrideMetadata> m_associationOverrides = new ArrayList<AssociationOverrideMetadata>();
    private List<AssociationOverrideMetadata> m_mapKeyAssociationOverrides = new ArrayList<AssociationOverrideMetadata>();
    private List<AttributeOverrideMetadata> m_attributeOverrides = new ArrayList<AttributeOverrideMetadata>();
    private List<AttributeOverrideMetadata> m_mapKeyAttributeOverrides = new ArrayList<AttributeOverrideMetadata>();
    private List<JoinColumnMetadata> m_mapKeyJoinColumns = new ArrayList<JoinColumnMetadata>();

    private MapKeyMetadata m_mapKey;
    private MetadataClass m_targetClass;
    private MetadataClass m_mapKeyClass;
    private MetadataClass m_referenceClass;

    private OrderByMetadata m_orderBy;
    private OrderColumnMetadata m_orderColumn;

    private String m_mapKeyConvert;
    private String m_mapKeyClassName;
    private String m_targetClassName;
    private String m_compositeMember;

    private TemporalMetadata m_mapKeyTemporal;

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
        m_targetClass = getMetadataClass(elementCollection.getAttributeString("targetClass"));

        // Set the attribute overrides if some are present.
        // Set the attribute overrides first if defined.
        if (isAnnotationPresent(JPA_ATTRIBUTE_OVERRIDES)) {
            for (Object attributeOverride : getAnnotation(JPA_ATTRIBUTE_OVERRIDES).getAttributeArray("value")) {
                addAttributeOverride(new AttributeOverrideMetadata((MetadataAnnotation)attributeOverride, this));
            }
        }

        // Set the single attribute override second if defined.
        if (isAnnotationPresent(JPA_ATTRIBUTE_OVERRIDE)) {
            addAttributeOverride(new AttributeOverrideMetadata(getAnnotation(JPA_ATTRIBUTE_OVERRIDE), this));
        }

        // Set the association overrides if some are present.
        // Set the association overrides first if defined.
        if (isAnnotationPresent(JPA_ASSOCIATION_OVERRIDES)) {
            for (Object associationOverride : getAnnotation(JPA_ASSOCIATION_OVERRIDES).getAttributeArray("value")) {
                addAssociationOverride(new AssociationOverrideMetadata((MetadataAnnotation) associationOverride, this));
            }
        }

        // Set the single association override second if defined.
        if (isAnnotationPresent(JPA_ASSOCIATION_OVERRIDE)) {
            addAssociationOverride(new AssociationOverrideMetadata(getAnnotation(JPA_ASSOCIATION_OVERRIDE), this));
        }

        // Set the column if one if defined.
        if (isAnnotationPresent(JPA_COLUMN)) {
            m_column = new ColumnMetadata(getAnnotation(JPA_COLUMN), this);
        }

        // Set the collection table if one is defined.
        if (isAnnotationPresent(JPA_COLLECTION_TABLE)) {
            setCollectionTable(new CollectionTableMetadata(getAnnotation(JPA_COLLECTION_TABLE), this));
        }

        // Set the composite member if one is defined.
        if (isAnnotationPresent(CompositeMember.class)) {
            m_compositeMember = getAnnotation(CompositeMember.class).getAttributeString("value");
        }

        // Set the order by if one is present.
        if (isAnnotationPresent(JPA_ORDER_BY)) {
            m_orderBy = new OrderByMetadata(getAnnotation(JPA_ORDER_BY), this);
        }

        // Set the map key if one is defined.
        if (isAnnotationPresent(JPA_MAP_KEY)) {
            m_mapKey = new MapKeyMetadata(getAnnotation(JPA_MAP_KEY), this);
        }

        // Set the map key class if one is defined.
        if (isAnnotationPresent(JPA_MAP_KEY_CLASS)) {
            m_mapKeyClass = getMetadataClass(getAnnotation(JPA_MAP_KEY_CLASS).getAttributeString("value"));
        }

        // Set the map key enumerated if one is defined.
        if (isAnnotationPresent(JPA_MAP_KEY_ENUMERATED)) {
            m_mapKeyEnumerated = new EnumeratedMetadata(getAnnotation(JPA_MAP_KEY_ENUMERATED), this);
        }

        // Set the map key temporal if one is defined.
        if (isAnnotationPresent(JPA_MAP_KEY_TEMPORAL)) {
            m_mapKeyTemporal = new TemporalMetadata(getAnnotation(JPA_MAP_KEY_TEMPORAL), this);
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

        // Set the map key column if one is defined.
        if (isAnnotationPresent(JPA_MAP_KEY_COLUMN)) {
            m_mapKeyColumn = new ColumnMetadata(getAnnotation(JPA_MAP_KEY_COLUMN), this);
        }

        // Set the convert key if one is defined.
        if (isAnnotationPresent(MapKeyConvert.class)) {
            m_mapKeyConvert = getAnnotation(MapKeyConvert.class).getAttributeString("value");
        }

        // Set the order column if one is defined.
        if (isAnnotationPresent(JPA_ORDER_COLUMN)) {
            m_orderColumn = new OrderColumnMetadata(getAnnotation(JPA_ORDER_COLUMN), this);
        }

        // Set the delete all flag if one is defined.
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
        if (super.equals(objectToCompare) && objectToCompare instanceof ElementCollectionAccessor) {
            ElementCollectionAccessor elementCollectionAccessor = (ElementCollectionAccessor) objectToCompare;

            if (! valuesMatch(m_deleteAll, elementCollectionAccessor.getDeleteAll())) {
                return false;
            }

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

            if (! valuesMatch(m_mapKeyForeignKey, elementCollectionAccessor.getMapKeyForeignKey())) {
                return false;
            }

            if (! valuesMatch(m_mapKey, elementCollectionAccessor.getMapKey())) {
                return false;
            }

            if (! valuesMatch(m_orderBy, elementCollectionAccessor.getOrderBy())) {
                return false;
            }

            if (! valuesMatch(m_orderColumn, elementCollectionAccessor.getOrderColumn())) {
                return false;
            }

            if (! valuesMatch(m_mapKeyConverts, elementCollectionAccessor.getMapKeyConverts())) {
                return false;
            }

            if (! valuesMatch(m_mapKeyClassName, elementCollectionAccessor.getMapKeyClassName())) {
                return false;
            }

            if (! valuesMatch(m_targetClassName, elementCollectionAccessor.getTargetClassName())) {
                return false;
            }

            return valuesMatch(m_mapKeyTemporal, elementCollectionAccessor.getMapKeyTemporal());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (m_deleteAll != null ? m_deleteAll.hashCode() : 0);
        result = 31 * result + (m_column != null ? m_column.hashCode() : 0);
        result = 31 * result + (m_mapKeyColumn != null ? m_mapKeyColumn.hashCode() : 0);
        result = 31 * result + (m_mapKeyEnumerated != null ? m_mapKeyEnumerated.hashCode() : 0);
        result = 31 * result + (m_mapKeyForeignKey != null ? m_mapKeyForeignKey.hashCode() : 0);
        result = 31 * result + (m_mapKeyConverts != null ? m_mapKeyConverts.hashCode() : 0);
        result = 31 * result + (m_associationOverrides != null ? m_associationOverrides.hashCode() : 0);
        result = 31 * result + (m_mapKeyAssociationOverrides != null ? m_mapKeyAssociationOverrides.hashCode() : 0);
        result = 31 * result + (m_attributeOverrides != null ? m_attributeOverrides.hashCode() : 0);
        result = 31 * result + (m_mapKeyAttributeOverrides != null ? m_mapKeyAttributeOverrides.hashCode() : 0);
        result = 31 * result + (m_mapKeyJoinColumns != null ? m_mapKeyJoinColumns.hashCode() : 0);
        result = 31 * result + (m_mapKey != null ? m_mapKey.hashCode() : 0);
        result = 31 * result + (m_orderBy != null ? m_orderBy.hashCode() : 0);
        result = 31 * result + (m_orderColumn != null ? m_orderColumn.hashCode() : 0);
        result = 31 * result + (m_mapKeyClassName != null ? m_mapKeyClassName.hashCode() : 0);
        result = 31 * result + (m_targetClassName != null ? m_targetClassName.hashCode() : 0);
        result = 31 * result + (m_compositeMember != null ? m_compositeMember.hashCode() : 0);
        result = 31 * result + (m_mapKeyTemporal != null ? m_mapKeyTemporal.hashCode() : 0);
        return result;
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
    @Override
    protected DatabaseTable getDefaultTableForEntityMapKey(){
        return getCollectionTable().getDatabaseTable();
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
    @Override
    public MetadataClass getMapKeyClass() {
        return m_mapKeyClass;
    }

    /**
     * INTERNAL:
     * Future: this method is where we would provide a more explicit reference
     * class to support an auto-apply jpa converter. Per the spec auto-apply
     * converters are applied against basics only.
     */
    @Override
    public MetadataClass getMapKeyClassWithGenerics() {
        return getMapKeyClass();
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    @Override
    public List<AssociationOverrideMetadata> getMapKeyAssociationOverrides() {
        return m_mapKeyAssociationOverrides;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    @Override
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
    @Override
    public ColumnMetadata getMapKeyColumn() {
        return m_mapKeyColumn;
    }

    /**
     * INTERNAL:
     */
    @Override
    public String getMapKeyConvert() {
        return m_mapKeyConvert;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    @Override
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
    @Override
    public ForeignKeyMetadata getMapKeyForeignKey() {
        return m_mapKeyForeignKey;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    @Override
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
                    getLogger().logConfigMessage(MetadataLogger.ELEMENT_COLLECTION_MAPPING_REFERENCE_CLASS, getAnnotatedElement(), m_referenceClass);
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
        initXMLObjects(m_associationOverrides, accessibleObject);
        initXMLObjects(m_attributeOverrides, accessibleObject);
        initXMLObjects(m_mapKeyAssociationOverrides, accessibleObject);
        initXMLObjects(m_mapKeyAttributeOverrides, accessibleObject);
        initXMLObjects(m_mapKeyConverts, accessibleObject);
        initXMLObjects(m_mapKeyJoinColumns, accessibleObject);

        // Initialize single objects.
        initXMLObject(m_column, accessibleObject);
        initXMLObject(m_mapKey, accessibleObject);
        initXMLObject(m_mapKeyColumn, accessibleObject);
        initXMLObject(m_orderBy, accessibleObject);
        initXMLObject(m_orderColumn, accessibleObject);
        initXMLObject(m_mapKeyForeignKey, accessibleObject);
        initXMLObject(m_mapKeyEnumerated, accessibleObject);
        initXMLObject(m_mapKeyTemporal, accessibleObject);

        // Initialize the any class names we read from XML.
        m_targetClass = initXMLClassName(m_targetClassName);
        m_mapKeyClass = initXMLClassName(m_mapKeyClassName);

        // Initialize a previous text element from a list of elements (legacy)
        m_mapKeyConvert = initXMLTextObject(m_mapKeyConverts);
    }

    /**
     * INTERNAL:
     * Used by our XML writing facility.
     * Returns false unless m_deleteAll is both set and true
     */
    public boolean isDeleteAll(){
        return m_deleteAll != null && m_deleteAll;
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
     * Process the element collection metadata.
     */
    @Override
    public void process() {
        if (isDirectEmbeddableCollection()) {
            processDirectEmbeddableCollectionMapping(getReferenceDescriptor());
        } else if (isValidDirectCollectionType()) {
            processDirectCollectionMapping();

            if (m_compositeMember != null) {
                ((CollectionMapping) getMapping()).setSessionName(m_compositeMember);
            }
        } else if (isValidDirectMapType()) {
            processDirectMapMapping();

            if (m_compositeMember != null) {
                ((CollectionMapping) getMapping()).setSessionName(m_compositeMember);
            }
        } else {
            throw ValidationException.invalidTargetClass(getAttributeName(), getJavaClass());
        }

        // Process an order by if specified.
        if (m_orderBy != null) {
            m_orderBy.process((CollectionMapping) getMapping(), getReferenceDescriptor(), getJavaClass());
        }

        // Process the order column if specified.
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
        for (JoinColumnMetadata joinColumn : getJoinColumnsAndValidate(getCollectionTable().getJoinColumns(), getOwningDescriptor())) {
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

        if (m_deleteAll != null && mapping.isPrivateOwned()) {
            mapping.setMustDeleteReferenceObjectsOneByOne(! m_deleteAll);
        }
    }

    /**
     * INTERNAL:
     */
    protected void processDirectEmbeddableCollectionMapping(MetadataDescriptor referenceDescriptor) {
        // Initialize our mapping.
        DatabaseMapping mapping = getOwningDescriptor().getClassDescriptor().newAggregateCollectionMapping();

        // Process common direct collection metadata. This must be done
        // before any field processing since field processing requires that
        // the collection table be processed before hand.
        process(mapping);

        // Make sure to mark the descriptor as an embeddable collection descriptor.
        referenceDescriptor.setIsEmbeddable();

        processContainerPolicyAndIndirection((ContainerMapping)mapping);

        if (mapping instanceof AggregateCollectionMapping) {
            AggregateCollectionMapping collectionMapping = (AggregateCollectionMapping)mapping;

            // Process the fetch type and set the correct indirection on the mapping.

            // Process the mappings from the embeddable to setup the field name
            // translations. Before we do that lets process the attribute and
            // association overrides that are available to us and that may be used
            // to override any field name translations.
            processMappingsFromEmbeddable(referenceDescriptor, null, collectionMapping, getAttributeOverrides(m_attributeOverrides), getAssociationOverrides(m_associationOverrides), "");
            processMappingValueConverters(getDescriptor());
        } else if (mapping.isAbstractCompositeCollectionMapping()) {
            ((AbstractCompositeCollectionMapping)mapping).setField(getDatabaseField(getDescriptor().getPrimaryTable(), MetadataLogger.COLUMN));
        }
    }

    /**
     * INTERNAL:
     * Process convertors registered for collection mapping.
     * @param embeddableDescriptor Metadata descriptor for embedded collection.
     */
    protected void processMappingValueConverters(final MetadataDescriptor embeddableDescriptor) {
        for (ConvertMetadata convert : getConverts()) {
            final String attributeName
                    = AttributeNameTokenizer.getNameAfterVersion(convert.getAttributeName());
            final MappingAccessor mappingAccessor = attributeName != null
                    ? embeddableDescriptor.getMappingAccessor(attributeName) : null;
            if (mappingAccessor != null) {
                convert.process(this.getMapping(),
                        getReferenceClass(), getClassAccessor(), attributeName);
            }
        }
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
                    AbstractDirectMapping directMapping = (AbstractDirectMapping) mapping;

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
    @Override
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

    @Override
    public void setTemporal(TemporalMetadata metadata, boolean isForMapKey) {
        if (isForMapKey) {
            setMapKeyTemporal(metadata);
        }
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

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setTargetClassName(String targetClassName) {
        m_targetClassName = targetClassName;
    }
}
