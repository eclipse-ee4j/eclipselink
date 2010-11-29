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

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.descriptors.partitioning.UnionPartitioningPolicy;

/**
 * INTERNAL:
 * Define JPA meta-data for partitioning policy.
 * 
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
public class UnionPartitioningMetadata extends ReplicationPartitioningMetadata {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.

    protected Boolean replicateWrites;

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public UnionPartitioningMetadata() {
        super("<union-partitioning>");
    }

    public UnionPartitioningMetadata(MetadataAnnotation annotation, MetadataAccessibleObject accessibleObject) {
        super(annotation, accessibleObject);
        this.replicateWrites = (Boolean)annotation.getAttribute("replicateWrites");
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
    public PartitioningPolicy buildPolicy() {
        UnionPartitioningPolicy policy = new UnionPartitioningPolicy();
        super.buildPolicy(policy);
        if (this.replicateWrites != null) {
            policy.setReplicateWrites(this.replicateWrites);
        }
        return policy;
    }
    
    public boolean getReplicateWrites() {
        return replicateWrites;
    }

    public void setReplicateWrites(boolean replicateWrites) {
        this.replicateWrites = replicateWrites;
    }
}
