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

import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
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
public abstract class AbstractPartitioningMetadata extends ORMetadata {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.
    
    protected String name;

    /**
     * Used for OX mapping.
     */
    public AbstractPartitioningMetadata(String elementName) {
        super(elementName);
    }

    public AbstractPartitioningMetadata(MetadataAnnotation annotation, MetadataAccessibleObject accessibleObject) {
        super(annotation, accessibleObject);
        this.name = (String)annotation.getAttribute("name");
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Equals is used for processing overrides from XML.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof AbstractPartitioningMetadata) {
            AbstractPartitioningMetadata policy = (AbstractPartitioningMetadata) objectToCompare;
            
            return valuesMatch(this.name, policy.getName());
        }
        
        return false;
    }
    
    /**
     * Require subclass to build policy.
     */
    public abstract PartitioningPolicy buildPolicy();
    
    /**
     * Set common fields into policy.
     * Should be called by subclasses.
     */
    public void buildPolicy(PartitioningPolicy policy) {
        policy.setName(this.name);
    }
}
