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
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;


//Created by Ian Reid
//Date: Mar 20, 2k3

public class TargetInvocationWhileMethodInstantiationTest extends ExceptionTest {
    public TargetInvocationWhileMethodInstantiationTest() {
        setDescription("This tests Target Invocation While Method Instantiation (TL-ERROR 104)");
    }
    RelationalDescriptor descriptor;
    InstantiationPolicy policy;

    protected void setup() {
        descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(TargetInvocationWhileMethodInstantiationTest.class);
        descriptor.addTableName("DummyTable");
        policy = descriptor.getInstantiationPolicy();
        policy.useMethodInstantiationPolicy("invalidMethod");
        policy.initialize((AbstractSession)getSession());

        expectedException = DescriptorException.targetInvocationWhileMethodInstantiation("invalidMethod", descriptor, new Exception());
    }

    public void test() {
        try {
            policy.buildNewInstance();
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    public static void invalidMethod() throws java.lang.IllegalAccessException {
        throw new java.lang.IllegalAccessException();
    }

}
