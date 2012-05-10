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
package org.eclipse.persistence.testing.tests.unitofwork;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod;


/**
 * This test checks on a previous problem TopLink had with merging clones and
 * serialization.  If an existing object was serialized and deserialized and another
 * reference added to that object.  Then the object was serialized and deserialized
 * and deepMergeClone'd into a unit of work and committed.
 * When the origional object was then serialized and deserialized and modified, then serialized
 * and deserialized and deepMergeClone'd with another unit of work and committed the changes are
 * not saved
 */
public class DeepMergeCloneSerializedObjectReferenceChangesTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public Employee original;
    public Employee mergedClone;
    public Employee deserialEmp;

    //stuff changed
    public Address firstAddress;
    public EmploymentPeriod originalEmployment;

    public DeepMergeCloneSerializedObjectReferenceChangesTest() {
        setDescription("This test verifies that deepMergeClone works after a serialized transaction");
    }

    public void reset() {
        if (getAbstractSession().isInTransaction()) {
            getAbstractSession().rollbackTransaction();
            getSession().getIdentityMapAccessor().initializeIdentityMaps();
        }
    }

    public void setup() {
        if (getSession() instanceof org.eclipse.persistence.sessions.remote.RemoteSession) {
            throw new TestWarningException("This test cannot be run through the remote.");
        }
        getAbstractSession().beginTransaction();
    }

    /*
     * This test creates an object and registers it with a unit of work.  It then serializes that
     * object and deserializes it.  Adds an object onto the origional then performs serialization
     * sequence again.  Then deepMergeClone is attempted and the results are compared to verify that
     * the merge worked.
     */

    public void test() {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream stream = new ObjectOutputStream(byteStream);

            //create the phoneNumber object
            Employee empClone;
            Session session = getSession();
            org.eclipse.persistence.sessions.UnitOfWork uow = session.acquireUnitOfWork();
            this.original = 
                    (Employee)session.readObject(Employee.class, new org.eclipse.persistence.expressions.ExpressionBuilder().get("firstName").equal("Bob"));
            //force instantiations of value holders before serialization	
            this.firstAddress = this.original.getAddress();
            this.originalEmployment = new EmploymentPeriod();
            this.originalEmployment.setStartDate(this.original.getPeriod().getStartDate());
            this.originalEmployment.setEndDate(this.original.getPeriod().getEndDate());

            //serialize object by writing to a stream
            stream.writeObject(this.original);
            stream.flush();
            byte[] arr = byteStream.toByteArray();
            ByteArrayInputStream inByteStream = new ByteArrayInputStream(arr);
            ObjectInputStream inObjStream = new ObjectInputStream(inByteStream);

            //deserialize the object
            try {
                this.deserialEmp = (Employee)inObjStream.readObject();
            } catch (ClassNotFoundException e) {
                throw new TestErrorException("Could not deserialize object " + e.toString());
            }

            Address newAddress = new Address();
            newAddress.city = "CITY";
            newAddress.province = "PROV";
            newAddress.postalCode = "K9K0K0";
            newAddress.street = "STREET";
            newAddress.country = "COUNTRY";
            this.deserialEmp.setAddress(newAddress);

            EmploymentPeriod employment = new EmploymentPeriod();
            employment.setStartDate(new java.sql.Date(194560000L));
            employment.setEndDate(new java.sql.Date(1999000000L));
            this.deserialEmp.setPeriod(employment);

            byteStream = new ByteArrayOutputStream();
            stream = new ObjectOutputStream(byteStream);
            //send the ammended object back through the serialization process
            stream.writeObject(deserialEmp);
            stream.flush();
            arr = byteStream.toByteArray();
            inByteStream = new ByteArrayInputStream(arr);
            inObjStream = new ObjectInputStream(inByteStream);
            try {
                deserialEmp = (Employee)inObjStream.readObject();
            } catch (ClassNotFoundException e) {
                throw new TestErrorException("Could not deserialize object " + e.toString());
            }

            //merge the ammended clone with the unit of work
            empClone = (Employee)uow.deepMergeClone(deserialEmp);
            uow.commit();
        } catch (IOException e) {
            throw new TestErrorException("Error running Test " + e.toString());
        }
    }

    /*
     * Checks to see that the names of the updated version and the origional are the same
     */

    public void verify() {
        Employee cachedEmp = (Employee)getSession().readObject(this.original);
        if (cachedEmp.getPeriod().getEndDate().equals(this.originalEmployment.getEndDate())) {
            throw new TestErrorException("Failed to update the aggregate");
        }
        if (cachedEmp.getPeriod().getStartDate().equals(this.originalEmployment.getStartDate())) {
            throw new TestErrorException("Failed to update the aggregate");
        }

        if (!cachedEmp.getPeriod().getEndDate().equals(new java.sql.Date(1999000000L))) {
            throw new TestErrorException("Failed to update the aggregate with new values");
        }
        if (!cachedEmp.getPeriod().getStartDate().equals(new java.sql.Date(194560000L))) {
            throw new TestErrorException("Failed to update the aggregate with new values");
        }

        if (getAbstractSession().compareObjects(this.firstAddress, cachedEmp.getAddress())) {
            throw new TestErrorException("Failed to update address correctly");
        }
    }
}
