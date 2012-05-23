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

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.testing.framework.WriteObjectTest;
import org.eclipse.persistence.testing.models.aggregate.Address;
import org.eclipse.persistence.testing.models.aggregate.AddressDescription;
import org.eclipse.persistence.testing.models.aggregate.Computer;
import org.eclipse.persistence.testing.models.aggregate.Employee;
import org.eclipse.persistence.testing.models.aggregate.Language;
import org.eclipse.persistence.testing.models.aggregate.ProjectDescription;
import org.eclipse.persistence.testing.models.aggregate.Responsibility;

/**
 * <p>
 * <b>Purpose</b>: This test checks to see if the Unit of Work functions with the Aggregate mappings using the commitAndResume functionality.
 *
 *     <p>
 * <b>Motivation </b>: This test was written to test a new feature: the UOW.
 * <p>
 * <b>Design</b>: The Complex Aggregate model is used. An Employee is registered into the UOW, and then
 *                             its different levels of aggregation and relationship mappings are changed and commited to
 *                             to the database, read back and compared. Specifically three levels of aggregation are tested
 *                             by making changes ot an object that is three level aggregation from the root object.
 *
 *     <p>
 * <b>Responsibilities</b>: Check if the unit of work commitAndResume functionality works properly with aggregate mappings
 *
 * <p>
 *     <b>Features Used</b>: Aggregate Mappings, Unit Of Work
 *
 * <p>
 * <b>Paths Covered</b>: Within the unit of work, different parts listed below were modified:
 *                                <ul>
 *                                <li>    <i>3 Level Aggregation</i>, modifying object at third level
 *                                <li> <i>1 Level Aggregation's 1:1 Mapping</i>, replacing object with new object
 *                                <li>    <i>1 Level Aggregation's 1:1 Mapping</i>, Replacing with a new object
 *                                <li>    <i>1 Level Aggregation's 1:M Mapping<i>, Deletion of an object
 *                                <li>    <i>1 Level Aggregation's 1:M Mapping</i>,Addition of a new element
 *                                <li> <i>1 Level Aggregation's M:M Mapping </i>, Deleting an Object
 *                                <li> <i>1 Level Aggregation's M:M Mapping</i>, Modifying an object
 *                                <li>    <i>1 Level Aggregation's M:M Mapping</i>, Replacing with a new object
 *
 * */
public class UnitOfWorkCommitResumeTest extends WriteObjectTest {
    public Object unitOfWorkWorkingCopy;
    public UnitOfWork unitOfWork;
    public UnitOfWork nestedUnitOfWork;
    public int changeVersion = 0;

    /**
     * UnitOfWorkCommitResumeTest constructor comment.
     */
    public UnitOfWorkCommitResumeTest() {
        super();
    }

    /**
     * UnitOfWorkCommitResumeTest constructor comment.
     * @param originalObject java.lang.Object
     */
    public UnitOfWorkCommitResumeTest(Object originalObject) {
        super(originalObject);
    }

    /** Change the working copy of the object. Add a suffix to the changes.
    Make sure the suffix is so long that it will be longer that the size of
    the field in the database. */
    protected void changeUnitOfWorkWorkingCopy() {
        Employee employee = (Employee)this.unitOfWorkWorkingCopy;
        AddressDescription addressDescription = employee.getAddressDescription();
        ProjectDescription projectDescription = employee.getProjectDescription();
        Responsibility responsibility = Responsibility.example6(employee);
        Vector responsibilities;
        Vector languages;
        Language language;
        String suffix = (new Integer(changeVersion)).toString();

        //Root object changed	
        employee.setFirstName("First " + suffix);
        employee.setLastName("Last " + suffix);

        // First level aggregate object changed
        projectDescription.setDescription("ProjectDescription's Description changed" + suffix);

        //Third level aggregate object changed
        addressDescription.getPeriodDescription().getPeriod().setEndDate(Helper.dateFromYearMonthDate(1900 + ((changeVersion + 1975) % 2500), (changeVersion % 12) + 1, (changeVersion % 28) + 1));
        ((Address)(addressDescription.getAddress().getValue())).setAddress(suffix + " Any Street");

        // 1 to 1 mapped object changed
        ((Computer)projectDescription.getComputer().getValue()).setDescription("Vic 20");

        //1 level aggregate's 1:M mapping, removing an element
        responsibility.setResponsibility("Changed Reponsibility" + suffix);
        responsibilities = (Vector)projectDescription.getResponsibilities().getValue();
        responsibilities.removeElement(responsibilities.firstElement());

        //1 level aggregate's 1:M mapping, adding a new element
        responsibilities.addElement(responsibility);

        // 1 level aggregate's M:M mapping, removing an element
        languages = (Vector)projectDescription.getLanguages().getValue();
        languages.removeElement(languages.firstElement());

        //1 level aggregate's M:M mapping, modifying an element	
        language = (Language)languages.firstElement();
        language.setLanguage("ModifiedLanguage" + suffix);
        // 1 level aggregate's M:M mapping, adding a new element
        languages.addElement(Language.example7());

        // Update the change version.
        changeVersion++;
    }

    /**
     * This method was created in VisualAge.
     */
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
        this.unitOfWork.commitAndResume();

        // See if the registered objects can still be changed after a commitAndResume().
        changeUnitOfWorkWorkingCopy();

        this.unitOfWork.commitAndResume();

    }

    /**
     * Verify if the objects match completely through allowing the session to use the descriptors.
     * This will compare the objects and all of their privately owned parts.
     */
    protected void verify() {
        if (!(compareObjects(this.unitOfWorkWorkingCopy, this.objectToBeWritten))) {
            throw new TestErrorException("The object in the unit of work has not been commited properly to its parent");
        }

        super.verify();
    }
}
