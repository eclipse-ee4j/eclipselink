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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.*;

public abstract class ConformResultsInUnitOfWorkTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public ObjectLevelReadQuery conformedQuery;
    public Object result;
    public UnitOfWork unitOfWork;
    public boolean shouldUseWrapperPolicy = false;

    public abstract void buildConformQuery();

    public abstract void prepareTest();

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        rollbackTransaction();
        if (shouldUseWrapperPolicy()) {
            getSession().getDescriptor(Employee.class).setWrapperPolicy(null);
        }
    }

    public void setup() {
        beginTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        unitOfWork = getSession().acquireUnitOfWork();
        if (shouldUseWrapperPolicy()) {
            EmployeeWrapperPolicy policy = new EmployeeWrapperPolicy();
            getSession().getDescriptor(Employee.class).setWrapperPolicy(policy);
        }
        prepareTest();
        buildConformQuery();
    }

    public void test() {
        result = unitOfWork.executeQuery(conformedQuery);
        unitOfWork.release();
    }

    public abstract void verify();

    /**
     * Added for bug 2766379: doesConform not unwrapping objects.
     */
    public void setShouldUseWrapperPolicy(boolean value) {
        this.shouldUseWrapperPolicy = value;
    }

    public boolean shouldUseWrapperPolicy() {
        return shouldUseWrapperPolicy;
    }
}
