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
 *     dminsky - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.inheritance;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.framework.TestCase;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.UnitOfWork;

import org.eclipse.persistence.testing.models.inheritance.Apple;
import org.eclipse.persistence.testing.models.inheritance.Pear;
import org.eclipse.persistence.testing.models.inheritance.Teacher;

/**
 * This test is partly timing related (changeset processing order), and is repeated multiple times
 * A specific inheritance model, create new objects and register them with a nested uow
 * Commit nested and root uow, verify that the results are correctly persisted and merged
 * For EL bug 378512 - use original object from nested uow for registration in parent uow
 */
public class NestedUnitOfWorkMergeIntoParentTest extends TestCase {
    
    protected static final int numberOfTimesToRepeatTest = 10;
    
    public NestedUnitOfWorkMergeIntoParentTest() {
        super();
        setDescription("Use original object from nested uow for registration in parent uow");
    }
    
    public void test() {
        for (int runNumber = 0; runNumber < numberOfTimesToRepeatTest; runNumber++) {
            UnitOfWork unitOfWork = getSession().acquireUnitOfWork();
            UnitOfWork nestedUnitOfWork = unitOfWork.acquireUnitOfWork();
            
            Teacher teacher = new Teacher();
            teacher.setName("Mrs. Crabapple");
            
            Apple apple = new Apple();
            apple.setQuality("high");
            apple.setTeacher(teacher);
            
            nestedUnitOfWork.registerObject(teacher);
            nestedUnitOfWork.registerObject(apple);
            
            int numberOfPearsToInsert = 10;
            List<Pear> pearsList = new ArrayList<Pear>(numberOfPearsToInsert);
            for (int pearNumber = 0; pearNumber < numberOfPearsToInsert; pearNumber++) {
                Pear pear = new Pear();
                pearsList.add(pear);
                pear.setQuality("medium");
                nestedUnitOfWork.registerObject(pear);
            }
            
            nestedUnitOfWork.commit();
            unitOfWork.commit();
            
            Teacher teacherRead = (Teacher)getSession().readObject(
                    Teacher.class, 
                    new ExpressionBuilder().get("id").equal(teacher.getId()));
            assertNotNull("Teacher should not be null", teacherRead);
            assertFalse("Teacher id should not be zero", teacherRead.getId() == 0l);
            
            Apple appleRead = (Apple)getSession().readObject(
                    Apple.class, 
                    new ExpressionBuilder().get("id").equal(apple.getId()));
            assertNotNull("Apple read should not be null", appleRead);
            assertFalse("Apple id should not be zero", appleRead.getId() == 0l);
            assertNotNull("Teacher referenced by Apple should not be null", appleRead.getTeacher());
            assertEquals("Teacher read should have same pk as the teacher referenced by apple", 
                    teacherRead.getId(), appleRead.getTeacher().getId());
            
            for (Pear pearInList : pearsList) {
                Pear pearRead = (Pear)getSession().readObject(
                    Pear.class,
                    new ExpressionBuilder().get("id").equal(pearInList.getId()));
                assertNotNull("Pear read should not be null", pearRead);
                assertFalse("Pear id should not be zero", pearRead.getId() == 0l);
            }
        }
    }

}
