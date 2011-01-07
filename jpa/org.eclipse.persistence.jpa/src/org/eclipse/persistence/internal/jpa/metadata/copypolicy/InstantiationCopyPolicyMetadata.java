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
 *     tware - March 28/2008 - 1.0M7 - Initial implementation
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.copypolicy;

import org.eclipse.persistence.descriptors.copying.CopyPolicy;
import org.eclipse.persistence.descriptors.copying.InstantiationCopyPolicy;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Used to store information about InstantiationCopyPolicy as it is read from 
 * XML or annotations
 * 
 * @see org.eclipse.persistence.annotations.InstantiationCopyPolicy
 * @author tware
 */
public class InstantiationCopyPolicyMetadata extends CopyPolicyMetadata {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public InstantiationCopyPolicyMetadata() {
        super("<instantiation-copy-policy>");
    }
    
    /**
     * INTERNAL:
     */
    public InstantiationCopyPolicyMetadata(MetadataAnnotation copyPolicy, MetadataAccessibleObject accessibleObject) { 
        super(copyPolicy, accessibleObject);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        return super.equals(objectToCompare) && objectToCompare instanceof InstantiationCopyPolicyMetadata;
    }

    /**
     * INTERNAL:
     */
    public CopyPolicy getCopyPolicy() {
        return new InstantiationCopyPolicy();
    }
}
