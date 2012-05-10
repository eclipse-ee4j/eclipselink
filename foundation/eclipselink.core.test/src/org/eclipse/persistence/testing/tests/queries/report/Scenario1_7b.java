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
package org.eclipse.persistence.testing.tests.queries.report;

import java.util.*;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * ReportQuery test for Scenario 1.7b
 * SELECT to.TYPE, t1.ID FROM PHONE t0, EMPLOYEE t1 WHERE t0.EMP_ID = t1.EMP_ID
 */
public class Scenario1_7b extends ReportQueryTestCase {
    public Scenario1_7b() {
        setDescription("Include PK attributes at start of Result");
    }

    protected void buildExpectedResults() {
        Vector phoneNumbers = getSession().readAllObjects(PhoneNumber.class);

        for (Enumeration e = phoneNumbers.elements(); e.hasMoreElements(); ) {
            PhoneNumber phone = (PhoneNumber)e.nextElement();
            Object pks = 
                getSession().getDescriptor(PhoneNumber.class).getObjectBuilder().extractPrimaryKeyFromObject(phone, 
                                                                                                             getAbstractSession());

            Object[] result = new Object[1];
            result[0] = phone.getType();
            addResult(result, pks);
        }
    }

    protected void setup() throws Exception {
        super.setup();
        reportQuery = new ReportQuery(new ExpressionBuilder());

        reportQuery.setReferenceClass(PhoneNumber.class);
        reportQuery.retrievePrimaryKeys();
        reportQuery.addAttribute("type", reportQuery.getExpressionBuilder().get("type"));
    }
}
