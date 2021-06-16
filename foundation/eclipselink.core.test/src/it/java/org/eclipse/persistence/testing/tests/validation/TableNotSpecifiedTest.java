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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.tests.validation.ExceptionTest;


//Created by Ian Reid
//Date: Feb 6, 2k3

public class TableNotSpecifiedTest extends ExceptionTest {
    public TableNotSpecifiedTest() {
        super();
        setDescription("This tests Table Not Specified (TL-ERROR 94) " + "");
    }

    protected void setup() {
        expectedException = DescriptorException.tableNotSpecified(null);
        orgDescriptor = ((DatabaseSession)getSession()).getDescriptor(org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod.class);
        orgIntegrityChecker = getSession().getIntegrityChecker();
    }
    ClassDescriptor orgDescriptor;
    IntegrityChecker orgIntegrityChecker;

    public void reset() {
        ((DatabaseSession)getSession()).getDescriptors().remove(org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod.class);
        if (orgDescriptor != null)
            ((DatabaseSession)getSession()).addDescriptor(orgDescriptor);
        if (orgIntegrityChecker != null)
            getSession().setIntegrityChecker(orgIntegrityChecker);
    }

    public void test() {
        try {
            getSession().setIntegrityChecker(new IntegrityChecker());
            getSession().getIntegrityChecker().dontCatchExceptions();
            buildProject((DatabaseSession)getSession());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    public void buildProject(DatabaseSession session) {
        session.addDescriptor(buildEmploymentPeriodDescriptor());
    }

    public RelationalDescriptor buildEmploymentPeriodDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod.class);

        //if the following is missing then the correct error will occure.
        //    descriptor.addTableName("EMPLOYEE");

        //OR    descriptor.descriptorIsAggregate();

        // Descriptor properties.

        // Query manager.

        // Event manager.

        // Mappings.

        return descriptor;
    }
}
