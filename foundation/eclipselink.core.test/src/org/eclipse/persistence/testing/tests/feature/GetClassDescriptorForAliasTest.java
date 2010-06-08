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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.testing.framework.*;

import org.eclipse.persistence.descriptors.*;

/**
 * Bug 5166035
 * Ensure the getClassDescriptorForAlias(String) method works on both Session and Project
 * @author tware
 *
 */
public class GetClassDescriptorForAliasTest extends TestCase {

    private ClassDescriptor empDescriptorSession = null;
    private ClassDescriptor empDescriptorProject = null;
    
    public void test(){
        empDescriptorSession = getSession().getClassDescriptorForAlias("SecureSystem");
        empDescriptorProject = getSession().getProject().getDescriptorForAlias("SecureSystem");
    }
 
    public void verify(){
        if (empDescriptorSession == null){
            throw new TestErrorException("getClassDescriptorForAlias() on Session failed.");
        }
        if (empDescriptorProject == null){
            throw new TestErrorException("getClassDescriptorForAlias() on Project failed.");
        }
    }
}
