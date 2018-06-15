/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.sessions.AbstractSession;


//Created by Ian Reid
//Date: Feb 27, 2k3

public class NoSuchMethodWhileInitializingClassExtractionMethodTest extends ExceptionTest {
    public NoSuchMethodWhileInitializingClassExtractionMethodTest() {
        super();
        setDescription("This tests No Such Method While Initializing Class Extraction Method (TL-ERROR 61)");
    }

    protected void setup() {
        expectedException = DescriptorException.noSuchMethodWhileInitializingClassExtractionMethod("Dummy_Method", new RelationalDescriptor(), new Exception());
    }

    public void test() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(NoSuchMethodWhileInitializingClassExtractionMethodTest.class);
        descriptor.setInheritancePolicy(new InheritancePolicy());

        InheritancePolicy policy = descriptor.getInheritancePolicy();
        //need not getClassExtractionMethodName() == null
        policy.setClassExtractionMethodName("Dummy_Method"); //this method does not exist in above class
        //need NoSuchMethod thrown from         setClassExtractionMethod(Helper.getDeclaredMethod(getDescriptor().getJavaClass(), getClassExtractionMethodName(), declarationParameters));

        //nice if isChildDescriptor() == false
        policy.setParentClass(null);

        try {

            policy.preInitialize((AbstractSession)getSession());

        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

}
