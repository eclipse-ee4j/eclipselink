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
package org.eclipse.persistence.testing.tests.queries;

import java.util.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * We have added support so that there can be multiple queries
 * with the same name (each with different argument sets, or no arguments)
 * cached on a descriptor.
 * DescriptorQueryManager.getQuery() should return the right named query
 * from the Vector (a Vector of queries, all with the same name, each with
 * different argument sets, or no arguments) if there are zero arguments.
 *
 * This test verifies that by adding (using, and removing) more
 * than one named query (with the same name but with different
 * argument sets, or no arguments) to a descriptor.
 *
 * Class created on Mar 19/2002; CR#3754 in StarTeam; Predrag
 */
public class NamedQueryGetQueryNoArgumentsTest extends MultiNameQueriesTestCase {
    protected Exception caughtException;
    protected ClassDescriptor descriptor;

    /**
    * Employee.class used to add two identically named NamedQueries
    */
    public NamedQueryGetQueryNoArgumentsTest() {
        setDescription("Verifies if a Named Query with no arguments" + " can be retreived from DescriptorQueryManager" + " just by calling getQuery, eventhough there are" + " some other queries with the same name cached" + " on DescriptorQueryManager");
    }

    public void reset() {
        // do not want to keep named queries on employee descriptor
        descriptor.getQueryManager().removeQuery("namedQuerySameName");
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        setDescriptorNamedQueries(Employee.class);
        addNamedQueryFirstName();
        addNamedQueryFirstAndLastName();
        addNamedQueryFirstAndLastNameNoArguments();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    // end of setup()
    public ClassDescriptor getDescriptorNamedQueries() {
        return descriptor;
    }

    // end of getDescriptorNamedQuery
    public void setDescriptorNamedQueries(Class cls) {
        this.descriptor = getSession().getClassDescriptor(cls);
    }

    // end of setDescriptorNamedQuery
    public void useNamedQueryFirstAndLastNameNoArguments() {
        ReadAllQuery queryFirstAndLastNameNoArguments = (ReadAllQuery)descriptor.getQueryManager().getQuery("namedQuerySameName");

        //descriptor.getQueryManager().getQuery("namedQuerySameName", null);
        Vector empsByFirstAndLastNameNoArguments = (Vector)getSession().executeQuery(queryFirstAndLastNameNoArguments);
    }

    // end of useNamedQueryFirstAndLastName
    public void addNamedQueryFirstName() {
        descriptor.getQueryManager().addQuery("namedQuerySameName", getNamedQueryFirstName());
    }

    // end of addNamedQueryFirstName
    public void addNamedQueryFirstAndLastName() {
        descriptor.getQueryManager().addQuery("namedQuerySameName", getNamedQueryFirstAndLastName());
    }

    // end of addNamedQueryFirstAndLastName
    public void addNamedQueryFirstAndLastNameNoArguments() {
        descriptor.getQueryManager().addQuery("namedQuerySameName", getNamedQueryFirstAndLastNameNoArguments());
    }

    // end of addNamedQueryFirstAndLastNameNoArguments    
    public void test() {
        // Can more than one named query co-exist with the same name?
        // Same name "namedQuerySameName" added three times to the very
        // same descriptor - Employee.class, with different argument sets
        // including one named query with no arguments.
        // We are trying to retreive the one that has no arguments !!
        try {
            useNamedQueryFirstAndLastNameNoArguments();
        } catch (ClassCastException e) {
            caughtException = e;
        }
    }

    // end of test()
    public void verify() {
        if (caughtException != null) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("Multiple queries with the same named cached on the DescriptorQueryManager.\n" + "Each with different argument sets, or no arguments.\n" + "This exception thrown while testing test case\n" + "----- NamedQueryGetQueryNoArgumentsTest() -----\n");
        }
    }
    // end of verify()
}// end of public class NamedQueryGetQueryNoArgumentsTest
