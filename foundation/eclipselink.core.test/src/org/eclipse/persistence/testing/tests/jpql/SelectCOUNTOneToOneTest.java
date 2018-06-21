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
package org.eclipse.persistence.testing.tests.jpql;


// Domain imports
import org.eclipse.persistence.testing.models.employee.domain.*;

//TopLink imports
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

public class SelectCOUNTOneToOneTest extends JPQLTestCase {
    public void setup() {
        String ejbqlString = null;

        //Using a ReportQuery, COUNT employees that have a phone number
        ReportQuery query = new ReportQuery();
        query.setReferenceClass(PhoneNumber.class);
        query.addCount("COUNT", new ExpressionBuilder().get("owner").distinct());
        query.returnSingleAttribute();
        query.dontRetrievePrimaryKeys();
        query.setName("selectEmployeesThatHavePhoneNumbers");

        setOriginalOject(getSession().executeQuery(query));
        setReferenceClass(PhoneNumber.class);
        useReportQuery();

        //setup the EJBQL to do the same
        ejbqlString = "SELECT COUNT(DISTINCT phone.owner) FROM Phone phone";
        setEjbqlString(ejbqlString);
        super.setup();
    }
}
