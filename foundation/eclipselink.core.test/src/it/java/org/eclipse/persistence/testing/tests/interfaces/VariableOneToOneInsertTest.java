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

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.interfaces.*;

public class VariableOneToOneInsertTest extends TransactionalTestCase {
    public Company company;

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        this.company = new Company();
        Company c = (Company)uow.registerObject(this.company);
        c.setName("Company One");
        c.setId(new Integer(54));
        Email email = new Email();
        email.setAddress("@Blather.ca");
        email.setHolder(c);
        email.setId(new Integer(45));
        c.setContact(email);
        c.email = email;

        // this is a little magic to ensure we write Company first, it will not enduce
        // any side effects.
        UnitOfWorkChangeSet uowcs = (UnitOfWorkChangeSet)uow.getCurrentChanges();
        uowcs.getObjectChanges().remove(Email.class);
        try {
            ((org.eclipse.persistence.internal.sessions.UnitOfWorkImpl)uow).commitRootUnitOfWorkWithPreBuiltChangeSet(uowcs);
        } catch (DatabaseException ex) {
            throw new TestErrorException("Duplicate Insert with Variable One To One Private Owned see bug 4378539");
        }
    }
}
