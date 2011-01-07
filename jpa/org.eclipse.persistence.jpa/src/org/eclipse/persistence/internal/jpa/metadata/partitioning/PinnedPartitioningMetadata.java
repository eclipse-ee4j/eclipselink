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
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.partitioning;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.descriptors.partitioning.PinnedPartitioningPolicy;

/**
 * INTERNAL:
 * Define JPA meta-data for partitioning policy.
 * 
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
public class PinnedPartitioningMetadata extends AbstractPartitioningMetadata {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.

    protected String connectionPool;
    
    /**
     * Used for OX mapping.
     */
    public PinnedPartitioningMetadata() {
        super("<pinned-partitioning>");
    }

    public PinnedPartitioningMetadata(MetadataAnnotation annotation, MetadataAccessibleObject accessibleObject) {
        super(annotation, accessibleObject);
        this.connectionPool = (String)annotation.getAttribute("connectionPool");
    }

    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && (objectToCompare instanceof PinnedPartitioningMetadata)) {
            PinnedPartitioningMetadata policy = (PinnedPartitioningMetadata) objectToCompare;
            
            return valuesMatch(this.connectionPool, policy.getConnectionPool());
        }
        
        return false;
    }

    public String getConnectionPool() {
        return connectionPool;
    }

    @Override
    public PartitioningPolicy buildPolicy() {
        PinnedPartitioningPolicy policy = new PinnedPartitioningPolicy();
        super.buildPolicy(policy);
        policy.setConnectionPool(getConnectionPool());
        return policy;
    }

    public void setConnectionPool(String connectionPool) {
        this.connectionPool = connectionPool;
    }
}
