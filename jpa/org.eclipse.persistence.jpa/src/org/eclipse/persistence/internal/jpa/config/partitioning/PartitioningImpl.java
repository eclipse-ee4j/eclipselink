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
import org.eclipse.persistence.internal.jpa.metadata.partitioning.PartitioningMetadata;
import org.eclipse.persistence.jpa.config.Partitioning;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class PartitioningImpl extends MetadataImpl<PartitioningMetadata> implements Partitioning {

    public PartitioningImpl() {
        super(new PartitioningMetadata());
    }

    @Override
    public Partitioning setClass(String className) {
        getMetadata().setClassName(className);
        return this;
    }

    @Override
    public Partitioning setName(String name) {
        getMetadata().setName(name);
        return this;
    }

}
