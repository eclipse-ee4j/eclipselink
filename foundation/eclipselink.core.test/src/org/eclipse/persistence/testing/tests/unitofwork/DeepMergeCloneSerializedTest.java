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

import java.sql.Time;

import java.util.Date;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.IsolatedClientSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;


/**
 * This test checks on a previous problem TopLink had with merging clones and
 * serialization.  If an existing object was serialized and deserialized and another
 * reference added to that object.  Then the object was serialized and deserialized
 * and deepMergeClone'd into a unit of work and committed.
 * When the origional object was then serialized and deserialized and modified, then serialized
 * and deserialized and deepMergeClone'd with another unit of work and committed the changes are
 * not saved
 */
public class DeepMergeCloneSerializedTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public Employee origional;
    public Employee mergedClone;
    public Employee empObject;

    //stuff changed
    public String gender;
    public Time endTime;
    public PhoneNumber removedPhone;
    public PhoneNumber addedPhone;
    public Date endDate;

    public DeepMergeCloneSerializedTest() {
        setDescription("This test verifies that deepMergeClone works after a serialized transaction");
    }

    public DeepMergeCloneSerializedTest(Employee employee) {
        this.empObject = employee;
        setDescription("This test verifies that deepMergeClone works after a serialized transaction");
    }

    public void reset() {
        if (getAbstractSession().isInTransaction()) {
            getAbstractSession().rollbackTransaction();
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
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
            this.empObject = 
                    (Employee)session.readObject(Employee.class, new org.eclipse.persistence.expressions.ExpressionBuilder().get("firstName").equal("Bob"));
            ClassDescriptor descriptor = session.getDescriptor(this.empObject);
            if (descriptor.isProtectedIsolation() && descriptor.shouldIsolateProtectedObjectsInUnitOfWork() && session instanceof IsolatedClientSession){
                // this will have read a version of the protected Entity into the Isolated Cache even though the test wants to isolated to UOW
                //replace with actual shared cache version
                this.empObject = (Employee) ((AbstractSession)session).getParentIdentityMapSession(descriptor, false, true).getIdentityMapAccessor().getFromIdentityMap(this.empObject);
            }
            //force instantiations of value holders before serialization	
            this.empObject.getPhoneNumbers();
            if (this.empObject.getManager() != null) {
                this.empObject.getManager().getManagedEmployees();
            }
            this.empObject.getResponsibilitiesList();

            //serialize object by writing to a stream
            stream.writeObject(this.empObject);
            stream.flush();
            byte[] arr = byteStream.toByteArray();
            ByteArrayInputStream inByteStream = new ByteArrayInputStream(arr);
            ObjectInputStream inObjStream = new ObjectInputStream(inByteStream);
            Employee deserialEmp;

            //deserialize the object
            try {
                deserialEmp = (Employee)inObjStream.readObject();
            } catch (ClassNotFoundException e) {
                throw new TestErrorException("Could not deserialize object " + e.toString());
            }

            //add a new manager, test 1-m's
            Employee newManager = new org.eclipse.persistence.testing.models.employee.domain.Employee();
            if (deserialEmp.getManager() != null) {
                deserialEmp.getManager().removeManagedEmployee(deserialEmp);
                this.removedPhone = (PhoneNumber)deserialEmp.getPhoneNumbers().firstElement();
                deserialEmp.getPhoneNumbers().removeElement(deserialEmp.getPhoneNumbers().firstElement());
            }
            newManager.addManagedEmployee(deserialEmp);

            //add the PhoneNumber object to the origional clone, test 1-1
            PhoneNumber phone = new org.eclipse.persistence.testing.models.employee.domain.PhoneNumber();
            phone.setNumber("5555897");
            phone.setType("Fax");
            phone.setOwner(deserialEmp);
            deserialEmp.addPhoneNumber(phone);

            this.addedPhone = phone;

            deserialEmp.setLastName("Willford");
            this.gender = deserialEmp.getGender();
            if (deserialEmp.getGender().equals("Female")) {
                deserialEmp.setMale();
            } else {
                deserialEmp.setFemale();
            }
            this.endDate = deserialEmp.getPeriod().getEndDate();
            deserialEmp.getPeriod().setEndDate(new java.sql.Date(System.currentTimeMillis() + 300000L));

            this.endTime = deserialEmp.getEndTime();
            deserialEmp.setEndTime(Helper.timeFromHourMinuteSecond(15, 2, 3));

            deserialEmp.addResponsibility("A Very New Respons");

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
            uow = session.acquireUnitOfWork();

            // do the serialization for the second time
            byteStream = new ByteArrayOutputStream();
            stream = new ObjectOutputStream(byteStream);
            stream.writeObject(this.empObject);
            stream.flush();
            arr = byteStream.toByteArray();
            inByteStream = new ByteArrayInputStream(arr);
            inObjStream = new ObjectInputStream(inByteStream);

            //attempt to deserialize the object
            try {
                deserialEmp = (Employee)inObjStream.readObject();
            } catch (ClassNotFoundException e) {
                throw new TestErrorException("Could not deserialize object " + e.toString());
            }
            deserialEmp.setFirstName("Danny");
            this.origional = deserialEmp;
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
            deserialEmp = (Employee)uow.deepMergeClone(deserialEmp);
            uow.commit();
            this.mergedClone = deserialEmp;
        } catch (IOException e) {
            throw new TestErrorException("Error running Test " + e.toString());
        }
    }

    /**
     * Checks to see that the names of the updated version and the origional are the same
     */
    public void verify() {
        if (this.mergedClone.getFirstName().equals(this.origional.getFirstName())) {
        } else {
            throw new TestErrorException("Clone did not merge properly");
        }
        Employee cachedEmp = (Employee)getSession().readObject(this.mergedClone);
        if (cachedEmp.getPeriod().getEndDate().equals(this.endDate)) {
            throw new TestErrorException("Failed to update the aggregate");
        }
        if (cachedEmp.getEndTime().equals(this.endTime)) {
            throw new TestErrorException("Failed to update the Transformation mapping");
        }
        if (cachedEmp.getGender().equals(this.gender)) {
            throw new TestErrorException("Failed to update the TypeConversionMapping");
        }
        if (!cachedEmp.getPhoneNumbers().contains(getSession().readObject(addedPhone))) {
            throw new TestErrorException("Failed to update one to many addition");
        }
        if (!cachedEmp.getResponsibilitiesList().contains("A Very New Respons")) {
            throw new TestErrorException("Failed to update Direct Collection mapping");
        }
        if (!cachedEmp.getLastName().equals("Willford")) {
            throw new TestErrorException("Failed to update Direct To Field");
        }
    }
}
