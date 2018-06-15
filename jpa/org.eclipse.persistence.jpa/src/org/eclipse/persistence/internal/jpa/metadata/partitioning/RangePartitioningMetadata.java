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
//     30/05/2012-2.4 Guy Pelletier
//       - 354678: Temp classloader is still being used during metadata processing
//     11/19/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
package org.eclipse.persistence.internal.jpa.metadata.partitioning;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.descriptors.partitioning.RangePartition;
import org.eclipse.persistence.descriptors.partitioning.RangePartitioningPolicy;

/**
 * INTERNAL:
 * Define JPA meta-data for partitioning policy.
 *
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - any metadata mapped from XML to this class must be initialized in the
 *   initXMLObject method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 *
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
public class RangePartitioningMetadata extends FieldPartitioningMetadata {
    protected List<RangePartitionMetadata> partitions;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public RangePartitioningMetadata() {
        super("<range-partitioning>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public RangePartitioningMetadata(MetadataAnnotation annotation, MetadataAccessor accessor) {
        super(annotation, accessor);

        this.partitions = new ArrayList<RangePartitionMetadata>();
        for (Object partitionAnnotation : annotation.getAttributeArray("partitions")) {
            this.partitions.add(new RangePartitionMetadata((MetadataAnnotation) partitionAnnotation, accessor));
        }
    }

    /**
     * INTERNAL:
     * Method for processing/building the specific property.
     */
    @Override
    public PartitioningPolicy buildPolicy() {
        RangePartitioningPolicy policy = new RangePartitioningPolicy();
        super.buildPolicy(policy);

        for (RangePartitionMetadata partition : getPartitions()) {
            policy.addPartition(new RangePartition(partition.getConnectionPool(), getPartitionValueType().getName(), partition.getStartValue(), partition.getEndValue()));
        }

        return policy;
    }

    /**
     * INTERNAL:
     * Used for XML merging and overriding.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && (objectToCompare instanceof RangePartitioningMetadata)) {
            RangePartitioningMetadata policy = (RangePartitioningMetadata) objectToCompare;

            return valuesMatch(this.partitions, policy.getPartitions());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (partitions != null ? partitions.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<RangePartitionMetadata> getPartitions() {
        return partitions;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);

        // Initialize list of objects.
        initXMLObjects(partitions, accessibleObject);
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setPartitions(List<RangePartitionMetadata> partitions) {
        this.partitions = partitions;
    }
}
