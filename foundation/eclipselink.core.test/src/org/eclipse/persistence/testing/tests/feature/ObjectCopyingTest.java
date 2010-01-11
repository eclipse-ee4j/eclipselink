/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
        Employee copy = (Employee)getSession().copyObject(original);
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
