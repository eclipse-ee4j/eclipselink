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

import org.eclipse.persistence.queries.*;

/**
 * <p>
 * <b>Purpose</b>: Define a generic test for reading an object from the database.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Be independent of the class being tested.
 * <li> Execute the read object query and verify no errors occurred.
 * <li> Verify the object returned matches the original object written.
 * </ul>
 */
public class ReadObjectCallTest extends AutoVerifyTestCase {
    protected Object objectFromDatabase;
    protected Call call;
    protected Class referenceClass;

    /**
     * This is required to allow subclassing.
     */
    public ReadObjectCallTest() {
        setDescription("The test reads the intended object from the database through the call and checks if it was read properly");
    }

    public ReadObjectCallTest(Class referenceClass, Call aCall) {
        setReferenceClass(referenceClass);
        setCall(aCall);
        setName("ReadObjectCallTest(" + referenceClass + ")");
        setDescription("The test reads the intended object through the call, '" + referenceClass + "', from the database and checks if it was read properly");
    }

    public Class getReferenceClass() {
        return referenceClass;
    }

    public void setReferenceClass(Class referenceClass) {
        this.referenceClass = referenceClass;
    }

    public Call getCall() {
        return call;
    }

    public void setCall(Call aCall) {
        call = aCall;
    }

    protected void setup() {
		if (getSession().getLogin().getTableQualifier() != "")
			throw new TestWarningException("this test can't work with table qualifier set");
        // Flush the cache to ensure that the query is actually executed
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    protected void test() {
        this.objectFromDatabase = getSession().readObject(getReferenceClass(), getCall());
    }

    /**
     * Verify if the objects match completely through allowing the session to use the descriptors.
     * This will compare the objects and all of their privately owned parts.
     */
    protected void verify() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        if (!(compareObjects(getSession().readObject(objectFromDatabase), this.objectFromDatabase))) {
            throw new TestErrorException("The object read from the database, '" + this.objectFromDatabase + "' does not match the originial.");
        }
    }
}
