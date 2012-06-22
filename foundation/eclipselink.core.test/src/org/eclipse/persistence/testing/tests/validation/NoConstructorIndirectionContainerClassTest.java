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

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.indirection.ContainerIndirectionPolicy;


//Created by Ian Reid
//Date: Mar 25, 2k3

public class NoConstructorIndirectionContainerClassTest extends ExceptionTest {
    public NoConstructorIndirectionContainerClassTest() {
        setDescription("This tests No Constructor Indirection Container Class (TL-ERROR 167)");
    }
    ContainerIndirectionPolicy policy;

    protected void setup() {
        policy = new ContainerIndirectionPolicy();
        //  policy.setContainerClass(NoConstructorIndirectionContainerClassTest.class);//no problems
        policy.setContainerClass(ClassWithoutConstructor.class); //problems

        expectedException = DescriptorException.noConstructorIndirectionContainerClass(policy, ClassWithoutConstructor.class);
    }

    public void test() {
        try {
            policy.initialize();
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }
}
