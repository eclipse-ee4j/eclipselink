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
package org.eclipse.persistence.testing.tests.nls.japanese;

import org.eclipse.persistence.testing.framework.*;
//import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import java.util.Vector;

public class NLSMemoryQueryReturnNotConfirmedTest extends AutoVerifyTestCase {
    protected NLSEmployee employee;
    protected ReadAllQuery queryAll;
    protected ReadObjectQuery queryObject;
    protected java.util.Vector allEmployees;
    protected java.util.Vector inMemoryResult;

    public NLSMemoryQueryReturnNotConfirmedTest() {
        setDescription("[NLS_Japanese] Test memory query ignore indirection exception return not conformed option");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        queryAll = (ReadAllQuery)getSession().getDescriptor(org.eclipse.persistence.testing.tests.nls.japanese.NLSEmployee.class).getQueryManager().getQuery("memoryQueryReturnNotConfirmedQuery");
        allEmployees = (Vector)getSession().executeQuery(queryAll);

        queryObject = new ReadObjectQuery(NLSEmployee.class);
        queryObject.setSelectionCriteria(new ExpressionBuilder().get("address").get("city").notEqual("\u3059\u30db\u30bb\u30c8\u30c4\u30aa\u30a2\u30b7"));//"Montreal" converted to japanese
        employee = (NLSEmployee)getSession().executeQuery(queryObject);
        if (employee != null) {
            employee.getAddress();
        }

        //    queryObject = new ReadObjectQuery(Employee.class);
        queryObject.setSelectionCriteria(new ExpressionBuilder().get("address").get("city").equal("\u3059\u30db\u30bb\u30c8\u30c4\u30aa\u30a2\u30b7"));//"Montreal" converted to japanese
        employee = (NLSEmployee)getSession().executeQuery(queryObject);
        if (employee != null) {
            employee.getAddress();
        }
    }

    public void test() {
        ReadAllQuery queryAllCopy = (ReadAllQuery)queryAll.clone();
        queryAllCopy.checkCacheOnly();//read from cache only
        queryAllCopy.setSelectionCriteria(new ExpressionBuilder().get("address").get("city").notEqual("\u3059\u30db\u30bb\u30c8\u30c4\u30aa\u30a2\u30b7"));//"Montreal" converted to japanese
        inMemoryResult = (Vector)getSession().executeQuery(queryAllCopy);
    }

    public void verify() {
        if (this.inMemoryResult.size() != 1) {
            throw new TestErrorException("In Memory Query returned incorrect number of results. This is not expected to happen because of database changes");
        }
    }
}
