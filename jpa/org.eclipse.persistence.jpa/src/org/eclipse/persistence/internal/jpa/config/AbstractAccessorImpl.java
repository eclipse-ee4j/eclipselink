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
package org.eclipse.persistence.internal.jpa.config;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.converters.ConverterImpl;
import org.eclipse.persistence.internal.jpa.config.converters.ObjectTypeConverterImpl;
import org.eclipse.persistence.internal.jpa.config.converters.StructConverterImpl;
import org.eclipse.persistence.internal.jpa.config.converters.TypeConverterImpl;
import org.eclipse.persistence.internal.jpa.config.mappings.AccessMethodsImpl;
import org.eclipse.persistence.internal.jpa.config.partitioning.HashPartitioningImpl;
import org.eclipse.persistence.internal.jpa.config.partitioning.PartitioningImpl;
import org.eclipse.persistence.internal.jpa.config.partitioning.PinnedPartitioningImpl;
import org.eclipse.persistence.internal.jpa.config.partitioning.RangePartitioningImpl;
import org.eclipse.persistence.internal.jpa.config.partitioning.ReplicationPartitioningImpl;
import org.eclipse.persistence.internal.jpa.config.partitioning.RoundRobinPartitioningImpl;
import org.eclipse.persistence.internal.jpa.config.partitioning.UnionPartitioningImpl;
import org.eclipse.persistence.internal.jpa.config.partitioning.ValuePartitioningImpl;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.PropertyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.ConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.ObjectTypeConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.StructConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.TypeConverterMetadata;
import org.eclipse.persistence.jpa.config.AccessMethods;
import org.eclipse.persistence.jpa.config.Converter;
import org.eclipse.persistence.jpa.config.HashPartitioning;
import org.eclipse.persistence.jpa.config.ObjectTypeConverter;
import org.eclipse.persistence.jpa.config.Partitioning;
import org.eclipse.persistence.jpa.config.PinnedPartitioning;
import org.eclipse.persistence.jpa.config.Property;
import org.eclipse.persistence.jpa.config.RangePartitioning;
import org.eclipse.persistence.jpa.config.ReplicationPartitioning;
import org.eclipse.persistence.jpa.config.RoundRobinPartitioning;
import org.eclipse.persistence.jpa.config.StructConverter;
import org.eclipse.persistence.jpa.config.TypeConverter;
import org.eclipse.persistence.jpa.config.UnionPartitioning;
import org.eclipse.persistence.jpa.config.ValuePartitioning;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
@SuppressWarnings("unchecked")
public class AbstractAccessorImpl<T extends MetadataAccessor, R> extends MetadataImpl<T> {

    public AbstractAccessorImpl(T t) {
        super(t);

        getMetadata().setConverters(new ArrayList<ConverterMetadata>());
        getMetadata().setObjectTypeConverters(new ArrayList<ObjectTypeConverterMetadata>());
        getMetadata().setProperties(new ArrayList<PropertyMetadata>());
        getMetadata().setStructConverters(new ArrayList<StructConverterMetadata>());
        getMetadata().setTypeConverters(new ArrayList<TypeConverterMetadata>());
    }

    public Converter addConverter() {
        ConverterImpl converter = new ConverterImpl();
        getMetadata().getConverters().add(converter.getMetadata());
        return converter;
    }

    public Property addProperty() {
        PropertyImpl property = new PropertyImpl();
        getMetadata().getProperties().add(property.getMetadata());
        return property;
    }

    public ObjectTypeConverter addObjectTypeConverter() {
        ObjectTypeConverterImpl converter = new ObjectTypeConverterImpl();
        getMetadata().getObjectTypeConverters().add(converter.getMetadata());
        return converter;
    }

    public StructConverter addStructConverter() {
        StructConverterImpl converter = new StructConverterImpl();
        getMetadata().getStructConverters().add(converter.getMetadata());
        return converter;
    }

    public TypeConverter addTypeConverter() {
        TypeConverterImpl converter = new TypeConverterImpl();
        getMetadata().getTypeConverters().add(converter.getMetadata());
        return converter;
    }

    public R setAccess(String access) {
        getMetadata().setAccess(access);
        return (R) this;
    }

    public AccessMethods setAccessMethods() {
        AccessMethodsImpl accessMethods = new AccessMethodsImpl();
        getMetadata().setAccessMethods(accessMethods.getMetadata());
        return accessMethods;
    }

    public Converter setConverter() {
        return addConverter();
    }

    public HashPartitioning setHashPartitioning() {
        HashPartitioningImpl hashPartitioning = new HashPartitioningImpl();
        getMetadata().setHashPartitioning(hashPartitioning.getMetadata());
        return hashPartitioning;
    }

    public R setName(String name) {
        getMetadata().setName(name);
        return (R) this;
    }

    public ObjectTypeConverter setObjectTypeConverter() {
        return addObjectTypeConverter();
    }

    public R setPartitioned(String partitioned) {
        getMetadata().setPartitioned(partitioned);
        return (R) this;
    }

    public Partitioning setPartitioning() {
        PartitioningImpl partitioning = new PartitioningImpl();
        getMetadata().setPartitioning(partitioning.getMetadata());
        return partitioning;
    }

    public PinnedPartitioning setPinnedPartitioning() {
        PinnedPartitioningImpl pinnedPartitioning = new PinnedPartitioningImpl();
        getMetadata().setPinnedPartitioning(pinnedPartitioning.getMetadata());
        return pinnedPartitioning;
    }

    public RangePartitioning setRangePartitioning() {
        RangePartitioningImpl rangePartitioning = new RangePartitioningImpl();
        getMetadata().setRangePartitioning(rangePartitioning.getMetadata());
        return rangePartitioning;
    }

    public ReplicationPartitioning setReplicationPartitioning() {
        ReplicationPartitioningImpl replicationPartitioning = new ReplicationPartitioningImpl();
        getMetadata().setReplicationPartitioning(replicationPartitioning.getMetadata());
        return replicationPartitioning;
    }

    public RoundRobinPartitioning setRoundRobinPartitioning() {
        RoundRobinPartitioningImpl roundRobinPartitioning = new RoundRobinPartitioningImpl();
        getMetadata().setRoundRobinPartitioning(roundRobinPartitioning.getMetadata());
        return roundRobinPartitioning;
    }

    public StructConverter setStructConverter() {
        return addStructConverter();
    }

    public TypeConverter setTypeConverter() {
        return addTypeConverter();
    }

    public UnionPartitioning setUnionPartitioning() {
        UnionPartitioningImpl unionPartitioning = new UnionPartitioningImpl();
        getMetadata().setUnionPartitioning(unionPartitioning.getMetadata());
        return unionPartitioning;
    }

    public ValuePartitioning setValuePartitioning() {
        ValuePartitioningImpl valuePartitioning = new ValuePartitioningImpl();
        getMetadata().setValuePartitioning(valuePartitioning.getMetadata());
        return valuePartitioning;
    }
}
