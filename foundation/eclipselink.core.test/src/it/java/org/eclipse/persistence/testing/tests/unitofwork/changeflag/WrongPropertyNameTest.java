/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.unitofwork.changeflag;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.descriptors.changetracking.AttributeChangeListener;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.tests.validation.ExceptionTest;

import java.beans.PropertyChangeEvent;


public class WrongPropertyNameTest extends ExceptionTest {

    ClassDescriptor des;
    UnitOfWork uow;
    PropertyChangeEvent even;

    public WrongPropertyNameTest() {
        setDescription("Tests if wrongPropertyNameInChangeEvent exception would be thrown");
    }

    @Override
    protected void setup() {
        expectedException =
                org.eclipse.persistence.exceptions.ValidationException.wrongPropertyNameInChangeEvent(null, null);

        des = getSession().getDescriptor(Employee.class);
        uow = getSession().acquireUnitOfWork();
        even = new PropertyChangeEvent(new Employee(), "wrongName", "oldName", "newName");
    }

    @Override
    protected void test() {
        try {
            new AttributeChangeListener(des, (UnitOfWorkImpl)uow, new Employee()).internalPropertyChange(even);
        } catch (EclipseLinkException ex) {
            caughtException = ex;
        }
    }
}
