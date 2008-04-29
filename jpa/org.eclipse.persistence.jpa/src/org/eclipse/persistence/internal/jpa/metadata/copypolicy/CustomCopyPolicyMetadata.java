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
 *     tware - March 28/2008 - 1.0M7 - Initial implementation
 ******************************************************************************/  

package org.eclipse.persistence.internal.jpa.metadata.copypolicy;

/**
 * INTERNAL:
 * 
 * Used to store information about CopyPolicy as it is read from XML or annotations
 * 
 * @see org.eclipse.persistence.annotations.CopyPolicy
 * 
 * @author tware
 */
import java.lang.annotation.Annotation;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.copying.CopyPolicy;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;

public class CustomCopyPolicyMetadata extends CopyPolicyMetadata {   

    private String copyPolicyClassName;
    private Class copyPolicyClass;
    
    // used for XML config
    public CustomCopyPolicyMetadata(){
    }
    
    public CustomCopyPolicyMetadata(Annotation copyPolicy){
        copyPolicyClass = (Class) MetadataHelper.invokeMethod("value", copyPolicy);
        copyPolicyClassName = copyPolicyClass.getName();
    }
    
    public CopyPolicy getCopyPolicy(){
        assert(false); // we should never get here
        return null;
    }
    
    public String getCopyPolicyClassName(){
        return copyPolicyClassName;
    }
    
    @Override
    public void process(MetadataDescriptor descriptor, Class javaClass){
        descriptor.setHasCopyPolicy();
        this.javaClassName = javaClass.getName();
        ClassDescriptor classDescriptor = descriptor.getClassDescriptor();
        if (copyPolicyClass == null){
            classDescriptor.setCopyPolicyClassName(descriptor.getClassAccessor().getEntityMappings().getFullyQualifiedClassName(copyPolicyClassName));
        } else {
            classDescriptor.setCopyPolicyClassName(copyPolicyClassName);
        }
    }
    
    public void setCopyPolicyClassName(String className){
        this.copyPolicyClassName = className;
    }
}
