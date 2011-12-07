/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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

import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetConstructorFor;
import org.eclipse.persistence.internal.security.PrivilegedInvokeConstructor;

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
        for (Object partitionAnnotation : (Object[])annotation.getAttributeArray("partitions")) {
            this.partitions.add(new RangePartitionMetadata((MetadataAnnotation)partitionAnnotation, accessor));
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    public PartitioningPolicy buildPolicy() {
        RangePartitioningPolicy policy = new RangePartitioningPolicy();
        super.buildPolicy(policy);
        Class type = String.class;
        if (this.partitionValueType != null) {
            type = getJavaClass(getMetadataClass(this.partitionValueType));
        }
        for (RangePartitionMetadata partition : getPartitions()) {
            Comparable startValue = null;
            if (partition.getStartValue() != null) {
                startValue = (Comparable)initObject(type, partition.getStartValue());
            }
            Comparable endValue = null;
            if (partition.getEndValue() != null) {
                endValue = (Comparable)initObject(type, partition.getEndValue());
            }
            policy.addPartition(new RangePartition(partition.getConnectionPool(), startValue, endValue));
        }
        return policy;
    }
    
    /**
     * INTERNAL:
     * For merging and overriding to work properly, all ORMetadata must be able 
     * to compare themselves for metadata equality.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && (objectToCompare instanceof RangePartitioningMetadata)) {
            RangePartitioningMetadata policy = (RangePartitioningMetadata) objectToCompare;
            
            return valuesMatch(this.partitions, policy.getPartitions());
        }
        
        return false;
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
     * TODO: this needs to be done at runtime.
     */    
    private Object initObject(Class type, String value) {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            try {
                Constructor constructor = (Constructor) AccessController.doPrivileged(new PrivilegedGetConstructorFor(type, new Class[] {String.class}, false));
                return AccessController.doPrivileged(new PrivilegedInvokeConstructor(constructor, new Object[] {value}));
            } catch (PrivilegedActionException exception) {
                //throwInitObjectException(exception, type, value, isData);
            }
        } else {
            try {
                Constructor constructor = PrivilegedAccessHelper.getConstructorFor(type, new Class[] {String.class}, false);
                return PrivilegedAccessHelper.invokeConstructor(constructor, new Object[] {value});
            } catch (Exception exception) {
                //throwInitObjectException(exception, type, value, isData);
            }
        }
        
        return value;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setPartitions(List<RangePartitionMetadata> partitions) {
        this.partitions = partitions;
    }
}
