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

import org.eclipse.persistence.descriptors.partitioning.CustomPartitioningPolicy;
import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

/**
 * INTERNAL:
 * Define JPA meta-data for partitioning policy.
 * 
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
public class PartitioningMetadata extends AbstractPartitioningMetadata {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.

    protected String className;
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public PartitioningMetadata() {
        super("<partitioning>");
    }
    
    /**
     * INTERNAL:
     */
    public PartitioningMetadata(MetadataAnnotation annotation, MetadataAccessibleObject accessibleObject) {
        super(annotation, accessibleObject);

        this.className = (String)annotation.getAttribute("partitioningClass");        
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && (objectToCompare instanceof PartitioningMetadata)) {
            PartitioningMetadata policy = (PartitioningMetadata) objectToCompare;
            
            return valuesMatch(this.className, policy.getClassName());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getClassName() {
        return this.className;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        this.className = initXMLClassName(this.className).getName();
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setClassName(String className) {
        this.className = className;
    }
    
    /**
     * Cannot instantiate policy until the correct class loader is available.
     */
    @Override
    public PartitioningPolicy buildPolicy() {
        CustomPartitioningPolicy policy = new CustomPartitioningPolicy();
        super.buildPolicy(policy);
        policy.setPartitioningClasName(this.className);
        return policy;
    }
}
