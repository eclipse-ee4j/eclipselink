/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.identitymaps.cacheinvalidation;

import org.eclipse.persistence.testing.framework.TestModel;

public class CacheExpiryModel extends TestModel {
    public CacheExpiryModel() {
        setDescription("This model tests the TopLink Cache Expiry feature.");
    }

    public void addRequiredSystems() {
        addRequiredSystem(new org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem());
    }

    public void addTests() {
        addTest(new CacheExpiryTestSuite());
    }
}
