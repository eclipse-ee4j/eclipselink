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
package org.eclipse.persistence.testing.tests.nls.japanese;

import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import java.util.Vector;

/**
 * If SQL is generated only once, sql string should be (t0.F_NAME = NULL) when the query is executed the second time
 * with argument null.  If SQL is generated every time when the query is executed, sql string should be (t0.F_NAME is NULL).
 */
public class NLSshouldPrepareTest extends AutoVerifyTestCase {
    private ReadObjectQuery query;
    private ReadObjectQuery queryCopy = new ReadObjectQuery();
    private Vector vec = new Vector();

    public NLSshouldPrepareTest() {
        setDescription("[NLS_Japanese] Test SQL prepared once option");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        query = (ReadObjectQuery)getSession().getDescriptor(org.eclipse.persistence.testing.tests.nls.japanese.NLSEmployee.class).getQueryManager().getQuery("shouldPrepareQuery");

        queryCopy = (ReadObjectQuery)query.clone();
        ExpressionBuilder ex = new ExpressionBuilder();
        queryCopy.setSelectionCriteria(ex.get("firstName").equal(ex.getParameter("firstName1")));
        queryCopy.addArgument("firstName1");

        vec = new Vector();
        //vec.add("Bob");
        vec.add("\u3044\u30db\u30a4");
        getSession().executeQuery(queryCopy, vec);
    }

    public void test() {
        vec.set(0, null);
        getSession().executeQuery(queryCopy, vec);
    }

    public void verify() {
        //if (!queryCopy.getCall().getSQLString().equals("SELECT t0.VERSION, t1.EMP_ID, t0.L_NAME, t0.F_NAME, t1.SALARY, t0.EMP_ID, t0.GENDER, t0.END_DATE, t0.START_DATE, t0.MANAGER_ID, t0.END_TIME, t0.START_TIME, t0.ADDR_ID FROM EMPLOYEE t0, SALARY t1 WHERE ((t0.F_NAME = 'Bob') AND (t1.EMP_ID = t0.EMP_ID))"))
        //Following works for japanese database with english data:
        if (!queryCopy.getCall().getSQLString().equals("SELECT t0.\u306b\u304a\u3064\u3066\u3051\u305d\u305b, t1.\u304a\u3059\u305f_\u3051\u3048, t0.\u3057_\u305b\u3042\u3059\u304a, t0.\u304b_\u305b\u3042\u3059\u304a, t1.\u3066\u3042\u3057\u3042\u3064\u306e, t0.\u304a\u3059\u305f_\u3051\u3048, t0.\u304d\u304a\u305b\u3048\u304a\u3064, t0.\u304a\u305b\u3048_\u3048\u3042\u3068\u304a, t0.\u3066\u3068\u3042\u3064\u3068_\u3048\u3042\u3068\u304a, t0.\u3059\u3042\u305b\u3042\u304d\u304a\u3064_\u3051\u3048, t0.\u304a\u305b\u3048_\u3068\u3051\u3059\u304a, t0.\u3066\u3068\u3042\u3064\u3068_\u3068\u3051\u3059\u304a, t0.\u3042\u3048\u3048\u3064_\u3051\u3048 FROM \u304a\u3059\u305f t0, \u3066\u3042\u3057\u3042\u3064\u306e t1 WHERE ((t0.\u304b_\u305b\u3042\u3059\u304a = ?) AND (t1.\u304a\u3059\u305f_\u3051\u3048 = t0.\u304a\u3059\u305f_\u3051\u3048))")) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("shouldPrepareTest failed.");
        }
    }
}
