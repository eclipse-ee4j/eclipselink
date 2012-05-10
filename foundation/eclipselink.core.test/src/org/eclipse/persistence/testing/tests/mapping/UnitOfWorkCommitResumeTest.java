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
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.mapping.Phone;
import org.eclipse.persistence.testing.models.mapping.Computer;
import org.eclipse.persistence.testing.models.mapping.Employee;
import org.eclipse.persistence.testing.models.mapping.JobDescription;

/**
 * <p>
 * <b>Purpose</b>: This test checks to see if the Unit of Work commitAndResume feature functions correctly
 * within the context of Complex mappings.
 *
 * <p>
 * <b>Motivation </b>: This test was written to test Unit-Of-Work commitAndResume feature.
 * <p>
 * <b>Design</b>: The Complex Mapping model is used. An Employee is registered into the UOW,
 *                     and then various member fields (which represent various different types
 *                    of mappings) are changed and commited (using commitAndResume()) to the database,
 *                    changed again in a different way, committed again, then read back and
 *                    compared.
 * <p>
 * <b>Responsibilities</b>: Check if the unit of work commitAndResume functionality workds properly with complex mappings
 *
 * <p>
 *     <b>Features Used</b>: Complex Mappings, Unit Of Work, CommitAndResume
 *
 * <p>
 * <b>Paths Covered</b>:
 *        <ul>
 *        <li><i>Adding an object</i> - creating an object within a UOW</li>
 *        <li><i>Modifying an object</i> - modify contents of an existing object within a UOW</li>
 *        <li><i>Removing an object</i> - deletion of an object</li>
 *        </ul>
 * <p>
 * For each of the above paths, the work is done w.r.t. the 1:1 mappings, 1:M mappings, the
 * Transformational mappings, etc.
 * */
public class UnitOfWorkCommitResumeTest extends WriteObjectTest {
    public Object unitOfWorkWorkingCopy;
    public UnitOfWork unitOfWork;

    /**
     * UnitOfWorkCommitResume constructor comment.
     */
    public UnitOfWorkCommitResumeTest() {
        super();
    }

    /**
     * UnitOfWorkCommitResume constructor comment.
     * @param originalObject java.lang.Object
     */
    public UnitOfWorkCommitResumeTest(Object originalObject) {
        super(originalObject);
    }

    /** Make changes to object so that each type of mapping is tested.
    */
    protected void changeUnitOfWorkWorkingCopy() {
        Employee employee = (Employee)this.unitOfWorkWorkingCopy;

        // SerializedObjectMapping - Employee.jobDescription
        employee.setJobDescription(JobDescription.example3());

        // TransformationMapping - Employee.dateAndTimeOfBirth
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.set(1978, 11, 31, 23, 59, 59);
        aCalendar.set(Calendar.MILLISECOND, 0);
        employee.dateAndTimeOfBirth = aCalendar.getTime();

        // TypeConversionMapping - Employee.joiningDate
        aCalendar.set(1995, 3, 14, 9, 0, 23);
        employee.joiningDate = aCalendar.getTime();

        // DirectCollectionMapping - Employee.policies
        Vector thePolicies = employee.getPolicies();
        thePolicies.removeElementAt(0);
        thePolicies.addElement("a silly policy");

        // OneToOneMapping - Employee.computer

        /* This change does not work because Employee uses firstName and lastName
        to access Computer which does not guarantee uniqueness.
        */

        //employee.computer = Computer.example1(employee) ;
        // ObjectTypeMapping - Computer.isMacintosh
        Computer aComputer = (Computer)employee.getComputer();
        if (aComputer.isMacintosh.booleanValue()) {
            aComputer.notMacintosh();
        } else {
            aComputer.isMacintosh();
        }

        // ManyToManyMapping - Employee.phoneNumbers
        Vector thePhoneNumbers = employee.getPhoneNumbers();
        thePhoneNumbers.removeElementAt(0);
        thePhoneNumbers.addElement(Phone.example10());
        thePhoneNumbers.addElement(Phone.example11());
        thePhoneNumbers.addElement(Phone.example12());
        thePhoneNumbers.addElement(Phone.example13());
        thePhoneNumbers.addElement(Phone.example14());
        thePhoneNumbers.removeElementAt(0);

        // OneToManyMapping - Employee.managedEmployees

        /*    Employee emp3 = employee.example3() ;
            unitOfWork.registerObject(emp3);
            employee.addManagedEmployee(emp3);
            */

        //	employee.addManagedEmployee(employee.example7());	
    }

    /** Make different changes to the object so that each type of mapping is tested in a different way.
    */
    protected void changeUnitOfWorkWorkingCopyAgain() {
        Employee employee = (Employee)this.unitOfWorkWorkingCopy;

        // SerializedObjectMapping - Employee.jobDescription
        JobDescription aJobDescription = employee.getJobDescription();
        aJobDescription.setDescription("Serve customers ice cream");
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.set(Calendar.MILLISECOND, 0);
        aCalendar.set(2000, 1, 28, 15, 45, 0);
        aJobDescription.setEndDate(aCalendar.getTime());
        aJobDescription.setSalary(10000.0f);
        aJobDescription.setTitle("Ice cream stand attendant");

        // TransformationMapping - Employee.dateAndTimeOfBirth
        aCalendar.set(1977, 11, 31, 23, 59, 59);
        employee.dateAndTimeOfBirth = aCalendar.getTime();

        // TypeConversionMapping - Employee.joiningDate
        aCalendar.set(1994, 9, 19, 9, 0, 23);
        employee.joiningDate = aCalendar.getTime();

        // DirectCollectionMapping - Employee.policies
        employee.setPolicies(new Vector());

        // OneToOneMapping - Employee.computer
        //employee.computer = Computer.example5(employee) ;
        // ManyToManyMapping - Employee.phoneNumbers
        Vector thePhoneNumbers = employee.getPhoneNumbers();
        thePhoneNumbers.addElement(Phone.example1());
        thePhoneNumbers.addElement(Phone.example2());
        thePhoneNumbers.addElement(Phone.example3());
        thePhoneNumbers.addElement(Phone.example4());
        thePhoneNumbers.removeElementAt(0);
        thePhoneNumbers.removeElementAt(0);
        thePhoneNumbers.removeElementAt(0);
        thePhoneNumbers.removeElementAt(0);

        // OneToManyMapping - Employee.managedEmployees
        //employee.managedEmployees = new Vector();
    }

    protected void setup() {
        super.setup();

        // Acquire first unit of work
        this.unitOfWork = getSession().acquireUnitOfWork();

        this.unitOfWorkWorkingCopy = this.unitOfWork.registerObject(this.objectToBeWritten);
        changeUnitOfWorkWorkingCopy();
        // Use the original session for comparision
        if (!compareObjects(this.originalObject, this.objectToBeWritten)) {
            throw new TestErrorException("The original object was changed through changing the clone.");
        }
    }

    protected void test() {
        // CommitAndResume, changeAgain, CommitAndResume again.
        this.unitOfWork.commitAndResume();
        changeUnitOfWorkWorkingCopyAgain();
        this.unitOfWork.commitAndResume();
    }

    /**
     * Verify if the objects match completely through allowing the session to use the descriptors.
     * This will compare the objects and all of their privately owned parts.
     */
    protected void verify() {
        try {
            if (!(compareObjects(this.unitOfWorkWorkingCopy, this.objectToBeWritten))) {
                throw new TestErrorException("The object in the unit of work has not been commited properly to its parent");
            }
        } catch (DatabaseException exception) {
            if (getSession().getLogin().getPlatform().isDBase()) {
                throw new TestWarningException("This fails because of some strange bug in the DBase driver. " + exception.getMessage());
            } else {
                throw exception;
            }
        }
        super.verify();
    }
}
