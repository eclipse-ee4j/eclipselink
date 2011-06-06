/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import org.eclipse.persistence.internal.indirection.NoIndirectionPolicy;


//Created by Ian Reid
//Date: Mar 5, 2k3

public class InvalidIndirectionPolicyOperationTest extends ExceptionTest {

    String methodName;

    public InvalidIndirectionPolicyOperationTest(String methodName) {
        this.methodName = methodName;
        setDescription("This tests Invalid Indirection Policy Operation (" + methodName + ") (TL-ERROR 152)");
    }

    protected void setup() {
        expectedException = DescriptorException.invalidIndirectionPolicyOperation(new NoIndirectionPolicy(), "Dummy_operation");
    }

    public void test() {
        try {
            if (methodName.equals("ContainerIndirectionPolicy.nullValueFromRow")) {
                ContainerIndirectionPolicy policy = new ContainerIndirectionPolicy();
                policy.nullValueFromRow();
            } else if (methodName.equals("NoIndirectionPolicy.getValueFromRemoteValueHolder")) {
                NoIndirectionPolicy policy = new NoIndirectionPolicy();
                policy.getValueFromRemoteValueHolder(null);
            } else if (methodName.equals("NoIndirectionPolicy.mergeRemoteValueHolder")) {
                NoIndirectionPolicy policy = new NoIndirectionPolicy();
                policy.mergeRemoteValueHolder(null, null, null);
            } else {
                throw new org.eclipse.persistence.testing.framework.TestProblemException("Wrong method name for testing Invalid Indirection Policy Operation");
            }

        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }
}
