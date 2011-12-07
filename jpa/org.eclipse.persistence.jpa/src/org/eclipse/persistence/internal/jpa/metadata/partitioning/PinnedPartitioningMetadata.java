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

import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.descriptors.partitioning.PinnedPartitioningPolicy;

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
public class PinnedPartitioningMetadata extends AbstractPartitioningMetadata {
    protected String connectionPool;
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public PinnedPartitioningMetadata() {
        super("<pinned-partitioning>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public PinnedPartitioningMetadata(MetadataAnnotation annotation, MetadataAccessor accessor) {
        super(annotation, accessor);
        this.connectionPool = (String)annotation.getAttribute("connectionPool");
    }

    /**
     * INTENAL:
     */
    @Override
    public PartitioningPolicy buildPolicy() {
        PinnedPartitioningPolicy policy = new PinnedPartitioningPolicy();
        super.buildPolicy(policy);
        policy.setConnectionPool(getConnectionPool());
        return policy;
    }
    
    /**
     * INTERNAL:
     * For merging and overriding to work properly, all ORMetadata must be able 
     * to compare themselves for metadata equality.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && (objectToCompare instanceof PinnedPartitioningMetadata)) {
            PinnedPartitioningMetadata policy = (PinnedPartitioningMetadata) objectToCompare;
            
            return valuesMatch(this.connectionPool, policy.getConnectionPool());
        }
        
        return false;
    }
    
    /**
     * INTENAL:
     * Used for OX mapping.
     */
    public String getConnectionPool() {
        return connectionPool;
    }
    
    /**
     * INTENAL:
     * Used for OX mapping.
     */
    public void setConnectionPool(String connectionPool) {
        this.connectionPool = connectionPool;
    }
}
