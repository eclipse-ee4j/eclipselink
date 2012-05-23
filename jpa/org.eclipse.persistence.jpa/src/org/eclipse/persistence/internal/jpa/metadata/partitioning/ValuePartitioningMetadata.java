/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - initial API and implementation
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.partitioning;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.descriptors.partitioning.ValuePartitioningPolicy;
import org.eclipse.persistence.exceptions.ValidationException;

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
public class ValuePartitioningMetadata extends FieldPartitioningMetadata {
    protected List<ValuePartitionMetadata> partitions;
    protected String defaultConnectionPool;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public ValuePartitioningMetadata() {
        super("<value-partitioning>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public ValuePartitioningMetadata(MetadataAnnotation annotation, MetadataAccessor accessor) {
        super(annotation, accessor);
        this.defaultConnectionPool = (String)annotation.getAttribute("defaultConnectionPool");
        this.partitions = new ArrayList<ValuePartitionMetadata>();
        for (Object partitionAnnotation : (Object[])annotation.getAttributeArray("partitions")) {
            this.partitions.add(new ValuePartitionMetadata((MetadataAnnotation)partitionAnnotation, accessor));
        }
    }

    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && (objectToCompare instanceof ValuePartitioningMetadata)) {
            ValuePartitioningMetadata policy = (ValuePartitioningMetadata) objectToCompare;
            
            return valuesMatch(this.partitions, policy.getPartitions());
        }
        
        return false;
    }
    
    /**
     * Convert the string value to the class type.
     * This will handle numbers, string, dates, and most other classes.
     */    
    private Object initObject(Class type, String value) {
        return ConversionManager.getDefaultManager().convertObject(value, type);
    }

    @Override
    public PartitioningPolicy buildPolicy() {
        ValuePartitioningPolicy policy = new ValuePartitioningPolicy();
        super.buildPolicy(policy);
        Class type = String.class;
        if (this.partitionValueType != null) {
            type = getJavaClass(getMetadataClass(this.partitionValueType));
        }
        policy.setDefaultConnectionPool(this.defaultConnectionPool);
        for (ValuePartitionMetadata partition : getPartitions()) {
            Object value = initObject(type, partition.getValue());
            if (policy.getPartitions().containsKey(value)) {
                throw ValidationException.duplicatePartitionValue(getName(), value);
            }
            policy.addPartition(value, partition.getConnectionPool());
        }
        return policy;
    }
    
    public List<ValuePartitionMetadata> getPartitions() {
        return partitions;
    }

    public void setPartitions(List<ValuePartitionMetadata> partitions) {
        this.partitions = partitions;
    }
    
    public String getDefaultConnectionPool() {
        return defaultConnectionPool;
    }

    public void setDefaultConnectionPool(String defaultConnectionPool) {
        this.defaultConnectionPool = defaultConnectionPool;
    }
}
