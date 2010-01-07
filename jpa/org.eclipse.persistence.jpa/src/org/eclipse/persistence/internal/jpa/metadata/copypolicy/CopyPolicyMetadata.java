/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - March 28/2008 - 1.0M7 - Initial implementation
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.copypolicy;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.descriptors.copying.CopyPolicy;

/**
 * INTERNAL:
 * Incapsulates common behavior amount class for all the different types of 
 * copy policy metadata
 * 
 * @see org.eclipse.persistence.internal.jpa.metadata.copypolicy.CustomCopyPolicy
 * @see org.eclipse.persistence.internal.jpa.metadata.copypolicy.InstantiationCopyPolicy
 * @see org.eclipse.persistence.internal.jpa.metadata.copypolicy.CloneCopyPolicy
 * 
 * @author tware
 */
public abstract class CopyPolicyMetadata extends ORMetadata {   
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    protected CopyPolicyMetadata(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    protected CopyPolicyMetadata(MetadataAnnotation annotation, MetadataAccessibleObject accessibleObject) {
        super(annotation, accessibleObject);
    }
    
    /**
     * INTERNAL:
     */
    public void process(MetadataDescriptor descriptor) {
        descriptor.setHasCopyPolicy();
        ClassDescriptor classDescriptor = descriptor.getClassDescriptor();       
        classDescriptor.setCopyPolicy(getCopyPolicy());
    }
    
    /**
     * INTERNAL:
     */
    public abstract CopyPolicy getCopyPolicy();
}
