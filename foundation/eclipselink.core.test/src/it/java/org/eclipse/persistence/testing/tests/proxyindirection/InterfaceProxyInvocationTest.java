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
package org.eclipse.persistence.testing.tests.proxyindirection;

import java.util.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Addresses CR2718
 * Invoking a method on an object which is null inside a Proxy object throws
 * a validation exception instead of causing a nullpointerexception.
 */
public class InterfaceProxyInvocationTest extends AutoVerifyTestCase {
    protected Cubicle cubicle;
    protected Employee employee;

    public InterfaceProxyInvocationTest() {
        super();
        setDescription("Tests an attempt to invoke a method on a value-held proxy-indirection enabled object that is null");
    }

    public void setup() {
        // we need to delete all employees to remove all of the
        // rows from the employee table - this is the crux of this test
        getAbstractSession().beginTransaction();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Vector theCondemned = uow.readAllObjects(Employee.class);
        uow.deleteAllObjects(theCondemned);
        uow.commit();

        // read the cubicle (there's now no employee)
        cubicle = (Cubicle)getSession().readObject(Cubicle.class);
    }

    public void test() {
        employee = cubicle.getEmployee();
    }

    public void verify() {
        boolean exceptionThrown = false;

        // This isn't a real employee, it's a Proxy object
        if (employee == null) {
            throw new TestErrorException("Proxy for Employee should not be null");
        }
        try {
            // invoke method on proxy object
            String firstName = employee.getFirstName();
        } catch (Exception n) {
            exceptionThrown = true;
            if (!(n instanceof EclipseLinkException)) {
                throw new TestErrorException("Unhandled non-TopLink Exception occurred upon invocation of getName() on employee");
            } else {
                EclipseLinkException w = (EclipseLinkException)n;

                // test error code here
                if (w.getErrorCode() != ValidationException.NULL_UNDERLYING_VALUEHOLDER_VALUE) {
                    throw new TestErrorException("An unexpected exception occurred", w);
                }
            }
        } finally {
            if (exceptionThrown == false) {
                throw new TestErrorException("No exception was thrown within the test case. Expected an exception.");
            }
        }
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
    }
}
