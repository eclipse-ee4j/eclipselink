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
import org.eclipse.persistence.internal.jpa.metadata.partitioning.ValuePartitionMetadata;
import org.eclipse.persistence.jpa.config.ValuePartition;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class ValuePartitionImpl extends MetadataImpl<ValuePartitionMetadata> implements ValuePartition {

    public ValuePartitionImpl() {
        super(new ValuePartitionMetadata());
    }

    @Override
    public ValuePartition setConnectionPool(String connectionPool) {
        getMetadata().setConnectionPool(connectionPool);
        return this;
    }

    @Override
    public ValuePartition setValue(String value) {
        getMetadata().setValue(value);
        return this;
    }

}
