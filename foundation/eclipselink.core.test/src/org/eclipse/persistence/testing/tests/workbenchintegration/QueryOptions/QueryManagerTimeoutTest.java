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
package org.eclipse.persistence.testing.tests.workbenchintegration.QueryOptions;

import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.tests.workbenchintegration.EmployeeWorkbenchIntegrationSystem;


/**
 * Bug 3670436
 * Ensure QueryManager timeout setting gets added in project XML and in Project class generation.
 * 
 */
public class QueryManagerTimeoutTest extends AutoVerifyTestCase {


    public QueryManagerTimeoutTest() {
        setDescription("Ensure timeout is correctly set on a DescriptorQueryManager.");
    }

    public void verify() {
        if (getSession().getPlatform().isPostgreSQL()) {
            throwWarning("Postgres does not support setting query timeout.");
        }
        if (getSession().getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Project.class).getQueryManager().getQueryTimeout() != 
            EmployeeWorkbenchIntegrationSystem.QUERY_MANAGER_TIMEOUT) {
            throw new TestErrorException("QueryManager timeout was not preserved in exported project.");
        }
    }
}

