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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.sessions.*;

/**
 *    Test the object copying feature.
 */
public class ObjectCopyingTest extends TransactionalTestCase {
    public ObjectCopyingTest() {
        setDescription("Test the object copying feature.");
    }

    public void test() {
        Employee original = (Employee)getSession().readObject(Employee.class);
        CopyGroup group = new CopyGroup();
        group.setShouldResetPrimaryKey(true);
        Employee copy = (Employee)getSession().copy(original, group);
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(copy);
        uow.commit();

        copy = (Employee)getSession().readObject(copy);
        if ((original == copy) || (original.getAddress() == copy.getAddress())) {
            throw new TestErrorException("Copies are not copies.");
        }
        if ((!original.getFirstName().equals(copy.getFirstName())) || (!original.getAddress().getCity().equals(copy.getAddress().getCity()))) {
            throw new TestErrorException("Copies are not the same.");
        }
    }
}
