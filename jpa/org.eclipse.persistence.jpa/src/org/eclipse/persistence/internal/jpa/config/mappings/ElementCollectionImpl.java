/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Guy Pelletier - initial API and implementation
package org.eclipse.persistence.internal.jpa.config.mappings;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.columns.AssociationOverrideImpl;
import org.eclipse.persistence.internal.jpa.config.columns.AttributeOverrideImpl;
import org.eclipse.persistence.internal.jpa.config.columns.ColumnImpl;
import org.eclipse.persistence.internal.jpa.config.columns.FieldImpl;
import org.eclipse.persistence.internal.jpa.config.columns.ForeignKeyImpl;
import org.eclipse.persistence.internal.jpa.config.columns.JoinColumnImpl;
import org.eclipse.persistence.internal.jpa.config.columns.OrderColumnImpl;
import org.eclipse.persistence.internal.jpa.config.converters.ConvertImpl;
import org.eclipse.persistence.internal.jpa.config.converters.EnumeratedImpl;
import org.eclipse.persistence.internal.jpa.config.converters.TemporalImpl;
import org.eclipse.persistence.internal.jpa.config.tables.CollectionTableImpl;
import org.eclipse.persistence.internal.jpa.metadata.columns.AssociationOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.AttributeOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.ConvertMetadata;
import org.eclipse.persistence.internal.jpa.metadata.mappings.OrderByMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ElementCollectionAccessor;
import org.eclipse.persistence.jpa.config.AssociationOverride;
import org.eclipse.persistence.jpa.config.AttributeOverride;
import org.eclipse.persistence.jpa.config.BatchFetch;
import org.eclipse.persistence.jpa.config.CollectionTable;
import org.eclipse.persistence.jpa.config.Column;
import org.eclipse.persistence.jpa.config.Convert;
import org.eclipse.persistence.jpa.config.ElementCollection;
import org.eclipse.persistence.jpa.config.Enumerated;
import org.eclipse.persistence.jpa.config.Field;
import org.eclipse.persistence.jpa.config.ForeignKey;
import org.eclipse.persistence.jpa.config.JoinColumn;
import org.eclipse.persistence.jpa.config.MapKey;
import org.eclipse.persistence.jpa.config.OrderColumn;
import org.eclipse.persistence.jpa.config.Temporal;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class ElementCollectionImpl extends AbstractDirectMappingImpl<ElementCollectionAccessor, ElementCollection> implements ElementCollection {

    public ElementCollectionImpl() {
        super(new ElementCollectionAccessor());

        getMetadata().setAssociationOverrides(new ArrayList<AssociationOverrideMetadata>());
        getMetadata().setAttributeOverrides(new ArrayList<AttributeOverrideMetadata>());
        getMetadata().setMapKeyAssociationOverrides(new ArrayList<AssociationOverrideMetadata>());
        getMetadata().setMapKeyAttributeOverrides(new ArrayList<AttributeOverrideMetadata>());
        getMetadata().setMapKeyConverts(new ArrayList<ConvertMetadata>());
        getMetadata().setMapKeyJoinColumns(new ArrayList<JoinColumnMetadata>());
    }

    @Override
    public AssociationOverride addAssociationOverride() {
        AssociationOverrideImpl override = new AssociationOverrideImpl();
        getMetadata().getAssociationOverrides().add(override.getMetadata());
        return override;
    }

    @Override
    public AttributeOverride addAttributeOverride() {
        AttributeOverrideImpl override = new AttributeOverrideImpl();
        getMetadata().getAttributeOverrides().add(override.getMetadata());
        return override;
    }

    @Override
    public AssociationOverride addMapKeyAssociationOverride() {
        AssociationOverrideImpl override = new AssociationOverrideImpl();
        getMetadata().getMapKeyAssociationOverrides().add(override.getMetadata());
        return override;
    }

    @Override
    public AttributeOverride addMapKeyAttributeOverride() {
        AttributeOverrideImpl override = new AttributeOverrideImpl();
        getMetadata().getMapKeyAttributeOverrides().add(override.getMetadata());
        return override;
    }

    /**
     * This covers the JPA 2.1 use case where multiple converts can be added.
     */
    @Override
    public Convert addMapKeyConvert() {
        ConvertImpl convert = new ConvertImpl();
        getMetadata().getMapKeyConverts().add(convert.getMetadata());
        return convert;
    }

    @Override
    public JoinColumn addMapKeyJoinColumn() {
        JoinColumnImpl joinColumn = new JoinColumnImpl();
        getMetadata().getMapKeyJoinColumns().add(joinColumn.getMetadata());
        return joinColumn;
    }

    @Override
    public BatchFetch setBatchFetch() {
        BatchFetchImpl batchFetch = new BatchFetchImpl();
        getMetadata().setBatchFetch(batchFetch.getMetadata());
        return batchFetch;
    }

    @Override
    public ElementCollection setCascadeOnDelete(Boolean cascadeOnDelete) {
        getMetadata().setCascadeOnDelete(cascadeOnDelete);
        return this;
    }

    @Override
    public CollectionTable setCollectionTable() {
        CollectionTableImpl collectionTable = new CollectionTableImpl();
        getMetadata().setCollectionTable(collectionTable.getMetadata());
        return collectionTable;
    }

    @Override
    public Column setColumn() {
        ColumnImpl column = new ColumnImpl();
        getMetadata().setColumn(column.getMetadata());
        return column;
    }

    @Override
    public ElementCollection setCompositeMember(String compositeMember) {
        getMetadata().setCompositeMember(compositeMember);
        return this;
    }

    @Override
    public ElementCollection setDeleteAll(Boolean deleteAll) {
        getMetadata().setDeleteAll(deleteAll);
        return this;
    }

    @Override
    public Field setField() {
        FieldImpl field = new FieldImpl();
        getMetadata().setField(field.getMetadata());
        return field;
    }

    @Override
    public ElementCollection setJoinFetch(String joinFetch) {
        getMetadata().setJoinFetch(joinFetch);
        return this;
    }

    @Override
    public MapKey setMapKey() {
        MapKeyImpl mapKey = new MapKeyImpl();
        getMetadata().setMapKey(mapKey.getMetadata());
        return mapKey;
    }

    @Override
    public ElementCollection setMapKeyClass(String mapKeyClass) {
        getMetadata().setMapKeyClassName(mapKeyClass);
        return this;
    }

    @Override
    public Column setMapKeyColumn() {
        ColumnImpl column = new ColumnImpl();
        getMetadata().setMapKeyColumn(column.getMetadata());
        return column;
    }

    /**
     * This covers the EclipseLink Convert, single TEXT convert element.
     */
    @Override
    public ElementCollection setMapKeyConvert(String mapKeyConvert) {
        ConvertMetadata convert = new ConvertMetadata();
        convert.setText(mapKeyConvert);
        getMetadata().getMapKeyConverts().add(convert);
        return this;
    }

    @Override
    public Enumerated setMapKeyEnumerated() {
        EnumeratedImpl enumerated = new EnumeratedImpl();
        getMetadata().setEnumerated(enumerated.getMetadata());
        return enumerated;
    }

    @Override
    public ForeignKey setMapKeyForeignKey() {
        ForeignKeyImpl foreignKey = new ForeignKeyImpl();
        getMetadata().setMapKeyForeignKey(foreignKey.getMetadata());
        return foreignKey;
    }

    @Override
    public Temporal setMapKeyTemporal() {
        TemporalImpl temporal = new TemporalImpl();
        getMetadata().setTemporal(temporal.getMetadata());
        return temporal;
    }

    @Override
    public ElementCollection setNonCacheable(Boolean nonCacheable) {
        getMetadata().setNonCacheable(nonCacheable);
        return this;
    }

    @Override
    public ElementCollection setOrderBy(String orderBy) {
        OrderByMetadata metadata = new OrderByMetadata();
        metadata.setValue(orderBy);
        getMetadata().setOrderBy(metadata);
        return this;
    }

    @Override
    public OrderColumn setOrderColumn() {
        OrderColumnImpl column = new OrderColumnImpl();
        getMetadata().setOrderColumn(column.getMetadata());
        return column;
    }

    @Override
    public ElementCollection setTargetClass(String targetClass) {
        getMetadata().setTargetClassName(targetClass);
        return this;
    }

}
