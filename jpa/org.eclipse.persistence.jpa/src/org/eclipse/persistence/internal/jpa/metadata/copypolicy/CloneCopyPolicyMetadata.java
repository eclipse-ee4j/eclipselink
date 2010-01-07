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
import org.eclipse.persistence.descriptors.copying.CloneCopyPolicy;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Used to store information about CloneCopyPolicy as it is read from XML or 
 * annotations.
 * 
 * @see org.eclipse.persistence.annotations.CloneCopyPolicy
 * @author tware
 */
public class CloneCopyPolicyMetadata extends CopyPolicyMetadata {
    private String methodName;
    private String workingCopyMethodName;
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public CloneCopyPolicyMetadata() {
        super("<clone-copy-policy>");
    }
    
    /**
     * INTERNAL:
     */
    public CloneCopyPolicyMetadata(MetadataAnnotation copyPolicy, MetadataAccessibleObject accessibleObject) {
        super(copyPolicy, accessibleObject);
        
        methodName = (String) copyPolicy.getAttribute("method");      
        workingCopyMethodName = (String) copyPolicy.getAttribute("workingCopyMethod");
    }
    
    /**
     * INTERNAL:
     */
    public CopyPolicy getCopyPolicy() {
        if (methodName == null && workingCopyMethodName == null){
            throw ValidationException.copyPolicyMustSpecifyEitherMethodOrWorkingCopyMethod(getLocation());
        }
        
        CloneCopyPolicy copyPolicy = new CloneCopyPolicy();
        copyPolicy.setMethodName(methodName);
        copyPolicy.setWorkingCopyMethodName(workingCopyMethodName);
        return copyPolicy;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getMethodName() {
        return methodName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getWorkingCopyMethodName() {
        return workingCopyMethodName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setWorkingCopyMethodName(String workingCopyMethodName) {
        this.workingCopyMethodName = workingCopyMethodName;
    }
}
