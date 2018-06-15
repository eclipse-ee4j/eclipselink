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

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.RangePartitionMetadata;
import org.eclipse.persistence.jpa.config.RangePartition;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class RangePartitionImpl extends MetadataImpl<RangePartitionMetadata> implements RangePartition {

    public RangePartitionImpl() {
        super(new RangePartitionMetadata());
    }

    @Override
    public RangePartition setConnectionPool(String connectionPool) {
        getMetadata().setConnectionPool(connectionPool);
        return this;
    }

    @Override
    public RangePartition setEndValue(String endValue) {
        getMetadata().setEndValue(endValue);
        return this;
    }

    @Override
    public RangePartition setStartValue(String startValue) {
        getMetadata().setStartValue(startValue);
        return this;
    }

}
