/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.changetracking;

import java.lang.annotation.Annotation;

import org.eclipse.persistence.annotations.ChangeTrackingType;
import org.eclipse.persistence.descriptors.ClassDescriptor;

import org.eclipse.persistence.descriptors.changetracking.AttributeChangeTrackingPolicy;
import org.eclipse.persistence.descriptors.changetracking.DeferredChangeDetectionPolicy;
import org.eclipse.persistence.descriptors.changetracking.ObjectChangePolicy;
import org.eclipse.persistence.descriptors.changetracking.ObjectChangeTrackingPolicy;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;

/**
 * Object to hold onto change tracking metadata.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class ChangeTrackingMetadata  {
    private Enum m_type;    
    
    /**
     * INTERNAL:
     */
    public ChangeTrackingMetadata() {}
    
    /**
     * INTERNAL:
     */
    public ChangeTrackingMetadata(Annotation changeTracking) {
        setType((Enum) MetadataHelper.invokeMethod("value", changeTracking));
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Enum getType() {
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
        ObjectChangePolicy policy = null;
                   
        if (m_type.equals(ChangeTrackingType.ATTRIBUTE)) {
            policy = new AttributeChangeTrackingPolicy();
        } else if (m_type.equals(ChangeTrackingType.OBJECT)) {
            policy = new ObjectChangeTrackingPolicy();
        } else if (m_type.equals(ChangeTrackingType.DEFERRED)) {
            policy = new DeferredChangeDetectionPolicy();
        } else if (m_type.equals(ChangeTrackingType.AUTO)) {
            // By setting the policy to null, this will unset any global 
            // settings. EclipseLink will then determine the change tracking 
            // policy at runtime.
            policy = null;
        }
               
        classDescriptor.setObjectChangePolicy(policy);
    }
        
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setType(Enum type) {
    	m_type = type;
    }
}
