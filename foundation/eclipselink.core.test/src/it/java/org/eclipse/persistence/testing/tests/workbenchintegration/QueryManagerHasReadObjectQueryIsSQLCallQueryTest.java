/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.sessions.factories.ProjectClassGenerator;


/** This class has been modified as per instructions from Tom Ware and Development
 *  the test(), verify() are all inherited from the parent class
 *  We pass in the TopLink project and the string we are looking for and the superclass does the verification and testing
 */
public class QueryManagerHasReadObjectQueryIsSQLCallQueryTest extends ProjectClassGeneratorResultFileTest {
    ClassDescriptor descriptorToModify;
    DatabaseMapping mappingToModify;

    public QueryManagerHasReadObjectQueryIsSQLCallQueryTest() {
        super(new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject(),
              "descriptor.getQueryManager().setReadObjectSQLString(\"testString\");");
        setDescription("Test addQueryManagerPropertyLines method -> ReadObjectQuery.isSQLQuery && DescriptorQueryManager().hasReadObjectQuery");
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        project = new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject();

        descriptorToModify = project.getDescriptors().get(Employee.class);
        ReadObjectQuery testReadQuery = new ReadObjectQuery();
        SQLCall testCall = new SQLCall();
        testReadQuery.setCall(testCall); // setting the SQLCall on ReadObject
        testReadQuery.setSQLString("testString");
        descriptorToModify.getQueryManager().setReadObjectQuery(testReadQuery);
        generator = new ProjectClassGenerator(project);
    }
}
