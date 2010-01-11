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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.ReadObjectTest;

/**
 * Test predefined queries.
 */
public class PredefinedQueryReadObjectTest extends ReadObjectTest {
    protected Exception caughtException;

    public PredefinedQueryReadObjectTest(Object originalObject) {
        super(originalObject);
        setName("PredefinedQueryReadObjectTest");
    }

    public void reset() {
        // do not want to keep named queries
        getSession().removeQuery("getEmployee");
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression firstNameExpression = builder.get("firstName").equal(builder.getParameter("firstName"));
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
        getSession().addQuery("getEmployee", query);
    }

    protected void test() {
        try {
            this.objectFromDatabase = getSession().executeQuery("getEmployee", "Bob", "Smith");
        } catch (ClassCastException e) {
            caughtException = e;
        }
    }

    public void verify() {
        if (caughtException != null) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("Testing predefined query. Adding to and removing from session. \n" + "This exception thrown while testing test case\n" + "----- PredefinedQueryReadObjectTest() -----\n");
        }
    }
    // end of verify()
}// end of PredefinedQueryReadObjectTest
