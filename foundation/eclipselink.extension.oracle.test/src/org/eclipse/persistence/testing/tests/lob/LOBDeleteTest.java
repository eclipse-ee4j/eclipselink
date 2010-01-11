/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.lob;

import org.eclipse.persistence.testing.framework.DeleteObjectTest;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;

/**
 * <p>
 * <b>Purpose</b>: Define a test for deleting CLOB/BLOB from the database.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Insert a new object (with BLOB/CLOB value) ready for deleting
 * <li> Execute the delete object query to delete the inserted object and verify no errors occurred.
 * <li> Verify the object has been completely deleted from the database.	
 * </ul>
 * 
 * @author King Wang (Aug. 2002)
 * @since TopLink/Java 5.0
 */
public class LOBDeleteTest extends DeleteObjectTest {
    public LOBDeleteTest(Object originalObject) {
        this.originalObject = originalObject;
        setName("LOBDeleteTest(" + originalObject + ")");
        setDescription("This case tests the BLOB/CLOB delete with size less or bigger than 4k and checks if it was deleted properly");
    }

    protected void setup() {
        getAbstractSession().beginTransaction();
        //insert the object into database during the setup (assume the brand new object)
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(this.originalObject);
        uow.assignSequenceNumber(originalObject);
        uow.commit();

        this.query = new ReadObjectQuery();
        this.query.setSelectionObject(this.originalObject);
        objectFromDatabase = getSession().executeQuery(this.query);
    }
}
