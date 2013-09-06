/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Guy Pelletier - initial API and implementation
 ******************************************************************************/
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

    public AssociationOverride addAssociationOverride() {
        AssociationOverrideImpl override = new AssociationOverrideImpl();
        getMetadata().getAssociationOverrides().add(override.getMetadata());
        return override;
    }

    public AttributeOverride addAttributeOverride() {
        AttributeOverrideImpl override = new AttributeOverrideImpl();
        getMetadata().getAttributeOverrides().add(override.getMetadata());
        return override;
    }

    public AssociationOverride addMapKeyAssociationOverride() {
        AssociationOverrideImpl override = new AssociationOverrideImpl();
        getMetadata().getMapKeyAssociationOverrides().add(override.getMetadata());
        return override;
    }

    public AttributeOverride addMapKeyAttributeOverride() {
        AttributeOverrideImpl override = new AttributeOverrideImpl();
        getMetadata().getMapKeyAttributeOverrides().add(override.getMetadata());
        return override;
    }
    
    /**
     * This covers the JPA 2.1 use case where multiple converts can be added.
     */
    public Convert addMapKeyConvert() {
        ConvertImpl convert = new ConvertImpl();
        getMetadata().getMapKeyConverts().add(convert.getMetadata());
        return convert;
    }

    public JoinColumn addMapKeyJoinColumn() {
        JoinColumnImpl joinColumn = new JoinColumnImpl();
        getMetadata().getMapKeyJoinColumns().add(joinColumn.getMetadata());
        return joinColumn;
    }

    public BatchFetch setBatchFetch() {
        BatchFetchImpl batchFetch = new BatchFetchImpl();
        getMetadata().setBatchFetch(batchFetch.getMetadata());
        return batchFetch;
    }

    public ElementCollection setCascadeOnDelete(Boolean cascadeOnDelete) {
        getMetadata().setCascadeOnDelete(cascadeOnDelete);
        return this;
    }

    public CollectionTable setCollectionTable() {
        CollectionTableImpl collectionTable = new CollectionTableImpl();
        getMetadata().setCollectionTable(collectionTable.getMetadata());
        return collectionTable;
    }

    public Column setColumn() {
        ColumnImpl column = new ColumnImpl();
        getMetadata().setColumn(column.getMetadata());
        return column;
    }

    public ElementCollection setCompositeMember(String compositeMember) {
        getMetadata().setCompositeMember(compositeMember);
        return this;
    }

    public ElementCollection setDeleteAll(Boolean deleteAll) {
        getMetadata().setDeleteAll(deleteAll);
        return this;
    }

    public Field setField() {
        FieldImpl field = new FieldImpl();
        getMetadata().setField(field.getMetadata());
        return field;
    }

    public ElementCollection setJoinFetch(String joinFetch) {
        getMetadata().setJoinFetch(joinFetch);
        return this;
    }

    public MapKey setMapKey() {
        MapKeyImpl mapKey = new MapKeyImpl();
        getMetadata().setMapKey(mapKey.getMetadata());
        return mapKey;
    }

    public ElementCollection setMapKeyClass(String mapKeyClass) {
        getMetadata().setMapKeyClassName(mapKeyClass);
        return this;
    }

    public Column setMapKeyColumn() {
        ColumnImpl column = new ColumnImpl();
        getMetadata().setMapKeyColumn(column.getMetadata());
        return column;
    }

    /**
     * This covers the EclipseLink Convert, single TEXT convert element.
     */
    public ElementCollection setMapKeyConvert(String mapKeyConvert) {
        ConvertMetadata convert = new ConvertMetadata();
        convert.setText(mapKeyConvert);
        getMetadata().getMapKeyConverts().add(convert);
        return this;
    }

    public Enumerated setMapKeyEnumerated() {
        EnumeratedImpl enumerated = new EnumeratedImpl();
        getMetadata().setEnumerated(enumerated.getMetadata());
        return enumerated;
    }
    
    public ForeignKey setMapKeyForeignKey() {
        ForeignKeyImpl foreignKey = new ForeignKeyImpl();
        getMetadata().setMapKeyForeignKey(foreignKey.getMetadata());
        return foreignKey;
    }

    public Temporal setMapKeyTemporal() {
        TemporalImpl temporal = new TemporalImpl();
        getMetadata().setTemporal(temporal.getMetadata());
        return temporal;
    }

    public ElementCollection setNonCacheable(Boolean nonCacheable) {
        getMetadata().setNonCacheable(nonCacheable);
        return this;
    }

    public ElementCollection setOrderBy(String orderBy) {
        OrderByMetadata metadata = new OrderByMetadata();
        metadata.setValue(orderBy);
        getMetadata().setOrderBy(metadata);
        return this;
    }

    public OrderColumn setOrderColumn() {
        OrderColumnImpl column = new OrderColumnImpl();
        getMetadata().setOrderColumn(column.getMetadata());
        return column;
    }

    public ElementCollection setTargetClass(String targetClass) {
        getMetadata().setTargetClassName(targetClass);
        return this;
    }

}
