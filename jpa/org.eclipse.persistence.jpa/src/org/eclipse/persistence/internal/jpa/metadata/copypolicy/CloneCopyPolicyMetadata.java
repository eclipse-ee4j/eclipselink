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
 * Used to store information about CloneCopyPolicy as it is read from XML or annotations
 * 
 * @see org.eclipse.persistence.annotations.CloneCopyPolicy
 * 
 * @author tware
 */
import java.lang.annotation.Annotation;

import org.eclipse.persistence.descriptors.copying.CopyPolicy;
import org.eclipse.persistence.descriptors.copying.CloneCopyPolicy;

import org.eclipse.persistence.exceptions.ValidationException;

public class CloneCopyPolicyMetadata extends CopyPolicyMetadata {
    
    private String methodName;
    private String workingCopyMethodName;
    
    // used for XML config
    public CloneCopyPolicyMetadata(){
    }
    
    public CloneCopyPolicyMetadata(Annotation copyPolicy){
        methodName = (String) MetadataHelper.invokeMethod("method", copyPolicy);      
        workingCopyMethodName = (String) MetadataHelper.invokeMethod("workingCopyMethod", copyPolicy);
    }
    
    public CopyPolicy getCopyPolicy(){
        if (methodName == null && workingCopyMethodName == null){
            throw ValidationException.copyPolicyMustSpecifyEitherMethodOrWorkingCopyMethod(javaClassName);
        }
        CloneCopyPolicy copyPolicy = new CloneCopyPolicy();
        copyPolicy.setMethodName(methodName);
        copyPolicy.setWorkingCopyMethodName(workingCopyMethodName);
        return copyPolicy;
    }
    
    public String getMethodName(){
        return methodName;
    }
    
    public String getWorkingCopyMethodName(){
        return workingCopyMethodName;
    }
    
    public void setMethodName(String methodName){
        this.methodName = methodName;
    }
    
    public void setWorkingCopyMethodName(String workingCopyMethodName){
        this.workingCopyMethodName = workingCopyMethodName;
    }
}
