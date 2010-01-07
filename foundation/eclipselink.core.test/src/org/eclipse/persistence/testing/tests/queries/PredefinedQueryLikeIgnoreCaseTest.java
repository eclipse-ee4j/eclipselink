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

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.framework.ReadObjectTest;

/**
 * Test a predefined query that uses a case insensitive like expression.
 */
public class PredefinedQueryLikeIgnoreCaseTest extends ReadObjectTest {
    public PredefinedQueryLikeIgnoreCaseTest() {
        super();
    }

    public PredefinedQueryLikeIgnoreCaseTest(Object originalObject) {
        super(originalObject);
    }

    /**
     * Add the query to the descriptor.
     */
    protected void setup() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression nameExpression = builder.get("firstName").likeIgnoreCase(builder.getParameter("NAME"));

        ReadObjectQuery query = new ReadObjectQuery();
        query.setReferenceClass(Employee.class);
        query.setSelectionCriteria(nameExpression);
        query.addArgument("NAME");

	ClassDescriptor descriptor;
	if (getSession() instanceof org.eclipse.persistence.sessions.remote.RemoteSession)
	{
		descriptor = org.eclipse.persistence.testing.tests.remote.RemoteModel.getServerSession().getDescriptor(Employee.class);
	}
	else
	{
		descriptor = getSession().getDescriptor(Employee.class);
	}
        // Test that the query can be defined twice and still found
        // Bug that was uncovered from running this test twice,
        // so to make test consistent will now always fail with the bug.
        descriptor.getQueryManager().addQuery("likeIgnoreCase", query);
        descriptor.getQueryManager().addQuery("likeIgnoreCase", query);
    }

    /**
     * Execute the predefined query.
     */
    protected void test() {
        try {
            this.objectFromDatabase = getSession().executeQuery("likeIgnoreCase", Employee.class, "Bob");
        } catch (org.eclipse.persistence.exceptions.DatabaseException exception) {
            throw new TestWarningException("Function not supported on this database.");
        }
    }

    /**
     * Verify that the object was returned.
     */
    protected void verify() {
        if (this.objectFromDatabase == null) {
            throw new TestErrorException("The object was not read from the database!!!");
        }
    }
}
