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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;


public class ProjectXMLDatabaseTableNameTest extends AutoVerifyTestCase {
    private String m_tableNameAfterWrite;
    private String m_tableNameBeforeWrite;
    private static final String TABLE_NAME_QUALIFIER = "FULLY.QUALIFIED";
    private static final String TEMP_FILE = "TempProjectSafeToDelete.xml";

    public ProjectXMLDatabaseTableNameTest() {
        setDescription("Tests that fully qualified table names are written out.");
    }

    public void reset() {
    }

    protected void setup() throws Exception {
    }

    public void test() {
        Project project1 = new EmployeeProject();
        m_tableNameBeforeWrite = project1.getDescriptor(Address.class).getTableName();

        DatabaseTable dbTable = project1.getDescriptor(Address.class).getTable(m_tableNameBeforeWrite);
        dbTable.setName(m_tableNameBeforeWrite);
        dbTable.setTableQualifier(TABLE_NAME_QUALIFIER);

        // Write out the project with changes and read back in again.
        XMLProjectWriter.write(TEMP_FILE, project1);

        Project project2 = XMLProjectReader.read(TEMP_FILE, getClass().getClassLoader());
        m_tableNameAfterWrite = 
                project2.getDescriptor(Address.class).getTable(m_tableNameBeforeWrite).getQualifiedName();
    }

    protected void verify() {
        if (!m_tableNameAfterWrite.equals(TABLE_NAME_QUALIFIER + "." + m_tableNameBeforeWrite)) {
            throw new TestErrorException("The table name was incorrectly written out. Expected: " + 
                                         TABLE_NAME_QUALIFIER + "." + m_tableNameBeforeWrite + ", wrote: " + 
                                         m_tableNameAfterWrite);
        }
    }
}
