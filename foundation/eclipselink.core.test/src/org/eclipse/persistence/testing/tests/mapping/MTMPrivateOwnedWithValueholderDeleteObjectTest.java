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
package org.eclipse.persistence.testing.tests.mapping;

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.mapping.Employee;
import org.eclipse.persistence.testing.models.mapping.Phone;
import org.eclipse.persistence.testing.models.mapping.Shipment;

public class MTMPrivateOwnedWithValueholderDeleteObjectTest extends org.eclipse.persistence.testing.framework.DeleteObjectTest {
    Vector phonesBeforeDelete;

    /**
     * ManyToManyPrivateOwnedWithValueholder constructor comment.
     */
    public MTMPrivateOwnedWithValueholderDeleteObjectTest() {
        super();
    }

    /**
     * ManyToManyPrivateOwnedWithValueholder constructor comment.
     * @param originalObject java.lang.Object
     */
    public MTMPrivateOwnedWithValueholderDeleteObjectTest(Object originalObject) {
        super(originalObject);
    }

    protected void setup() {
        beginTransaction();
        originalObject = getSession().readObject(Employee.class);
        phonesBeforeDelete = ((Employee)originalObject).getPhoneNumbers();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

    }

    protected void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        originalObject = uow.readObject(Employee.class);
        Employee emp = (Employee)originalObject;
        uow.deleteObject(emp);
        for (Enumeration enumtr = emp.getShipments().elements(); enumtr.hasMoreElements();) {
            Shipment ship = (Shipment)enumtr.nextElement();
            ship.employees.removeElement(emp);
        }
        for (Enumeration enumtr = emp.getManagedEmployees().elements(); enumtr.hasMoreElements();) {
            Employee emp2 = (Employee)enumtr.nextElement();
            emp2.manager = null;
        }
        uow.commit();
    }

    protected void verify() {
        for (Enumeration enumBeforeDelete = phonesBeforeDelete.elements();
                 enumBeforeDelete.hasMoreElements();) {
            Phone phoneBeforeDelete = (Phone)enumBeforeDelete.nextElement();
            if (!verifyDelete(phoneBeforeDelete)) {
                throw new TestErrorException("The entry in the relation table is deleted before the value holder is triggered");
            }
        }
    }
}
