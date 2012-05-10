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
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.TestErrorException;

/**
 * Purpose: Tests the recursive behavior of ObjectLevelReadQuery.getLeafMappingFor.
 * To verify bug 3608082.
 * <p>
 * Post 9.0.3.6 will throw an exception.
 * <p>
 * Pre 9.0.3.6 would pass but the mapping for the ReportItem would be null.
 *  @author  smcritch
 */
public class GetLeafMappingForReportQueryTest extends ReportQueryTestCase {
    public GetLeafMappingForReportQueryTest() {
        setDescription("Tests recursive behavior of this method.");
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

protected void setup()  throws Exception {
        super.setup();

        ExpressionBuilder builder = new ExpressionBuilder();

        reportQuery = new ReportQuery(builder);

        Expression expression = null;

        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addAttribute("address ids", builder.anyOf("projects").get("teamLeader").get("address").get("id"));

        reportQuery.setSelectionCriteria(expression);

    }

    public void verify() {
        //super.verify();
        ReportItem item = reportQuery.getItems().get(0);

        ClassDescriptor descriptor = getSession().getClassDescriptor(Address.class);
        DatabaseMapping mapping = descriptor.getMappingForAttributeName("id");
	
	if (! (getSession() instanceof org.eclipse.persistence.sessions.remote.RemoteSession)) {
		// Avoid check in remote because mapping is serialized, not ==.
		if (item.getMapping() != mapping) {
			throw new TestErrorException(
				"The mapping was not set correctly on the ReportItem: " + item.getMapping());
		}
	}
}

}
