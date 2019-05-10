/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpql;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.inheritance.IBMPC;

public class SelectSimpleBooleanTest extends JPQLTestCase {
    private Exception caught = null;
    private IBMPC ibmpc = null;

    public static SelectSimpleBooleanTest getSimpleTrueTest() {
        SelectSimpleBooleanTest theTest = new SelectSimpleBooleanTest();

        String ejbqlString = "SELECT OBJECT(i) FROM IBMPC i WHERE ";
        ejbqlString = ejbqlString + "i.isClone = true";
        theTest.setEjbqlString(ejbqlString);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("isClone").equal(new Boolean(true));

        theTest.setOriginalObjectExpression(whereClause);

        theTest.setName("SelectSimpleBooleanTest -> = TRUE test");

        return theTest;
    }

    public static SelectSimpleBooleanTest getSimpleFalseTest() {
        SelectSimpleBooleanTest theTest = new SelectSimpleBooleanTest();

        String ejbqlString = "SELECT OBJECT(i) FROM IBMPC i WHERE ";
        ejbqlString = ejbqlString + "i.isClone = false";
        theTest.setEjbqlString(ejbqlString);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("isClone").equal(new Boolean(false));

        theTest.setOriginalObjectExpression(whereClause);

        theTest.setName("SelectSimpleBooleanTest -> = FALSE test");

        return theTest;
    }

    public static SelectSimpleBooleanTest getSimpleNotEqualsTrueTest() {
        SelectSimpleBooleanTest theTest = new SelectSimpleBooleanTest();

        String ejbqlString = "SELECT OBJECT(i) FROM IBMPC i WHERE ";
        ejbqlString = ejbqlString + "i.isClone <> true";
        theTest.setEjbqlString(ejbqlString);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("isClone").notEqual(new Boolean(true));

        theTest.setOriginalObjectExpression(whereClause);

        theTest.setName("SelectSimpleBooleanTest -> <> TRUE test");

        return theTest;
    }

    public static SelectSimpleBooleanTest getSimpleNotEqualsFalseTest() {
        SelectSimpleBooleanTest theTest = new SelectSimpleBooleanTest();

        String ejbqlString = "SELECT OBJECT(i) FROM IBMPC i WHERE ";
        ejbqlString = ejbqlString + "i.isClone <> false";
        theTest.setEjbqlString(ejbqlString);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("isClone").notEqual(new Boolean(false));

        theTest.setOriginalObjectExpression(whereClause);

        theTest.setName("SelectSimpleBooleanTest -> <> FALSE test");

        return theTest;
    }

    public void test() {
        try {
            super.test();
        } catch (Exception e) {
            setCaught(e);
        }
    }

    public void setup() {
        //insert computer that we're looking for
        ibmpc = org.eclipse.persistence.testing.models.inheritance.Computer.example3a();
        getDatabaseSession().writeObject(ibmpc);

        //execute query to read what we just wrote out
        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(org.eclipse.persistence.testing.models.inheritance.IBMPC.class);
        raq.setSelectionCriteria(getOriginalObjectExpression());

        setOriginalOject(getSession().executeQuery(raq));
        setReferenceClass(org.eclipse.persistence.testing.models.inheritance.IBMPC.class);

        super.setup();
    }

    public void reset() {
        //remove the computer that we just inserted
        getDatabaseSession().deleteObject(ibmpc);
    }

    public void verify() throws Exception {
        if (getCaught() != null) {
            throw new TestErrorException(getName() + " Verify Failed:" + getCaught().getMessage());
        }
    }

    private Exception getCaught() {
        return caught;
    }

    private void setCaught(Exception wasCaught) {
        caught = wasCaught;
    }
}
