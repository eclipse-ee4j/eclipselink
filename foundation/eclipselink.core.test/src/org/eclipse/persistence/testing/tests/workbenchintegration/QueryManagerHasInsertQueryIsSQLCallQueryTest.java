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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.sessions.factories.ProjectClassGenerator;


/** This class has been modified as per instructions from Tom Ware and Development
 *  the test(), verify() are all inherited from the parent class
 *  We pass in the TopLink project and the string we are looking for and the superclass does the verification and testing
 */
public class QueryManagerHasInsertQueryIsSQLCallQueryTest extends ProjectClassGeneratorResultFileTest {
    ClassDescriptor descriptorToModify;
    DatabaseMapping mappingToModify;

    public QueryManagerHasInsertQueryIsSQLCallQueryTest() {
        super(new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject(),
              "descriptor.getQueryManager().setInsertSQLString(\"testString\");");
        setDescription("Test addQueryManagerPropertyLines method -> InsertQuery.isSQLQuery && DescriptorQueryManager().hasInsertQuery");
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        project = new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject();

        descriptorToModify = project.getDescriptors().get(Employee.class);
        InsertObjectQuery testReadQuery = new InsertObjectQuery();
        SQLCall testCall = new SQLCall();
        testReadQuery.setCall(testCall); // setting the SQLCall on ReadObjectQuery
        testReadQuery.setSQLString("testString");
        descriptorToModify.getQueryManager().setInsertQuery(testReadQuery);
        generator = new ProjectClassGenerator(project);
    }
}
