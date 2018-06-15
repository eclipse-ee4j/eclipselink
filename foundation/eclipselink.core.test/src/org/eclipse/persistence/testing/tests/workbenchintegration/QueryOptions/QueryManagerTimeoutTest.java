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

