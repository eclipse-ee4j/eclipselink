/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
