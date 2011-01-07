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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;


public class RuntimeCustomSQLQueriesTest extends AutoVerifyTestCase {
    public static final String PROJECT_FILE = "CustomSQLQueriesProject.xml";
    public static final String INSERT_SQL = "INSERT TEST";
    public static final String READ_OBJECT_SQL = "READ OBJECT TEST";
    public static final String READ_ALL_SQL = "READ ALL TEST";
    public static final String UPDATE_SQL = "UPDATE TEST";
    public static final String DELETE_SQL = "DELETE TEST";

    public RuntimeCustomSQLQueriesTest() {
        this.setDescription("Validate custom defined SQL queries by reading from an XML project");
    }

    protected void setup() {
        // Modify the employee project to use custom SQL queries
        EmployeeProject project = new EmployeeProject();
        ClassDescriptor addressDescriptor = project.getDescriptors().get(Address.class);

        addressDescriptor.getQueryManager().setInsertSQLString(INSERT_SQL);
        addressDescriptor.getQueryManager().setReadObjectSQLString(READ_OBJECT_SQL);
        addressDescriptor.getQueryManager().setReadAllSQLString(READ_ALL_SQL);
        addressDescriptor.getQueryManager().setUpdateSQLString(UPDATE_SQL);
        addressDescriptor.getQueryManager().setDeleteSQLString(DELETE_SQL);

        // write project to an XMLl project file
        XMLProjectWriter.write(PROJECT_FILE, project);
    }

    protected void test() {
        // test run time project that should contains cusomtom sql queries
        Project project = XMLProjectReader.read(PROJECT_FILE, getClass().getClassLoader());
        ClassDescriptor addressDescriptor = project.getDescriptors().get(Address.class);

        if (!addressDescriptor.getQueryManager().getInsertSQLString().equals(RuntimeCustomSQLQueriesTest.INSERT_SQL)) {
            throw new TestErrorException("Custom insert SQL for Address is not equal to " + RuntimeCustomSQLQueriesTest.INSERT_SQL);
        }

        if (!addressDescriptor.getQueryManager().getReadObjectSQLString().equals(RuntimeCustomSQLQueriesTest.READ_OBJECT_SQL)) {
            throw new TestErrorException("Custom read object SQL for Address is not equal to " + 
                                         RuntimeCustomSQLQueriesTest.READ_OBJECT_SQL);
        }

        if (!addressDescriptor.getQueryManager().getReadAllSQLString().equals(RuntimeCustomSQLQueriesTest.READ_ALL_SQL)) {
            throw new TestErrorException("Custom read all SQL for Address is not equal to " + RuntimeCustomSQLQueriesTest.READ_ALL_SQL);
        }

        if (!addressDescriptor.getQueryManager().getUpdateSQLString().equals(RuntimeCustomSQLQueriesTest.UPDATE_SQL)) {
            throw new TestErrorException("Custom update SQL for Address is not equal to " + RuntimeCustomSQLQueriesTest.UPDATE_SQL);
        }

        if (!addressDescriptor.getQueryManager().getDeleteSQLString().equals(RuntimeCustomSQLQueriesTest.DELETE_SQL)) {
            throw new TestErrorException("Custom delete SQL for Address is not equal to " + RuntimeCustomSQLQueriesTest.DELETE_SQL);
        }
    }
}
