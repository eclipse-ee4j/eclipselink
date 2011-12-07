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

import org.eclipse.persistence.descriptors.partitioning.FieldPartitioningPolicy;
import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

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
public abstract class FieldPartitioningMetadata extends AbstractPartitioningMetadata {
    protected ColumnMetadata partitionColumn;
    protected String partitionValueType;
    protected Boolean unionUnpartitionableQueries;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public FieldPartitioningMetadata() {
        super("<field-partitioning>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public FieldPartitioningMetadata(MetadataAnnotation annotation, MetadataAccessor accessor) {
        super(annotation, accessor);
        MetadataAnnotation column = (MetadataAnnotation)annotation.getAttribute("partitionColumn");
        if (column != null) {
            this.partitionColumn = new ColumnMetadata(column, accessor);
        }
        this.partitionValueType = (String)annotation.getAttribute("partitionValueType");
        this.unionUnpartitionableQueries = (Boolean)annotation.getAttribute("unionUnpartitionableQueries");
    }
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    protected FieldPartitioningMetadata(String elementName) {
        super(elementName);
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
    
    /**
     * INTERNAL:
     * For merging and overriding to work properly, all ORMetadata must be able 
     * to compare themselves for metadata equality.
     */
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
     * INTERNAL:
     * Used for OX mapping
     */
    public ColumnMetadata getPartitionColumn() {
        return partitionColumn;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping
     */
    public String getPartitionValueType() {
        return partitionValueType;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping
     */
    public Boolean getUnionUnpartitionableQueries() {
        return unionUnpartitionableQueries;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        initXMLObject(partitionColumn, accessibleObject);
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping
     */
    public void setPartitionColumn(ColumnMetadata partitionColumn) {
        this.partitionColumn = partitionColumn;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping
     */
    public void setPartitionValueType(String partitionValueType) {
        this.partitionValueType = partitionValueType;
    }

    /**
     * INTERNAL:
     * Used for OX mapping
     */
    public void setUnionUnpartitionableQueries(Boolean unionUnpartitionableQueries) {
        this.unionUnpartitionableQueries = unionUnpartitionableQueries;
    }
}
