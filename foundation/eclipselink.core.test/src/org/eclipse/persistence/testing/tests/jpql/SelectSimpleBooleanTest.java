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

        String ejbqlString = "SELECT OBJECT(ibmpc) FROM IBMPC ibmpc WHERE ";
        ejbqlString = ejbqlString + "ibmpc.isClone = true";
        theTest.setEjbqlString(ejbqlString);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("isClone").equal(new Boolean(true));

        theTest.setOriginalObjectExpression(whereClause);

        theTest.setName("SelectSimpleBooleanTest -> = TRUE test");

        return theTest;
    }

    public static SelectSimpleBooleanTest getSimpleFalseTest() {
        SelectSimpleBooleanTest theTest = new SelectSimpleBooleanTest();

        String ejbqlString = "SELECT OBJECT(ibmpc) FROM IBMPC ibmpc WHERE ";
        ejbqlString = ejbqlString + "ibmpc.isClone = false";
        theTest.setEjbqlString(ejbqlString);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("isClone").equal(new Boolean(false));

        theTest.setOriginalObjectExpression(whereClause);

        theTest.setName("SelectSimpleBooleanTest -> = FALSE test");

        return theTest;
    }

    public static SelectSimpleBooleanTest getSimpleNotEqualsTrueTest() {
        SelectSimpleBooleanTest theTest = new SelectSimpleBooleanTest();

        String ejbqlString = "SELECT OBJECT(ibmpc) FROM IBMPC ibmpc WHERE ";
        ejbqlString = ejbqlString + "ibmpc.isClone <> true";
        theTest.setEjbqlString(ejbqlString);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("isClone").notEqual(new Boolean(true));

        theTest.setOriginalObjectExpression(whereClause);

        theTest.setName("SelectSimpleBooleanTest -> <> TRUE test");

        return theTest;
    }

    public static SelectSimpleBooleanTest getSimpleNotEqualsFalseTest() {
        SelectSimpleBooleanTest theTest = new SelectSimpleBooleanTest();

        String ejbqlString = "SELECT OBJECT(ibmpc) FROM IBMPC ibmpc WHERE ";
        ejbqlString = ejbqlString + "ibmpc.isClone <> false";
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
