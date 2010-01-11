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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.DatabaseRecord;


//Created by Ian Reid
//Date: April 25, 2k3

public class SecurityWhileInitializingClassExtractionMethodTest extends ExceptionTestSaveSecurityManager {

    org.eclipse.persistence.descriptors.InheritancePolicy policy;

    public SecurityWhileInitializingClassExtractionMethodTest() {
        super();
        setDescription("This tests Security While Initializing Class Extraction Method (TL-ERROR 88)");
    }

    protected void setup() {
        super.setup();
        expectedException = DescriptorException.securityWhileInitializingClassExtractionMethod("Dummy_Method", new RelationalDescriptor(), new Exception());
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(SecurityWhileInitializingClassExtractionMethodTest.class);
        descriptor.setInheritancePolicy(new InheritancePolicy());

        policy = descriptor.getInheritancePolicy();
        //need not getClassExtractionMethodName() == null
        policy.setClassExtractionMethodName("dummy_Method"); //this method does exist in above class
        //need NoSuchMethod thrown from 		setClassExtractionMethod(Helper.getDeclaredMethod(getDescriptor().getJavaClass(), getClassExtractionMethodName(), declarationParameters));

        //nice if isChildDescriptor() == false 
        policy.setParentClass(null);
    }

    public void test() {
        try {

            policy.preInitialize((AbstractSession)getSession());

        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    public void dummy_Method(DatabaseRecord row) {
        //do nothing as security manager will cause error to occur.
    }

}
