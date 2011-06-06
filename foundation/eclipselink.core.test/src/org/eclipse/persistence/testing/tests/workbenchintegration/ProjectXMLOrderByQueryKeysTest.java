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

import java.util.List;

import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;


/**
 * Tests that the order by query keys are properly persisted to the deployment
 * xml.
 * 
 * @author Guy Pelletier
 * @version 1.0 
 * @date March 11, 2005
 */
public class ProjectXMLOrderByQueryKeysTest extends TestCase {
    private List m_queryKeyExpressionsAfterWrite;
    private List m_queryKeyExpressionsBeforeWrite;
    private static final String TEMP_FILE = "TempTestProjectSafeToDelete.xml";
    private static final String ATTRIBUTE_NAME = "phoneNumbers";

    public ProjectXMLOrderByQueryKeysTest() {
        setDescription("Tests the order by query keys on a collection mapping.");
    }

    public void test() {
        Project project = new EmployeeProject();
        CollectionMapping mapping = 
            (CollectionMapping)project.getDescriptor(Employee.class).getMappingForAttributeName(ATTRIBUTE_NAME);

        // Add the new order by query keys to the mapping
        mapping.addAscendingOrdering("ascending1");
        mapping.addDescendingOrdering("descending1");
        mapping.addAscendingOrdering("ascending2");
        mapping.addDescendingOrdering("descending2");

        m_queryKeyExpressionsBeforeWrite = mapping.getOrderByQueryKeyExpressions();

        // Write out the project with changes and read back in again.
        XMLProjectWriter.write(TEMP_FILE, project);

        project = XMLProjectReader.read(TEMP_FILE, getClass().getClassLoader());
        mapping = 
                (CollectionMapping)project.getDescriptor(Employee.class).getMappingForAttributeName(ATTRIBUTE_NAME);

        // Store the query keys after reading them back in again
        m_queryKeyExpressionsAfterWrite = mapping.getOrderByQueryKeyExpressions();
    }


    protected void verify() {
        if (m_queryKeyExpressionsBeforeWrite.size() != m_queryKeyExpressionsAfterWrite.size()) {
            throw new TestErrorException("The number of query keys read was not equal to the number written.");
        }

        // check that each is in the same order as they were set.
        for (int i = 0; i < m_queryKeyExpressionsBeforeWrite.size(); i++) {
            String key1 = 
                ((FunctionExpression)m_queryKeyExpressionsBeforeWrite.get(i)).getBaseExpression().getName();
            String key2 = 
                ((FunctionExpression)m_queryKeyExpressionsAfterWrite.get(i)).getBaseExpression().getName();

            if (!key1.equals(key2)) {
                throw new TestErrorException("Ordering query keys not written (or read) in the same order as they were set.");
            }
        }
    }
}
