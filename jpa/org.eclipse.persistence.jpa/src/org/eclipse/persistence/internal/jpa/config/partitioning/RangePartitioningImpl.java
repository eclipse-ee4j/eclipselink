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
package org.eclipse.persistence.internal.jpa.config.partitioning;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.config.columns.ColumnImpl;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.RangePartitionMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.RangePartitioningMetadata;
import org.eclipse.persistence.jpa.config.Column;
import org.eclipse.persistence.jpa.config.RangePartition;
import org.eclipse.persistence.jpa.config.RangePartitioning;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class RangePartitioningImpl extends MetadataImpl<RangePartitioningMetadata> implements RangePartitioning {

    public RangePartitioningImpl() {
        super(new RangePartitioningMetadata());

        getMetadata().setPartitions(new ArrayList<RangePartitionMetadata>());
    }

    @Override
    public RangePartition addPartition() {
        RangePartitionImpl rangePartition = new RangePartitionImpl();
        getMetadata().getPartitions().add(rangePartition.getMetadata());
        return rangePartition;
    }

    @Override
    public RangePartitioning setName(String name) {
        getMetadata().setName(name);
        return this;
    }

    @Override
    public Column setPartitionColumn() {
        ColumnImpl column = new ColumnImpl();
        getMetadata().setPartitionColumn(column.getMetadata());
        return column;
    }

    @Override
    public RangePartitioning setPartitionValueType(String partitionValueType) {
        getMetadata().setPartitionValueTypeName(partitionValueType);
        return this;
    }

    @Override
    public RangePartitioning setUnionUnpartitionableQueries(Boolean unionUnpartitionableQueries) {
        getMetadata().setUnionUnpartitionableQueries(unionUnpartitionableQueries);
        return this;
    }

}
