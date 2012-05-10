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
package org.eclipse.persistence.testing.tests.mapping;

import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.framework.DeleteObjectTest;
import org.eclipse.persistence.testing.models.mapping.*;
import org.eclipse.persistence.sessions.*;

/**
 * <P>
 * <B>Purpose</B>: This test tests deleting an object with public parts, and ensures that
 * the public parts are not deleted as well.<P>
 *
 * <B>Motivation</B>: If an object has several public parts (in this example, an Employee manages
 * several other Employees), the public parts should not be deleted when the owner is deleted (if
 * the manager is fired and deleted from the database, all the employees he/she managed should not
 * be deleted).<P>
 *
 * <B>Design</B>: In this model, each <CODE>Employee</CODE> has a <CODE>manager</CODE>, and has
 * a collection of <CODE>managedEmployees</CODE>.  In the test, an Employee is read in, and his
 * <CODE>managedEmployee</CODE> collection is saved.  The Manger is then deleted, and the test
 * then verifies that none of the Employees in <CODE>managedEmployee</CODE> was deleted.<P>
 *
 * <B>Responsibilities</B>: Ensure that public parts are not deleted with their parent.<P>
 *
 * <B>Features Used</B>:
 * <UL>
 *     <LI>Public 1:M mapping
 * </UL>
 *
 * <B>Paths Covered</B>: The following conversions are made:
 * <UL>
 *        <LI><CODE>Employee</CODE> with multiple <CODE>managedEmployees</CODE> is deleted from the
 *        database.
 * </UL>
 *
 * @author Rick Barkhouse
 */
public class Public1MTest extends DeleteObjectTest {
    Employee employeeBeingDeleted;
    Vector managedEmployees;
    Vector shipments;
    Employee manager;
    public UnitOfWork unitOfWork;

    public Public1MTest(Object originalObject) {
        super(originalObject);
    }

    public void reset() {
        super.reset();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    protected void setup() {
        super.setup();

        unitOfWork = getSession().acquireUnitOfWork();
        employeeBeingDeleted = (Employee)(unitOfWork.registerObject(originalObject));
        managedEmployees = employeeBeingDeleted.getManagedEmployees();

        shipments = employeeBeingDeleted.getShipments();

        manager = employeeBeingDeleted.getManager();

        if (manager != null) {
            manager.removeManagedEmployee(employeeBeingDeleted);
        }

        Enumeration employeeEnum = managedEmployees.elements();
        while (employeeEnum.hasMoreElements()) {
            ((Employee)employeeEnum.nextElement()).setManager(null);
        }

        Enumeration shipmentEnum = shipments.elements();
        while (shipmentEnum.hasMoreElements()) {
            ((Shipment)shipmentEnum.nextElement()).removeEmployee(employeeBeingDeleted);
        }
    }

    protected void test() {
        unitOfWork.deleteObject(employeeBeingDeleted);
        unitOfWork.commit();
    }

    protected void verify() {
        // Test to see if any managed employees were deleted
        Enumeration enumtr = managedEmployees.elements();
        while (enumtr.hasMoreElements()) {
            if (verifyDelete(enumtr.nextElement())) {
                throw new TestErrorException("Public parts were deleted along with " + employeeBeingDeleted);
            }
        }
    }
}
