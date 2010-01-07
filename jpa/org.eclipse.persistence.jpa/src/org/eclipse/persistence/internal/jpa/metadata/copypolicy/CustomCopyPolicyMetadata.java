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

import org.eclipse.persistence.descriptors.copying.CopyPolicy;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

/**
 * Used to store information about CopyPolicy as it is read from XML or 
 * annotations
 * 
 * @see org.eclipse.persistence.annotations.CopyPolicy
 * @author tware
 */
public class CustomCopyPolicyMetadata extends CopyPolicyMetadata {
    private String copyPolicyClassName;
    private MetadataClass copyPolicyClass;
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public CustomCopyPolicyMetadata() {
        super("<copy-policy");
    }
    
    /**
     * INTERNAL:
     */
    public CustomCopyPolicyMetadata(MetadataAnnotation copyPolicy, MetadataAccessibleObject accessibleObject) {
        super(copyPolicy, accessibleObject);
        
        this.copyPolicyClass = getMetadataClass((String) copyPolicy.getAttribute("value"));
    }
    
    /**
     * INTERNAL:
     */
    public CopyPolicy getCopyPolicy(){
        assert(false); // we should never get here
        return null;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getCopyPolicyClassName(){
        return copyPolicyClassName;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        copyPolicyClass = initXMLClassName(copyPolicyClassName);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void process(MetadataDescriptor descriptor) {
        descriptor.setHasCopyPolicy();
        descriptor.getClassDescriptor().setCopyPolicyClassName(copyPolicyClass.getName());
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCopyPolicyClassName(String copyPolicyClassName) {
        this.copyPolicyClassName = copyPolicyClassName;
    }
}
