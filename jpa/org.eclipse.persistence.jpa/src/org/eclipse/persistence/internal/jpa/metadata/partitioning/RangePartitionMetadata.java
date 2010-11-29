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
public class RangePartitionMetadata extends ORMetadata {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.

    protected String connectionPool;
    protected String startValue;
    protected String endValue;

    /**
     * Used for OX mapping.
     */
    public RangePartitionMetadata() {
        super("<range-partition>");
    }
    
    public RangePartitionMetadata(MetadataAnnotation annotation, MetadataAccessibleObject accessibleObject) {
        super(annotation, accessibleObject);
        this.connectionPool = (String)annotation.getAttribute("connectionPool");
        this.startValue = (String)annotation.getAttribute("startValue");
        this.endValue = (String)annotation.getAttribute("endValue");
    }
    
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
    
    public String getConnectionPool() {
        return connectionPool;
    }

    public void setConnectionPool(String connectionPool) {
        this.connectionPool = connectionPool;
    }

    public String getStartValue() {
        return startValue;
    }

    public void setStartValue(String startValue) {
        this.startValue = startValue;
    }

    public String getEndValue() {
        return endValue;
    }

    public void setEndValue(String endValue) {
        this.endValue = endValue;
    }
}
