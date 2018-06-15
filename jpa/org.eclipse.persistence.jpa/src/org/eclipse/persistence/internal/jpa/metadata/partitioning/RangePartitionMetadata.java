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

import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

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
public class RangePartitionMetadata extends ORMetadata {
    protected String connectionPool;
    protected String startValue;
    protected String endValue;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public RangePartitionMetadata() {
        super("<range-partition>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public RangePartitionMetadata(MetadataAnnotation annotation, MetadataAccessor accessor) {
        super(annotation, accessor);
        this.connectionPool = annotation.getAttributeString("connectionPool");
        this.startValue = annotation.getAttributeString("startValue");
        this.endValue = annotation.getAttributeString("endValue");
    }

    /**
     * INTERNAL:
     * Used for XML merge and overriding.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof RangePartitionMetadata) {
            RangePartitionMetadata partition = (RangePartitionMetadata) objectToCompare;

            return valuesMatch(this.connectionPool, partition.getConnectionPool())
                    && valuesMatch(this.startValue, partition.getStartValue())
                    && valuesMatch(this.endValue, partition.getEndValue());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = connectionPool != null ? connectionPool.hashCode() : 0;
        result = 31 * result + (startValue != null ? startValue.hashCode() : 0);
        result = 31 * result + (endValue != null ? endValue.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getConnectionPool() {
        return connectionPool;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getEndValue() {
        return endValue;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getStartValue() {
        return startValue;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setConnectionPool(String connectionPool) {
        this.connectionPool = connectionPool;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setEndValue(String endValue) {
        this.endValue = endValue;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setStartValue(String startValue) {
        this.startValue = startValue;
    }
}
