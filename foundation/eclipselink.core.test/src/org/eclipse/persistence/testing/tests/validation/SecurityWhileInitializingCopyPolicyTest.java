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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.descriptors.copying.CloneCopyPolicy;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;


/**
 * Code coverage test for clone copy policy exception.
 */
public class SecurityWhileInitializingCopyPolicyTest extends ExceptionTestSaveSecurityManager {
    CloneCopyPolicy policy;

    public SecurityWhileInitializingCopyPolicyTest() {
        setDescription("This tests Security While Initializing Copy Policy (TL-ERROR 89)");
    }

    protected void setup() {
        super.setup();
        expectedException = DescriptorException.securityWhileInitializingCopyPolicy("dummy_Method", new RelationalDescriptor(), new Exception());

        RelationalDescriptor descriptor = new RelationalDescriptor();

        //need superclass == null    
        descriptor.setJavaClass(SecurityWhileInitializingCopyPolicyTest.class);
        descriptor.setCopyPolicy(new CloneCopyPolicy());
        policy = (CloneCopyPolicy)descriptor.getCopyPolicy();

        //need not getMethodName() == null
        policy.setMethodName("dummy_Method"); //this method does exist in above class
        //need NoSuchMethod thrown from     		this.setMethod(Helper.getDeclaredMethod(this.getDescriptor().getJavaClass(), this.getMethodName(), new Class[0]));
    }

    public void test() {
        try {
            policy.initialize(getSession());

        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    public void dummy_Method() {
        //do nothing as security manager will cause error to occur.
    }
}
