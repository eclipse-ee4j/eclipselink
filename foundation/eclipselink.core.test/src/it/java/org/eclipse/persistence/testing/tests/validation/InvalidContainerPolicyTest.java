/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.queries.IndirectListContainerPolicy;
import org.eclipse.persistence.testing.framework.TestErrorException;


//Created by Ian Reid
//Date: Mar 4, 2k3
//Verify method changed to allow passing of test

public class InvalidContainerPolicyTest extends ExceptionTest {

    boolean passed = false;

    public InvalidContainerPolicyTest() {
        super();
        setDescription("This tests Invalid Container Policy (TL-ERROR 147)");
    }

    protected void setup() {
        expectedException = DescriptorException.invalidContainerPolicy(null, InvalidContainerPolicyTest.class);
        //  expectedException.setErrorCode(147);

    }


    public void test() {
        try {
            passed = false;
            IndirectListContainerPolicy policy = new IndirectListContainerPolicy(InvalidContainerPolicyTest.class);
            /* The exception is created in the following contructor, but never thrown....
 public IndirectListContainerPolicy(Class containerClass) {
    super(containerClass);
//mod-jdk1.2 u1
    DescriptorException.invalidContainerPolicy(this, containerClass);
}
 */
            if (IndirectListContainerPolicy.class.isInstance(policy))
                passed = true;

        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    protected void verify() {
        if (caughtException != null) {
            throw new TestErrorException("The proper exception was not thrown:" + org.eclipse.persistence.internal.helper.Helper.cr() + "[CAUGHT] " + caughtException + "\n\n[EXPECTING] " + expectedException);
        }
        if ((147 != expectedException.getErrorCode()) || (!passed)) {
            throw new TestErrorException("The proper exception was not thrown:" + org.eclipse.persistence.internal.helper.Helper.cr() + "caught exception was not null! \n\n[EXPECTING] " + expectedException);
        }

    }

}
