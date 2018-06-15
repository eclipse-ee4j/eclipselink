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
import org.eclipse.persistence.internal.jpa.config.columns.ForeignKeyImpl;
import org.eclipse.persistence.internal.jpa.config.columns.JoinColumnImpl;
import org.eclipse.persistence.internal.jpa.config.columns.OrderColumnImpl;
import org.eclipse.persistence.internal.jpa.config.converters.ConvertImpl;
import org.eclipse.persistence.internal.jpa.config.converters.EnumeratedImpl;
import org.eclipse.persistence.internal.jpa.config.converters.TemporalImpl;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.CollectionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.columns.AssociationOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.AttributeOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.ConvertMetadata;
import org.eclipse.persistence.internal.jpa.metadata.mappings.OrderByMetadata;
import org.eclipse.persistence.jpa.config.AssociationOverride;
import org.eclipse.persistence.jpa.config.AttributeOverride;
import org.eclipse.persistence.jpa.config.Column;
import org.eclipse.persistence.jpa.config.Convert;
import org.eclipse.persistence.jpa.config.Enumerated;
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
@SuppressWarnings("unchecked")
public class AbstractCollectionMappingImpl<T extends CollectionAccessor, R> extends AbstractRelationshipMappingImpl<T, R> {

    public AbstractCollectionMappingImpl(T t) {
        super(t);

        getMetadata().setMapKeyAssociationOverrides(new ArrayList<AssociationOverrideMetadata>());
        getMetadata().setMapKeyAttributeOverrides(new ArrayList<AttributeOverrideMetadata>());
        getMetadata().setMapKeyConverts(new ArrayList<ConvertMetadata>());
        getMetadata().setMapKeyJoinColumns(new ArrayList<JoinColumnMetadata>());
    }

    public AssociationOverride addMapKeyAssociationOverride() {
        AssociationOverrideImpl associationOverride = new AssociationOverrideImpl();
        getMetadata().getMapKeyAssociationOverrides().add(associationOverride.getMetadata());
        return associationOverride;
    }

    public AttributeOverride addMapKeyAttributeOverride() {
        AttributeOverrideImpl attributeOverride = new AttributeOverrideImpl();
        getMetadata().getMapKeyAttributeOverrides().add(attributeOverride.getMetadata());
        return attributeOverride;
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

    public R setDeleteAll(Boolean deleteAll) {
        getMetadata().setDeleteAll(deleteAll);
        return (R) this;
    }

    public MapKey setMapKey() {
        MapKeyImpl mapKey = new MapKeyImpl();
        getMetadata().setMapKey(mapKey.getMetadata());
        return mapKey;
    }

    public R setMapKeyClass(String mapKeyClass) {
        getMetadata().setMapKeyClassName(mapKeyClass);
        return (R) this;
    }

    public Column setMapKeyColumn() {
        ColumnImpl column = new ColumnImpl();
        getMetadata().setMapKeyColumn(column.getMetadata());
        return column;
    }

    /**
     * This covers the EclipseLink Convert, single TEXT convert element.
     */
    public R setMapKeyConvert(String mapKeyConvert) {
        ConvertMetadata convert = new ConvertMetadata();
        convert.setText(mapKeyConvert);
        getMetadata().getMapKeyConverts().add(convert);
        return (R) this;
    }

    public Enumerated setMapKeyEnumerated() {
        EnumeratedImpl enumerated = new EnumeratedImpl();
        getMetadata().setMapKeyEnumerated(enumerated.getMetadata());
        return enumerated;
    }

    public ForeignKey setMapKeyForeignKey() {
        ForeignKeyImpl foreignKey = new ForeignKeyImpl();
        getMetadata().setMapKeyForeignKey(foreignKey.getMetadata());
        return foreignKey;
    }

    public Temporal setMapKeyTemporal() {
        TemporalImpl temporal = new TemporalImpl();
        getMetadata().setMapKeyTemporal(temporal.getMetadata());
        return temporal;
    }

    public R setOrderBy(String orderBy) {
        OrderByMetadata metadata = new OrderByMetadata();
        metadata.setValue(orderBy);
        getMetadata().setOrderBy(metadata);
        return (R) this;
    }

    public OrderColumn setOrderColumn() {
        OrderColumnImpl column = new OrderColumnImpl();
        getMetadata().setOrderColumn(column.getMetadata());
        return column;
    }

}
