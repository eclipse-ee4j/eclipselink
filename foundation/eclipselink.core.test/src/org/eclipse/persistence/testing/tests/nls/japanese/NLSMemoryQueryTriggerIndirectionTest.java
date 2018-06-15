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
package org.eclipse.persistence.testing.tests.nls.japanese;

import org.eclipse.persistence.testing.framework.*;
//import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import java.util.Vector;

public class NLSMemoryQueryTriggerIndirectionTest extends AutoVerifyTestCase {
    protected ReadAllQuery queryAll;
    protected java.util.Vector allEmployees;
    protected java.util.Vector inMemoryResult;

    public NLSMemoryQueryTriggerIndirectionTest() {
        setDescription("[NLS_Japanese] Test memory query trigger indirection option");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        allEmployees = (Vector)getSession().executeQuery("memoryQueryTriggerIndirectionQuery", org.eclipse.persistence.testing.tests.nls.japanese.NLSEmployee.class);
        queryAll = (ReadAllQuery)getSession().getDescriptor(org.eclipse.persistence.testing.tests.nls.japanese.NLSEmployee.class).getQueryManager().getQuery("memoryQueryTriggerIndirectionQuery");
    }

    public void test() {
        ReadAllQuery queryAllCopy = (ReadAllQuery)queryAll.clone();
        queryAllCopy.checkCacheOnly();//read from cache only
        queryAllCopy.setSelectionCriteria(new ExpressionBuilder().get("address").get("city").notEqual("\u3059\u30db\u30bb\u30c8\u30c4\u30aa\u30a2\u30b7"));//"Montreal"
        inMemoryResult = (Vector)getSession().executeQuery(queryAllCopy);
    }

    public void verify() {
        if (inMemoryResult.size() != (allEmployees.size() - 1)) {
            throw new TestErrorException("In Memory Query did not return all objects.  Auto-indirection triggering is not working");
        }
    }
}
