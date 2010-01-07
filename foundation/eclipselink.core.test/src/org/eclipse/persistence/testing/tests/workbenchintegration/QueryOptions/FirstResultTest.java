/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import java.util.Vector;

import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;


public class FirstResultTest extends AutoVerifyTestCase {
    private Vector employees;

    public FirstResultTest() {
        setDescription("Test that FirstResult persists properly.");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
    }

    public void test() {
        employees = 
                (Vector)getSession().executeQuery("firstResultQuery", org.eclipse.persistence.testing.models.employee.domain.Employee.class);
    }

    public void verify() {
        if (employees.size() != 10) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("ReadAllQuery with setFirstResult test failed. Mismatched objects returned");
        }
    }
}
