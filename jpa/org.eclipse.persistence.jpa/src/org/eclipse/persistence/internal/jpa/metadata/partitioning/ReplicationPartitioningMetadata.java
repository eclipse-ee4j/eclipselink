/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.partitioning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.descriptors.partitioning.ReplicationPartitioningPolicy;

/**
 * INTERNAL:
 * Define JPA meta-data for partitioning policy.
 * 
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
public class ReplicationPartitioningMetadata extends AbstractPartitioningMetadata {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.

    protected List<String> connectionPools;
    
    /**
     * Used for OX mapping.
     */
    public ReplicationPartitioningMetadata() {
        super("<replication-partitioning>");
    }
    
    public ReplicationPartitioningMetadata(String elementName) {
        super(elementName);
    }

    public ReplicationPartitioningMetadata(MetadataAnnotation annotation, MetadataAccessibleObject accessibleObject) {
        super(annotation, accessibleObject);
        this.connectionPools = new ArrayList<String>();
        this.connectionPools.addAll((List)Arrays.asList((Object[]) annotation.getAttributeArray("connectionPools")));
    }

    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && (objectToCompare instanceof ReplicationPartitioningMetadata)) {
            ReplicationPartitioningMetadata policy = (ReplicationPartitioningMetadata) objectToCompare;
            
            return valuesMatch(this.connectionPools, policy.getConnectionPools());
        }
        
        return false;
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
