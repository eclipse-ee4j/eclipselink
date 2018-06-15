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
//     11/19/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
package org.eclipse.persistence.internal.jpa.metadata.partitioning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.descriptors.partitioning.ReplicationPartitioningPolicy;

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
public class ReplicationPartitioningMetadata extends AbstractPartitioningMetadata {
    protected List<String> connectionPools;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public ReplicationPartitioningMetadata() {
        super("<replication-partitioning>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public ReplicationPartitioningMetadata(MetadataAnnotation annotation, MetadataAccessor accessor) {
        super(annotation, accessor);
        this.connectionPools = new ArrayList<String>();
        this.connectionPools.addAll((List)Arrays.asList(annotation.getAttributeArray("connectionPools")));
    }

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    protected ReplicationPartitioningMetadata(String elementName) {
        super(elementName);
    }

    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && (objectToCompare instanceof ReplicationPartitioningMetadata)) {
            ReplicationPartitioningMetadata policy = (ReplicationPartitioningMetadata) objectToCompare;

            return valuesMatch(this.connectionPools, policy.getConnectionPools());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (connectionPools != null ? connectionPools.hashCode() : 0);
        return result;
    }

    public List<String> getConnectionPools() {
        return connectionPools;
    }

    @Override
    public PartitioningPolicy buildPolicy() {
        ReplicationPartitioningPolicy policy = new ReplicationPartitioningPolicy();
        super.buildPolicy(policy);
        return policy;
    }

    @Override
    public void buildPolicy(PartitioningPolicy policy) {
        super.buildPolicy(policy);
        ((ReplicationPartitioningPolicy)policy).setConnectionPools(getConnectionPools());
    }

    public void setConnectionPools(List<String> connectionPools) {
        this.connectionPools = connectionPools;
    }
}
