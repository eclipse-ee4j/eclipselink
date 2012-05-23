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
package org.eclipse.persistence.testing.tests.aggregate;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.aggregate.nested.Guardian;
import org.eclipse.persistence.testing.models.aggregate.nested.MailingAddress;
import org.eclipse.persistence.testing.models.aggregate.nested.Student;

public class NestedAggregateTestCase extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {

    public void reset() {
        rollbackTransaction();
    }

    protected void setup() {
        beginTransaction();

        // CREATE A MAILING ADDRESS
        MailingAddress mailingAddress = new MailingAddress();
        mailingAddress.setStreet("123 Any Street");
        mailingAddress.setCity("Willow Beach");
        mailingAddress.setProvince("Ontario");
        mailingAddress.setCountry("Canada");
        mailingAddress.setPostalCode("A1B 2C3");

        // CREATE A GUARDIAN
        Guardian guardian = new Guardian();
        guardian.setFirstName("John");
        guardian.setLastName("Doe");
        guardian.setMailingAddress(mailingAddress);

        // CREATE A STUDENT
        Student student = new Student();
        student.setFirstName("Jane");
        student.setLastName("Doe");
        student.setGuardian(guardian);

        // ADD THE STUDENT TO THE DATABASE
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(student);
        uow.commit();

    }

    /**
     * This method was created in VisualAge.
     */
    public void test() {
        Student student = (Student)getSession().readObject(Student.class);

        // Set the Guardian to null
        student.setGuardian(null);

        // Refresh the Student object
        getSession().refreshObject(student);
    }

    /**
     * This method was created in VisualAge.
     */
    public void verify() {
    }
}
