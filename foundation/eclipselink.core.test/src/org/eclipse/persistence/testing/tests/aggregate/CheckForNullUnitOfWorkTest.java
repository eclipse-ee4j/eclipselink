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
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.WriteObjectTest;
import org.eclipse.persistence.testing.models.aggregate.AddressDescription;
import org.eclipse.persistence.testing.models.aggregate.Employee;
import org.eclipse.persistence.testing.models.aggregate.Language;
import org.eclipse.persistence.testing.models.aggregate.ProjectDescription;

/**
 * <p>
 * <b>Purpose</b>: This test checks to see if TopLink handles null's while cloning aggregates
 *
 * <p>
 * <b>Motivation </b>: This test was written to fix a bug. While merging if the root of
 *                                   an aggregate object contained any null's a null pointer exception was
 *                                        raised.
 * <p>
 * <b>Design</b>: The Complex Aggregate model is used. An Employee is registered into the UOW, and then
 *                             different levels of aggregation and relationship mappings are tested by setting them to
 *                             null.
 *
 *     <p>
 * <b>Responsibilities</b>: Try to commit an object with a null part, the merge should proceed without
 *                                             a NullPointerException
 * <p>
 *     <b>Features Used</b>: Aggregate Mappings, Unit Of Work
 *
 * <p>
 * <b>Paths Covered</b>: Within the unit of work, different parts listed below were set to null:
 *                                <ul>
 *                                <li>    <i>First and Last Name</i>, Direct To Field
 *                                <li> <i>Project's Description</i>, Aggregate Objects, Direct To Field
 *                                <li>    <i>Period</i>, Third level Aggregate Object replaced
 *                                <li>    <i>Computer<i>, Aggregate Object's 1 to 1 mapped object
 *                                <li>    <i>Responsibilities</i>,Aggregate objects 1:M mapping
 *                                <li> <i>Language Modified</i>, Aggregate objects M:M mapping
 *                                <li> <i>null added to languages</i>, Aggregate objects M:M mapping
 *
 */
public class CheckForNullUnitOfWorkTest extends WriteObjectTest {
    public Object unitOfWorkWorkingCopy;
    public UnitOfWork unitOfWork;
    public UnitOfWork nestedUnitOfWork;

    /**
     * CheckForNullUnitOfWorkTest constructor comment.
     */
    public CheckForNullUnitOfWorkTest() {
        super();
    }

    /**
     * CheckForNullUnitOfWorkTest constructor comment.
     * @param originalObject java.lang.Object
     */
    public CheckForNullUnitOfWorkTest(Object originalObject) {
        super(originalObject);
    }

    protected void changeUnitOfWorkWorkingCopy() {
        Employee employee = (Employee)this.unitOfWorkWorkingCopy;
        AddressDescription addressDescription = employee.getAddressDescription();
        ProjectDescription projectDescription = employee.getProjectDescription();
        Language language;
        Vector languages;

        //Root object changed	
        employee.setFirstName(null);
        employee.setLastName(null);
        //Third level aggregate object changed
        addressDescription.setPeriodDescription(null);
        //1 to 1 mapped object changed
        projectDescription.getComputer().setValue(null);
        //1 level aggregate's 1:M mapping
        projectDescription.getResponsibilities().setValue(null);
        languages = (Vector)projectDescription.getLanguages().getValue();
        language = (Language)languages.firstElement();
        language.setLanguage(null);

        // 1 level aggregate's M:M mapping, adding a new element
        //languages.addElement(null);
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
        this.unitOfWork.commit();
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
