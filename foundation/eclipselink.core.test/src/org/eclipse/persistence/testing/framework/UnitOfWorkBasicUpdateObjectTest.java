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
package org.eclipse.persistence.testing.framework;

import org.eclipse.persistence.sessions.*;

/**
 * <p>
 * <b>Purpose</b>: Define a generic test for writing an object to the database
 * using UnitOfWork commit().
 * Should originalObject contain no changes to the original object from the
 * database (a TRIVIAL UPDATE), find and mutate a direct to field mapping before
 * writing the object to the database.  If originalObject is different from but
 * has the same primary key as an object on the database, do not mutate the 
 * object as it has already been changed (a NON-TRIVIAL UPDATE).
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Be independent of the class being tested.
 * <li> Attempt to mutate a Direct to Field mapping if instructed to do so.
 * <li> Execute the insert object query and verify no errors occurred.
 * <li> Verify the object written matches the object that was written.
 * </ul>
 */
public class UnitOfWorkBasicUpdateObjectTest extends WriteObjectTest {

    public  UnitOfWorkBasicUpdateObjectTest() {
        setDescription("The test writing (using UnitOfWork) of the intended "+
            "object from the database and checks if it was inserted properly");
    }

    public UnitOfWorkBasicUpdateObjectTest(Object originalObject) {
        this.originalObject = originalObject;
        setName(getName() + "(" + originalObject.getClass() + ")");
        setDescription(
            "The test writing (using UnitOfWork) of the intended object, '"+
            originalObject+"', from the database and checks if it was inserted "+
            "properly");
    }

    /**
     * The test() method will, if required, pass the UnitOfWork clone object to
     * the findAndMutateDirectToFieldMappingInObject method, and will then
     * attempt to write the object to the database.
     */
    protected void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Object cloneObjectToBeWritten = 
            uow.registerObject(this.objectToBeWritten);
        if (testShouldMutate())
        {
            cloneObjectToBeWritten = 
                this.findAndMutateDirectToFieldMappingInObject(
                    cloneObjectToBeWritten, true);
        }
        uow.commit();
    }
}
