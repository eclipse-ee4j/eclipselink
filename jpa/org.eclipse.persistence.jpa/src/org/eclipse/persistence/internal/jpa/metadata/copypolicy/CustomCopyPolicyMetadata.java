/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.copypolicy;

import org.eclipse.persistence.descriptors.copying.CopyPolicy;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;

import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

/**
 * Used to store information about CopyPolicy as it is read from XML or 
 * annotations
 *
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @see org.eclipse.persistence.annotations.CopyPolicy
 * @author tware
 */
public class CustomCopyPolicyMetadata extends CopyPolicyMetadata {
    private String copyPolicyClassName;
    private MetadataClass copyPolicyClass;
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public CustomCopyPolicyMetadata() {
        super("<copy-policy");
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public CustomCopyPolicyMetadata(MetadataAnnotation copyPolicy, MetadataAccessor accessor) {
        super(copyPolicy, accessor);
        
        this.copyPolicyClass = getMetadataClass((String) copyPolicy.getAttribute("value"));
    }
    
    /**
     * INTERNAL:
     * For merging and overriding to work properly, all ORMetadata must be able 
     * to compare themselves for metadata equality.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof CustomCopyPolicyMetadata) {
            CustomCopyPolicyMetadata customCopyPoliy = (CustomCopyPolicyMetadata) objectToCompare;
            return valuesMatch(copyPolicyClassName, customCopyPoliy.getCopyPolicyClassName());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     */
    @Override
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
