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
package org.eclipse.persistence.testing.framework;

import java.util.*;
import org.eclipse.persistence.queries.*;

/**
 * <p>
 * <b>Purpose</b>: Define a generic test for reading a vector of objects from the database.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Be independent of the class being tested.
 * <li> Execute the read all query and verify no errors occurred.
 * <li> Verify the objects returned match the original number of objects.
 * </ul>
 */
public class ReadAllCallTest extends AutoVerifyTestCase {
    protected int originalObjectsSize;
    protected Object objectsFromDatabase;
    protected Class referenceClass;
    protected Call call;

    public ReadAllCallTest(Class referenceClass, int originalObjectsSize, Call aCall) {
        setOriginalObjectsSize(originalObjectsSize);
        setReferenceClass(referenceClass);
        setCall(aCall);
        setName("ReadAllCallTest(" + referenceClass.getName() + ")");
        setDescription("The test reads the intended objects from the database through the call, and checks if it was read properly.");
    }

    public Call getCall() {
        return call;
    }

    public void setCall(Call aCall) {
        call = aCall;
    }

    public int getOriginalObjectsSize() {
        return originalObjectsSize;
    }

    public Class getReferenceClass() {
        return referenceClass;
    }

    public void setOriginalObjectsSize(int size) {
        originalObjectsSize = size;
    }

    public void setReferenceClass(Class aClass) {
        referenceClass = aClass;
    }

    protected void setup() {
		if (getSession().getLogin().getTableQualifier() != "")
			throw new TestWarningException("this test can't work with table qualifier set");
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    protected void test() {
        this.objectsFromDatabase = getSession().readAllObjects(getReferenceClass(), getCall());
    }

    /**
     * Verify if number of objects returned, matches the number of object written.
     */
    protected void verify() throws Exception {
        if (!(getOriginalObjectsSize() == ((Vector)this.objectsFromDatabase).size())) {
            throw new TestErrorException((((Vector)this.objectsFromDatabase).size()) + " objects were read from the database, but originially there were, " + getOriginalObjectsSize() + ".");
        }
    }
}
