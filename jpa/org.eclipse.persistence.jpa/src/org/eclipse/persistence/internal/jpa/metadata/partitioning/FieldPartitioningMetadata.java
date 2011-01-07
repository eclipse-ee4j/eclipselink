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

import org.eclipse.persistence.descriptors.partitioning.FieldPartitioningPolicy;
import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;

/**
 * INTERNAL:
 * Define JPA meta-data for partitioning policy.
 * 
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
public abstract class FieldPartitioningMetadata extends AbstractPartitioningMetadata {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.

    protected ColumnMetadata partitionColumn;
    protected String partitionValueType;
    protected Boolean unionUnpartitionableQueries;

    public FieldPartitioningMetadata() {
        super("<field-partitioning>");
    }
    
    public FieldPartitioningMetadata(String elementName) {
        super(elementName);
    }

    public FieldPartitioningMetadata(MetadataAnnotation annotation, MetadataAccessibleObject accessibleObject) {
        super(annotation, accessibleObject);
        MetadataAnnotation column = (MetadataAnnotation)annotation.getAttribute("partitionColumn");
        if (column != null) {
            this.partitionColumn = new ColumnMetadata(column, accessibleObject);
        }
        this.partitionValueType = (String)annotation.getAttribute("partitionValueType");
        this.unionUnpartitionableQueries = (Boolean)annotation.getAttribute("unionUnpartitionableQueries");
    }

    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && (objectToCompare instanceof FieldPartitioningMetadata)) {
            FieldPartitioningMetadata policy = (FieldPartitioningMetadata) objectToCompare;
            
            return valuesMatch(this.partitionColumn, policy.getPartitionColumn())
                    && valuesMatch(this.unionUnpartitionableQueries, policy.getUnionUnpartitionableQueries())
                    && valuesMatch(this.partitionValueType, policy.getPartitionValueType());
        }
        
        return false;
    }
    
    /**
     * Set common fields into policy.
     * Should be called by subclasses.
     */
    @Override
    public void buildPolicy(PartitioningPolicy policy) {
        super.buildPolicy(policy);
        if (this.partitionColumn != null) {
            ((FieldPartitioningPolicy)policy).setPartitionField(this.partitionColumn.getDatabaseField());
        }
        if (this.unionUnpartitionableQueries != null) {
            ((FieldPartitioningPolicy)policy).setUnionUnpartitionableQueries(this.unionUnpartitionableQueries);
        }
    }
    
    public ColumnMetadata getPartitionColumn() {
        return partitionColumn;
    }

    public void setPartitionColumn(ColumnMetadata partitionColumn) {
        this.partitionColumn = partitionColumn;
    }

    public String getPartitionValueType() {
        return partitionValueType;
    }

    public void setPartitionValueType(String partitionValueType) {
        this.partitionValueType = partitionValueType;
    }

    public Boolean getUnionUnpartitionableQueries() {
        return unionUnpartitionableQueries;
    }

    public void setUnionUnpartitionableQueries(Boolean unionUnpartitionableQueries) {
        this.unionUnpartitionableQueries = unionUnpartitionableQueries;
    }
}
