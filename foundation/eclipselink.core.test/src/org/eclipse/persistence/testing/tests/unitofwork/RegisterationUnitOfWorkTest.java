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

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.WriteObjectTest;


/**
 * <p>
 * <b>Purpose</b>: This test checks if UOW merge is perfromed properly.
 * <p>
 * <b>Motivation </b>: This test was written to fix a bug. While in a unit of work, a new
 * object that refrenced clones, after the merge still refrenced clones.
 * <p>
 * <b>Design</b>: implement an equals method for objects that always returns true, and in another object
 * implement an equals that always returns false. Now check the four paths mentioned below by
 * registering the two objects into the UOW, compare the clones returned. If the two objects are
 * different (regardless of what they think) then different clones must be returned.
 *
 * <p>
 * <b>Responsibilities</b>: Verify that the registration of the objects does not depend on the equals method
 * <p>
 * <b>Features Used</b>: Unit Of Work, Registering feature
 * <p>
 * <b>Paths Covered</b>: Within the unit of work, different parts of the employee were set with clones:
 * <ul>
 * <li><i> Think they are equal but are unequal, clones are different
 * <li> Think they are equal and are equal, clones are identical
 * <li> Think they are unequal but are equal, clones are identical
 * <li> Think they are unequal and are unequal, clones are different </i>
 */
public class RegisterationUnitOfWorkTest extends WriteObjectTest {
    public Object unitOfWorkWorkingCopy;
    public UnitOfWork unitOfWork;

    /**
     * RegisterationUnitOfWorkTests constructor comment.
     */
    public RegisterationUnitOfWorkTest() {
        super();
    }

    /**
     * RegisterationUnitOfWorkTests constructor comment.
     */
    public RegisterationUnitOfWorkTest(Object originalObject) {
        super(originalObject);
        setDescription("The test registers equal and unequal objects to the database and sees if the" + 
                       "' registeration works properly by checking if different clones are returned for similar objects");
    }

    public void setup() {
        super.setup();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        this.unitOfWork = getSession().acquireUnitOfWork();
    }

    protected void test() {
        testRegisteringEqualObjects();
        testRegisteringUnequalObjects();
        this.unitOfWork.commit();
    }

    protected void testRegisteringEqualObjects() {
        /* This test will try to register two equal objects
        In the first case the two objects are equal and the method returns true
        In the second case the two objects are equal and the equals method returns false
        We should get exactly the same clone when we register both */
        Person person = Person.example1();
        Person personClone1 = (Person)this.unitOfWork.registerObject(person);
        Person personClone2 = (Person)this.unitOfWork.registerObject(person);
        if (!(personClone1 == personClone2)) {
            throw new TestErrorException("Registration fault: Clones are different for equal objects");
        }
        MailAddress address = MailAddress.example1();
        MailAddress addressClone1 = (MailAddress)this.unitOfWork.registerObject(address);
        MailAddress addressClone2 = (MailAddress)this.unitOfWork.registerObject(address);
        if (!(addressClone1 == addressClone2)) {
            throw new TestErrorException("Registration fault: Clones are different for equal objects");
        }
    }

    protected void testRegisteringUnequalObjects() {
        /* This test will register two unequal objects
            In the first case the two objects are unequal and think that they are equal
            In the second case the two objects are unequal and know that they are unequal
            We should get exactly two different clones each time */
        Person person1 = Person.example1();
        Person person2 = Person.example2();

        Person personClone1 = (Person)this.unitOfWork.registerObject(person1);
        Person personClone2 = (Person)this.unitOfWork.registerObject(person2);
        if (personClone1 == personClone2) {
            throw new TestErrorException("Registration fault: Same clone returned for unequal objects");
        }
        MailAddress address1 = MailAddress.example1();
        MailAddress address2 = MailAddress.example2();
        MailAddress addressClone1 = (MailAddress)this.unitOfWork.registerObject(address1);
        MailAddress addressClone2 = (MailAddress)this.unitOfWork.registerObject(address2);
        if (addressClone1 == addressClone2) {
            throw new TestErrorException("Registration fault: Same clone returned for unequal objects");
        }
    }
}
