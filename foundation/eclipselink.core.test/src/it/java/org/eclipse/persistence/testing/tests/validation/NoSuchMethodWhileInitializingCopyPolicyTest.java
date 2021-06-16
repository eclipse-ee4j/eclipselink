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

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.descriptors.copying.CloneCopyPolicy;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;


/**
 * Code coverage test for clone copy policy exceptions.
 */
public class NoSuchMethodWhileInitializingCopyPolicyTest extends ExceptionTest {
    IntegrityChecker orgIntegrityChecker;

    public NoSuchMethodWhileInitializingCopyPolicyTest() {
        super();
        setDescription("This tests No Such Method While Initializing Copy Policy (TL-ERROR 62)");
    }

    protected void setup() {
        expectedException = DescriptorException.noSuchMethodWhileInitializingCopyPolicy("Dummy_Method", new RelationalDescriptor(), new Exception());
        orgIntegrityChecker = getSession().getIntegrityChecker();
    }

    public void reset() {
        if (orgIntegrityChecker != null) {
            getSession().setIntegrityChecker(orgIntegrityChecker);
        }
    }

    public void test() {
        try {
            //need getIntegrityChecker()
            getSession().setIntegrityChecker(new IntegrityChecker());
            getSession().getIntegrityChecker().dontCatchExceptions();
            RelationalDescriptor descriptor = new RelationalDescriptor();

            //need superclass == null
            descriptor.setJavaClass(Object.class);
            descriptor.setCopyPolicy(new CloneCopyPolicy());
            CloneCopyPolicy policy = (CloneCopyPolicy)descriptor.getCopyPolicy();

            //need not getMethodName() == null
            policy.setMethodName("Dummy_Method"); //this method does not exist in above class
            //need NoSuchMethod thrown from             this.setMethod(Helper.getDeclaredMethod(this.getDescriptor().getJavaClass(), this.getMethodName(), new Class[0]));
            policy.initialize(getSession());

        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }
}
