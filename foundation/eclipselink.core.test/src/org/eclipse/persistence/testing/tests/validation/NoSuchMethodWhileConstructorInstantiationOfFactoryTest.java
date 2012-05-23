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
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;


//Created by Ian Reid
//Date: Mar 21, 2k3
//uses class ClassWithProblemConstructor

public class NoSuchMethodWhileConstructorInstantiationOfFactoryTest extends ExceptionTest {
    public NoSuchMethodWhileConstructorInstantiationOfFactoryTest() {
        setDescription("This tests No Such Method While Constructor Instantiation Of Factory (TL-ERROR 172)");
    }
    RelationalDescriptor descriptor;
    InstantiationPolicy policy;
    IntegrityChecker orgIntegrityChecker;

    protected void setup() {
        descriptor = new RelationalDescriptor();
        //   descriptor.setJavaClass(ClassWithProblemConstructor.class);
        descriptor.setJavaClass(ExceptionTest.class); //no problems
        descriptor.addTableName("EMPLOYEE");
        policy = descriptor.getInstantiationPolicy();
        policy.setMethodName("invalidMethod");
        //   policy.useFactoryInstantiationPolicy(ClassWithProblemConstructor.class,"invalidMethod");
        policy.useFactoryInstantiationPolicy(ExceptionTest.class, "invalidMethod");

        orgIntegrityChecker = getSession().getIntegrityChecker();
        getSession().setIntegrityChecker(new IntegrityChecker()); //moved into setup
        getSession().getIntegrityChecker().dontCatchExceptions(); //moved into setup

        expectedException = DescriptorException.noSuchMethodWhileConstructorInstantiationOfFactory(descriptor, new Exception());
    }

    public void reset() {
        getSession().setIntegrityChecker(orgIntegrityChecker);
    }

    public void test() {
        try {
            policy.initialize((AbstractSession)getSession());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }
    //  public static String invalidMethod(DatabaseRecord row) throws java.lang.IllegalAccessException {
    //    throw new java.lang.IllegalAccessException();
    //  }
}
