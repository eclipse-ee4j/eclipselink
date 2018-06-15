/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     James Sutherland - initial API and implementation
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
package org.eclipse.persistence.internal.jpa.metadata.partitioning;

import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.descriptors.partitioning.UnionPartitioningPolicy;

/**
 * INTERNAL:
 * Define JPA meta-data for partitioning policy.
 *
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 *
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
public class UnionPartitioningMetadata extends ReplicationPartitioningMetadata {
    protected Boolean replicateWrites;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public UnionPartitioningMetadata() {
        super("<union-partitioning>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public UnionPartitioningMetadata(MetadataAnnotation annotation, MetadataAccessor accessor) {
        super(annotation, accessor);

        this.replicateWrites = annotation.getAttributeBooleanDefaultFalse("replicateWrites");
    }

    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && (objectToCompare instanceof UnionPartitioningMetadata)) {
            UnionPartitioningMetadata policy = (UnionPartitioningMetadata) objectToCompare;

            return valuesMatch(this.replicateWrites, policy.getReplicateWrites());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (replicateWrites != null ? replicateWrites.hashCode() : 0);
        return result;
    }

    @Override
    public PartitioningPolicy buildPolicy() {
        UnionPartitioningPolicy policy = new UnionPartitioningPolicy();
        super.buildPolicy(policy);
        if (this.replicateWrites != null) {
            policy.setReplicateWrites(this.replicateWrites);
        }
        return policy;
    }

    public Boolean getReplicateWrites() {
        return replicateWrites;
    }

    public void setReplicateWrites(Boolean replicateWrites) {
        this.replicateWrites = replicateWrites;
    }
}
