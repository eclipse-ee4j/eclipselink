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

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;


//Created by Ian Reid
//Date: Mar 21, 2k3

public class NullPointerWhileConstructorInstantiationTest extends ExceptionTest {
    public NullPointerWhileConstructorInstantiationTest() {
        setDescription("This tests Null Pointer While Constructor Instantiation (TL-ERROR 113)");
    }
    InstantiationPolicy policy;

    protected void setup() {
        policy = new org.eclipse.persistence.internal.descriptors.InstantiationPolicy();


        expectedException = DescriptorException.nullPointerWhileConstructorInstantiation(null, new Exception());
    }

    public void test() {
        try {
            policy.buildNewInstance();
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }
}
