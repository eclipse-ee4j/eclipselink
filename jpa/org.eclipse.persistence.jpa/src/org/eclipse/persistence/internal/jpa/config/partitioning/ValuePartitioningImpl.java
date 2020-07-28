/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Guy Pelletier - initial API and implementation
package org.eclipse.persistence.internal.jpa.config.partitioning;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.config.columns.ColumnImpl;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.ValuePartitionMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.ValuePartitioningMetadata;
import org.eclipse.persistence.jpa.config.Column;
import org.eclipse.persistence.jpa.config.ValuePartition;
import org.eclipse.persistence.jpa.config.ValuePartitioning;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class ValuePartitioningImpl extends MetadataImpl<ValuePartitioningMetadata> implements ValuePartitioning {

    public ValuePartitioningImpl() {
        super(new ValuePartitioningMetadata());

        getMetadata().setPartitions(new ArrayList<ValuePartitionMetadata>());
    }

    public ValuePartition addPartition() {
        ValuePartitionImpl valuePartition = new ValuePartitionImpl();
        getMetadata().getPartitions().add(valuePartition.getMetadata());
        return valuePartition;
    }

    public ValuePartitioning setDefaultConnectionPool(String defaultConnectionPool) {
        getMetadata().setDefaultConnectionPool(defaultConnectionPool);
        return this;
    }

    public ValuePartitioning setName(String name) {
        getMetadata().setName(name);
        return this;
    }

    public Column setPartitionColumn() {
        ColumnImpl column = new ColumnImpl();
        getMetadata().setPartitionColumn(column.getMetadata());
        return column;
    }

    public ValuePartitioning setPartitionValueType(String partitionValueType) {
        getMetadata().setPartitionValueTypeName(partitionValueType);
        return this;
    }

    public ValuePartitioning setUnionUnpartitionableQueries(Boolean unionUnpartitionableQueries) {
        getMetadata().setUnionUnpartitionableQueries(unionUnpartitionableQueries);
        return this;
    }

}
