/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.framework;

import java.util.*;
import org.eclipse.persistence.expressions.Expression;
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
public class ReadAllCallWithOrderingTest extends AutoVerifyTestCase {
    protected int originalObjectsSize;
    protected Object objectsFromDatabase;
    protected Class referenceClass;
    protected Call call;
    protected Expression orderBy;

    public ReadAllCallWithOrderingTest(Class referenceClass, int originalObjectsSize, Call aCall, Expression orderBy) {
        setOriginalObjectsSize(originalObjectsSize);
        setReferenceClass(referenceClass);
        setCall(aCall);
        setName("ReadAllCallWithOrderingTest(" + referenceClass.getName() + ")");
        setOrderBy(orderBy);
        setDescription("The test reads the intended objects from the database through the call, and checks if it was read properly.");
    }

    public Call getCall() {
        return call;
    }

    public void setCall(Call aCall) {
        call = aCall;
    }

    public Expression getOrderBy() {
        return orderBy;
    }

    public int getOriginalObjectsSize() {
        return originalObjectsSize;
    }

    public Class getReferenceClass() {
        return referenceClass;
    }

    public void setOrderBy(Expression orderBy) {
        this.orderBy = orderBy;
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
        ReadAllQuery query = new ReadAllQuery(getReferenceClass(), getCall());
        query.addOrdering(getOrderBy());
        this.objectsFromDatabase = getSession().executeQuery(query);
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
