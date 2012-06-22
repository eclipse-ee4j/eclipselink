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

import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.querykeys.OneToOneQueryKey;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;

/**
 * Purpose: Tests the recursive behavior of ObjectLevelReadQuery.getLeafMappingFor.
 * To verify bug 3608082.
 * <p>
 * Post 9.0.3.6 will throw an exception.
 * <p>
 * Pre 9.0.3.6 would pass but the mapping for the ReportItem would be null.
 *  @author  smcritch
 */
public class ReportItemQueryKeyTest extends ReportQueryTestCase {
    public ReportItemQueryKeyTest() {
        setDescription("Tests recursive behavior of this method, especially when custom query keys are used.");
    }

    protected void buildExpectedResults() throws Exception {

        /* The expression is too complicated to easily extract what we expect back,
         * and we are not really testing that.
            ReadAllQuery query = new ReadAllQuery(Employee.class);
        
            ExpressionBuilder builder = new ExpressionBuilder();
        
            Expression expression =
                builder.anyOf("projects").
                            get("teamLeader").
                                anyOf("phoneNumbers").
                                    get("id").greaterThan(0);
        
            query.setSelectionCriteria(expression);
            Vector employees = (Vector)getSession().executeQuery(query);
        
            for (Enumeration e = employees.elements(); e.hasMoreElements(); ) {
                Employee employee = (Employee) e.nextElement();
                Object[] result = new Object[1];
                result[0] = employee.getId();
                addResult(result, null);
            }
        */
    }

protected void setup()  throws Exception
{
	if (getSession() instanceof org.eclipse.persistence.sessions.remote.RemoteSession) {
		throw new TestWarningException("Test not supported in remote, it needs to modify the server-side session descriptor.");
	}
        super.setup();

        ExpressionBuilder builder = new ExpressionBuilder();

        reportQuery = new ReportQuery(builder);

        Expression expression = null;

        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addAttribute("address ids", builder.anyOf("projects").get("teamLeaderX").get("address").get("id"));

        reportQuery.setSelectionCriteria(expression);

        // Now, create a custom query key.
        ClassDescriptor projectDescriptor = getSession().getClassDescriptor(Project.class);

        ExpressionBuilder qkBuilder = new ExpressionBuilder();
        Expression joinExpression = qkBuilder.getField("EMP_ID").equal(qkBuilder.getParameter("LEADER_ID"));

        OneToOneQueryKey customQK = new OneToOneQueryKey();
        customQK.setDescriptor(projectDescriptor);
        customQK.setName("teamLeaderX");
        customQK.setReferenceClass(Employee.class);
        customQK.setJoinCriteria(joinExpression);

        projectDescriptor.addQueryKey(customQK);

    }

    public void reset() {
        ClassDescriptor projectDescriptor = getSession().getClassDescriptor(Project.class);
        projectDescriptor.getQueryKeys().remove("teamLeaderX");
    }

    public void verify() {
        //super.verify();
        //ReportQueryResult aResult = (ReportQueryResult)results.firstElement();
        ReportItem item = reportQuery.getItems().get(0);

        ClassDescriptor descriptor = getSession().getClassDescriptor(Address.class);
        DatabaseMapping mapping = descriptor.getMappingForAttributeName("id");
        if (item.getMapping() != mapping) {
            throw new TestErrorException("The mapping was not set correctly on the ReportItem: " + item.getMapping());
        }
    }
}
