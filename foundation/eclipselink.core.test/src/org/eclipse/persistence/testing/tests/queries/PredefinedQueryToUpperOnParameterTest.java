/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class PredefinedQueryToUpperOnParameterTest extends org.eclipse.persistence.testing.framework.ReadObjectTest {

    /**
     * PredefinedQueryToUpperOnParameterTest constructor comment.
     */
    public PredefinedQueryToUpperOnParameterTest() {
        super();
    }

    /**
     * PredefinedQueryToUpperOnParameterTest constructor comment.
     * @param originalObject java.lang.Object
     */
    public PredefinedQueryToUpperOnParameterTest(Object originalObject) {
        super(originalObject);
        setName("PredefinedQueryToUpperOnParameterTest(" + originalObject +")");
    }

    protected void setup() {
        if (getSession().getLogin().getDatasourcePlatform().isDB2())
        {
          throw new TestWarningException("DB2 doesn't support UCASE() on Parameter");
        }
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression  firstNameExpression = builder.get("firstName").toUpperCase().equal((builder.getParameter("firstName").toUpperCase()));
        Expression lastNameExpression = builder.get("lastName").equal(builder.getParameter("lastName"));

        ReadObjectQuery query = new ReadObjectQuery();
        query.setReferenceClass(Employee.class);
        query.setSelectionCriteria(firstNameExpression.and(lastNameExpression));
        query.addArgument("firstName");
        query.addArgument("lastName");

    ClassDescriptor descriptor;
    if (getSession() instanceof org.eclipse.persistence.sessions.remote.RemoteSession) {
        descriptor = org.eclipse.persistence.testing.tests.remote.RemoteModel.getServerSession().getDescriptor(Employee.class);
    } else {
        descriptor = getSession().getDescriptor(Employee.class);
    }
        getSession().removeQuery("getEmployee");
        descriptor.getQueryManager().addQuery("getEmployee", query);
    }

    protected void test() {
        this.objectFromDatabase = getSession().executeQuery("getEmployee", Employee.class, "Bob", "Smith");
    }
}
