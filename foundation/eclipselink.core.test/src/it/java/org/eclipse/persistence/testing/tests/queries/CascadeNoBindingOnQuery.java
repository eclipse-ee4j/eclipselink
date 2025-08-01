/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.Vector;

/** Test the ordering feature
*/
public class CascadeNoBindingOnQuery extends AutoVerifyTestCase {
    public boolean shouldBindParameters;
    public boolean usesBatchReading;
    public CascadeNoBindingOnQuery() {
        setDescription("This tests verifies that setting binding off on a query will cascade through indirection");
    }

    @Override
    public void setup() {
        this.shouldBindParameters = getSession().getLogin().shouldBindAllParameters();
        OneToManyMapping mapping = (OneToManyMapping)getSession().getDescriptor(Employee.class).getMappingForAttributeName("phoneNumbers");
        this.usesBatchReading = mapping.shouldUseBatchReading();
        mapping.useBatchReading();
        getSession().getLogin().setShouldBindAllParameters(true);
    }

    @Override
    public void reset() {
        getSession().getLogin().setShouldBindAllParameters(this.shouldBindParameters);
        OneToManyMapping mapping = (OneToManyMapping)getSession().getDescriptor(Employee.class).getMappingForAttributeName("phoneNumbers");
        mapping.setUsesBatchReading(this.usesBatchReading);
    }

    @Override
    public void test() {
        Vector emps = getSession().readAllObjects(Employee.class, new ExpressionBuilder().get("firstName").like("%o%"));
        Vector pks = new Vector();
        for (Object emp : emps) {
            pks.add(((Employee) emp).getId());
        }
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.setShouldPrepare(false);
        query.cascadeAllParts();
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("id").in(builder.getParameter("inList"));
        query.setSelectionCriteria(expression);
        query.addArgument("inList", Vector.class);
        Vector arguments = new Vector();
        arguments.add(pks);
        emps = (Vector)getSession().executeQuery(query, arguments);
        try {
            Vector phoneNumbers = ((Employee)emps.get(0)).getPhoneNumbers();
        } catch (Exception ex) {
            throw new TestErrorException("Failed to cascade no prepare on query. : " + ex);
        }
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        query = new ReadAllQuery(Employee.class);
        query.setShouldBindAllParameters(false);
        query.cascadeAllParts();
        query.setSelectionCriteria(expression);
        query.addArgument("inList", Vector.class);
        emps = (Vector)getSession().executeQuery(query, arguments);
        try {
            Vector phoneNumbers = ((Employee)emps.get(0)).getPhoneNumbers();
        } catch (Exception ex) {
            throw new TestErrorException("Failed to cascade no binding on query. : " + ex);
        }
    }

    @Override
    protected void verify() {
    }
}
