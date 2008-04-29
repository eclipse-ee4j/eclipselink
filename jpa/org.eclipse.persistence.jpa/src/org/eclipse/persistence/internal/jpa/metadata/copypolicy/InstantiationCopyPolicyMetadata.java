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
 * Used to store information about InstantiationCopyPolicy as it is read from XML or annotations
 * 
 * @see org.eclipse.persistence.annotations.InstantiationCopyPolicy
 * 
 * @author tware
 */

import java.lang.annotation.Annotation;

import org.eclipse.persistence.descriptors.copying.CopyPolicy;
import org.eclipse.persistence.descriptors.copying.InstantiationCopyPolicy;

public class InstantiationCopyPolicyMetadata extends CopyPolicyMetadata {

    // used for XML config
    public InstantiationCopyPolicyMetadata(){
    }
    
    public InstantiationCopyPolicyMetadata(Annotation copyPolicy){ 
        super();
    }
    
    public CopyPolicy getCopyPolicy(){
        return new InstantiationCopyPolicy();
    }
    
}
