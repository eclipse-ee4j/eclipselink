/* Copyright (c) 2005, Oracle. All rights reserved.  */
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
        uowcs.getObjectChanges().remove(Email.class.getName());
        try {
            ((org.eclipse.persistence.internal.sessions.UnitOfWorkImpl)uow).commitRootUnitOfWorkWithPreBuiltChangeSet(uowcs);
        } catch (DatabaseException ex) {
            throw new TestErrorException("Duplicate Insert with Variable One To One Private Owned see bug 4378539");
        }
    }
}
