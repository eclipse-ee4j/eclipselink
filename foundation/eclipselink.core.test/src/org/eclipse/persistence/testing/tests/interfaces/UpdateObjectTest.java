/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
