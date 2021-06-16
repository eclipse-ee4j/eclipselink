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
package org.eclipse.persistence.testing.tests.interfaces;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.interfaces.*;

public class UpdateObjectTest extends TransactionalTestCase {
    public Employee orig;

    public void test() {
        Session session = getSession();
        UnitOfWork uow = session.acquireUnitOfWork();
        this.orig = Employee.example1();
        Employee e = (Employee)uow.registerObject(this.orig);
        Phone ph = (Phone)uow.readObject(Phone.class);
        ph.setNumber("5555567");
        e.setContact(ph);
        try {
            uow.commit();
        } catch (org.eclipse.persistence.exceptions.DatabaseException ex) {
            throw new TestErrorException("Inserted instead of updating" + org.eclipse.persistence.internal.helper.Helper.cr() +
                                         ex.toString());
        }
    }
}
