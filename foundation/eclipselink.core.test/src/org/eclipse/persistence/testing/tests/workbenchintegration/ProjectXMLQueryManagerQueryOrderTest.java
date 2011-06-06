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

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;


/**
 * bug: 5677655
 * Used to compare the order of queries contained in the DescriptorQueryManager so that 
 * XML files written out the same as they were read in wrt query order.  
 */
public class ProjectXMLQueryManagerQueryOrderTest extends AutoVerifyTestCase {
    Vector original, current; //list of queries to compare

    public ProjectXMLQueryManagerQueryOrderTest() {
    }

    public void test() {
        original = current = null;

        Project initialProj = (new EmployeeWorkbenchIntegrationSystem()).getInitialProject();
        ClassDescriptor initialDescriptor = 
            initialProj.getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Employee.class);

        Project readinProj = this.getSession().getProject();
        ClassDescriptor currentDescriptor = 
            readinProj.getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        original = initialDescriptor.getDescriptorQueryManager().getAllQueries();
        current = currentDescriptor.getDescriptorQueryManager().getAllQueries();
    }


    protected void verify() {
        if (original.size() != current.size()) {
            throw new TestErrorException("The number of queries read was not equal to the number originally.");
        }

        Iterator orig = original.iterator();
        Iterator curr = current.iterator();
        while (orig.hasNext()) {
            DatabaseQuery origQuery = (DatabaseQuery)orig.next();
            int argumentTypesSize = 0;
            if (origQuery.getArguments() != null) {
                argumentTypesSize = origQuery.getArguments().size();
            }
            Vector argumentTypes = new Vector();
            for (int i = 0; i < argumentTypesSize; i++) {
                argumentTypes.addElement(origQuery.getArgumentTypeNames().get(i));
            }

            DatabaseQuery currentQuery = (DatabaseQuery)curr.next();
            if ((origQuery.getName() != currentQuery.getName()) && 
                ((origQuery.getName() == null) || !origQuery.getName().equals(currentQuery.getName())))
                if (!argumentTypes.equals(currentQuery.getArgumentTypeNames())) {
                    throw new TestErrorException("A query in the descriptor query manager does not match the original based on arguments");
                }
        }
    }
}
