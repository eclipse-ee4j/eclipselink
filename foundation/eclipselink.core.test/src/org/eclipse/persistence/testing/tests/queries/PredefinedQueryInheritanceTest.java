/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * PredefinedQueryInheritanceTest checks that named queries are accessable
 * from subclasses of the class they are defined in.
 */
public class PredefinedQueryInheritanceTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    // Class members
    public static final String QUERY_NAME = "getProjectSubClassQuery13478";
    public static final String TEST_NAME = "PredefinedQueryInheritanceTest";
    LargeProject subclassTestObjectRead;
    ReadObjectQuery query;
    ClassDescriptor descriptor;
    Exception storedException;
    String objectName;

    // Temporary storage for Queries
    DatabaseQuery sessionQuery;
    DatabaseQuery descriptorQuery;

    /**
     * PredefinedQueryInheritanceTest constructor comment.
     */
    public PredefinedQueryInheritanceTest() {
        super();
        setDescription("Tests that named queries work on subclasses from where they're defined.");
    }

    public void reset() {
        // Get a handle on the descriptor of Project.class
	if (getSession() instanceof org.eclipse.persistence.sessions.remote.RemoteSession) {
		descriptor = org.eclipse.persistence.testing.tests.remote.RemoteModel.getServerSession().getDescriptor(Project.class);
	} else {
		descriptor = getSession().getDescriptor(Project.class);
	}
        // Reset the original queries
        descriptor.getQueryManager().removeQuery(QUERY_NAME);
        if (descriptorQuery != null) {
            descriptor.getQueryManager().addQuery(descriptorQuery);
        }
        if (sessionQuery != null) {
            getSession().addQuery(sessionQuery.getName(), sessionQuery);
        }

        // Initialize identitymaps
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

    }

    protected void setup() {
        subclassTestObjectRead = null;
        storedException = null;
        sessionQuery = null;
        descriptorQuery = null;
        objectName = "";

        // Get a handle on the descriptor of Project.class
	if (getSession() instanceof org.eclipse.persistence.sessions.remote.RemoteSession) {
		descriptor = org.eclipse.persistence.testing.tests.remote.RemoteModel.getServerSession().getDescriptor(Project.class);;
	} else {
		descriptor = getSession().getDescriptor(Project.class);
	}
        // Find an object to use for testing
        try {
            subclassTestObjectRead = (LargeProject)getSession().readObject(LargeProject.class);
        } catch (Exception e) {
            setStoredException(new TestErrorException("No LargeProject test objects available for:" + TEST_NAME));
        }
        objectName = subclassTestObjectRead.getName();

        // Set up a named query and add it to the root descriptor (Project.class)
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression nameExpression = builder.get("name").equal(objectName);
        query = new ReadObjectQuery();
        query.setReferenceClass(Project.class);// Set to Project class
        query.setSelectionCriteria(nameExpression);

        // Store a copy of queries, remove any query with the same name
        descriptorQuery = descriptor.getQueryManager().getQuery(QUERY_NAME);
        sessionQuery = getSession().getQuery(QUERY_NAME);
        getSession().removeQuery(QUERY_NAME);
        descriptor.getQueryManager().removeQuery(QUERY_NAME);

        // Add this query to the descriptor
        descriptor.getQueryManager().addQuery(QUERY_NAME, query);

    }

    protected void test() {
        // Get subclass of Project class (LargeProject) back from database using the 
        // named query defined in Project
        subclassTestObjectRead = null;

        try {
            subclassTestObjectRead = (LargeProject)getSession().executeQuery(QUERY_NAME, LargeProject.class);
        } catch (Exception e) {
            setStoredException(new TestErrorException("Error reading with inherited named query in test:" + TEST_NAME, e));
            return;
        }
        if (subclassTestObjectRead == null) {
            setStoredException(new TestErrorException("Inherited named query returned null object in test:" + TEST_NAME));
            return;
        }
    }

    protected void verify() throws Exception {
        // If any errors, throw them here
        if (storedException != null) {
            throw storedException;
        }
    }

    protected void setStoredException(Exception e) {
        if (storedException == null) {
            storedException = e;
        }
    }
}// end test case
