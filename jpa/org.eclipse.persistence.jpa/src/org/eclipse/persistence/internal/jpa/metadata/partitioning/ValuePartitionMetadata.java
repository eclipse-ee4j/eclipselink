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

import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Define JPA meta-data for partitioning policy.
 * 
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
public class ValuePartitionMetadata extends ORMetadata {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.

    protected String connectionPool;
    protected String value;

    /**
     * Used for OX mapping.
     */
    public ValuePartitionMetadata() {
        super("<value-partition>");
    }
    
    public ValuePartitionMetadata(MetadataAnnotation annotation, MetadataAccessibleObject accessibleObject) {
        super(annotation, accessibleObject);
        this.connectionPool = (String)annotation.getAttribute("connectionPool");
        this.value = (String)annotation.getAttribute("value");
    }
    
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof ValuePartitionMetadata) {
            ValuePartitionMetadata partition = (ValuePartitionMetadata) objectToCompare;
            
            return valuesMatch(this.connectionPool, partition.getConnectionPool())
                && valuesMatch(this.value, partition.getValue());
        }
        
        return false;
    }
    
    public String getConnectionPool() {
        return connectionPool;
    }

    public void setConnectionPool(String connectionPool) {
        this.connectionPool = connectionPool;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
