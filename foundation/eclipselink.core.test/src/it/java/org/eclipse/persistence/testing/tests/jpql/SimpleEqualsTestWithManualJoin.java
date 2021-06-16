/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpql;

import java.util.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

//Test id comparison without 1:1, that must force a join
public class SimpleEqualsTestWithManualJoin extends JPQLTestCase {
    public void setup() {
        setReferenceClass(Employee.class);

        //add a mapping for addressId so we can use it in from "emp"
        //        DirectToFieldMapping addressIdMapping = new DirectToFieldMapping();
        //        addressIdMapping.setAttributeName("addressId");
        //        addressIdMapping.setFieldName("EMPLOYEE.ADDR_ID");
        //        addressIdMapping.setGetMethodName("getAddressId");
        //        addressIdMapping.setSetMethodName("setAddressId");
        //        addressIdMapping.setIsReadOnly(true);
        //        getSession().getDescriptor(Employee.class).addMapping(addressIdMapping);
        //        addressIdMapping.initialize(getSession());
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        ExpressionBuilder addressBuilder = new ExpressionBuilder(Address.class);
        Expression whereClause = builder.get("addressId").equal(addressBuilder.get("id"));
        Vector employees = getSession().readAllObjects(Employee.class, whereClause);

        setOriginalOject(employees);
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        String ejbqlString;
        ejbqlString = "SELECT OBJECT(emp) FROM Employee emp, Address address " + "WHERE emp.addressId = address.id";

        setEjbqlString(ejbqlString);
        super.setup();
    }

    public void reset() {
        //        getSession().getDescriptor(Employee.class).removeMappingForAttributeName("addressId");
    }
}
