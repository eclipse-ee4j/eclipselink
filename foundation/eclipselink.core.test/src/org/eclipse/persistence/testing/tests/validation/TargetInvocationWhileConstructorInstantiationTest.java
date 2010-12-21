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

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;


//Created by Ian Reid
//Date: Mar 21, 2k3
//uses class ClassWithProblemConstructor

public class TargetInvocationWhileConstructorInstantiationTest extends ExceptionTest {

    public TargetInvocationWhileConstructorInstantiationTest() {
        setDescription("This tests Target Invocation While Constructor Instantiation (TL-ERROR 168)");
    }
    RelationalDescriptor descriptor;
    InstantiationPolicy policy;

    protected void setup() {
        descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(ClassWithProblemConstructor.class);
        descriptor.addTableName("EMPLOYEE");
        policy = descriptor.getInstantiationPolicy();
        policy.setMethodName(null);

        expectedException = DescriptorException.targetInvocationWhileConstructorInstantiation(descriptor, new Exception());
    }

    public void test() {
        try {
            policy.buildNewInstance();
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

}
