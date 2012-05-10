/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
