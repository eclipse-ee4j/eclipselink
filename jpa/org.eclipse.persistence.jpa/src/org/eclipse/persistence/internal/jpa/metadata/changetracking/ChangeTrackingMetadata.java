/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Guy Pelletier (Oracle), February 28, 2007 
 *        - New file introduced for bug 217880.
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     08/17/2010-2.2 Guy Pelletier 
 *       - 252280:  inconsistency in change-tracking xml
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.changetracking;

import org.eclipse.persistence.annotations.ChangeTrackingType;
import org.eclipse.persistence.descriptors.ClassDescriptor;

import org.eclipse.persistence.descriptors.changetracking.AttributeChangeTrackingPolicy;
import org.eclipse.persistence.descriptors.changetracking.DeferredChangeDetectionPolicy;
import org.eclipse.persistence.descriptors.changetracking.ObjectChangeTrackingPolicy;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * Object to hold onto change tracking metadata.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class ChangeTrackingMetadata extends ORMetadata {
    private String m_type;    
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public ChangeTrackingMetadata() {
        super("<change-tracking>");
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public ChangeTrackingMetadata(MetadataAnnotation changeTracking, MetadataAccessor accessor) {
        super(changeTracking, accessor);
        
        m_type = (String) changeTracking.getAttribute("value");
    }
    
    /**
     * INTERNAL:
     * For merging and overriding to work properly, all ORMetadata must be able 
     * to compare themselves for metadata equality.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof ChangeTrackingMetadata) {
            ChangeTrackingMetadata changeTracking = (ChangeTrackingMetadata) objectToCompare;            
            return valuesMatch(m_type, changeTracking.getType());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getType() {
        return m_type;
    }
    
    /**
     * INTERNAL:
     */
    public void process(MetadataDescriptor descriptor) {
        // Set the change tracking flag on the Metadata Descriptor.
        descriptor.setHasChangeTracking();
        
        // Process the change tracking metadata.
        ClassDescriptor classDescriptor = descriptor.getClassDescriptor();
                   
        if (m_type == null || m_type.equals(ChangeTrackingType.AUTO.name())) {
            // By setting the policy to null, this will unset any global 
            // settings. EclipseLink will then determine the change tracking 
            // policy at runtime.
            classDescriptor.setObjectChangePolicy(null);
        } else if (m_type.equals(ChangeTrackingType.ATTRIBUTE.name())) {
            classDescriptor.setObjectChangePolicy(new AttributeChangeTrackingPolicy());
        } else if (m_type.equals(ChangeTrackingType.OBJECT.name())) {
            classDescriptor.setObjectChangePolicy(new ObjectChangeTrackingPolicy());
        } else if (m_type.equals(ChangeTrackingType.DEFERRED.name())) {
            classDescriptor.setObjectChangePolicy(new DeferredChangeDetectionPolicy());
        }
    }
        
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setType(String type) {
        m_type = type;
    }
}
