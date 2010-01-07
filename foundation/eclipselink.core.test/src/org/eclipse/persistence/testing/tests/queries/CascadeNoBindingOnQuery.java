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
package org.eclipse.persistence.testing.tests.queries;

import java.util.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/** Test the ordering feature
*/
public class CascadeNoBindingOnQuery extends AutoVerifyTestCase {
    public boolean shouldBindParameters;
    public boolean usesBatchReading;
    public CascadeNoBindingOnQuery() {
        setDescription("This tests verifies that setting binding off on a query will cascade through indirection");
    }

    public void setup() {
        this.shouldBindParameters = getSession().getLogin().shouldBindAllParameters();
        OneToManyMapping mapping = (OneToManyMapping)getSession().getDescriptor(Employee.class).getMappingForAttributeName("phoneNumbers");
        this.usesBatchReading = mapping.shouldUseBatchReading();
        mapping.useBatchReading();
        getSession().getLogin().setShouldBindAllParameters(true);
    }

    public void reset() {
        getSession().getLogin().setShouldBindAllParameters(this.shouldBindParameters);
        OneToManyMapping mapping = (OneToManyMapping)getSession().getDescriptor(Employee.class).getMappingForAttributeName("phoneNumbers");
        mapping.setUsesBatchReading(this.usesBatchReading);
    }

    public void test() {
        Vector emps = getSession().readAllObjects(Employee.class, new ExpressionBuilder().get("firstName").like("%o%"));
        Vector pks = new Vector();
        for (int index = 0; index < emps.size(); ++index) {
            pks.add(((Employee)emps.get(index)).getId());
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

    protected void verify() {
    }
}
