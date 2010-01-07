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

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.indirection.TransparentIndirectionPolicy;
import org.eclipse.persistence.mappings.OneToManyMapping;


//Created by Ian Reid
//Date: Mar 5, 2k3

public class InvalidContainerPolicyWithTransparentIndirectionTest extends ExceptionTest {

    IntegrityChecker orgIntegrityChecker;

    public InvalidContainerPolicyWithTransparentIndirectionTest() {
        super();
        setDescription("This tests Invalid Container Policy With Transparent Indirection (TL-ERROR 148)");
    }

    protected void setup() {
        expectedException = DescriptorException.invalidContainerPolicyWithTransparentIndirection(null, null);
        orgIntegrityChecker = getSession().getIntegrityChecker();
        getSession().setIntegrityChecker(new IntegrityChecker());
        getSession().getIntegrityChecker().dontCatchExceptions();
    }

    public void reset() {
        if (orgIntegrityChecker != null)
            getSession().setIntegrityChecker(orgIntegrityChecker);
    }

    public void test() {
        TransparentIndirectionPolicy policy = new TransparentIndirectionPolicy();
        OneToManyMapping mapping = new OneToManyMapping();
        mapping.setAttributeName("manager");
        mapping.setReferenceClass(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        mapping.addTargetForeignKeyFieldName("EMPLOYEE.MANAGER_ID", "EMPLOYEE.EMP_ID");
        policy.setMapping(mapping);
        try {
            policy.validateContainerPolicy(getSession().getIntegrityChecker());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }
}
