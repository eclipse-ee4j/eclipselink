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
package org.eclipse.persistence.testing.tests.nls.japanese;

import org.eclipse.persistence.testing.framework.*;
//import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.exceptions.*;

public class NLSMemoryQueryThrowExceptionTest extends AutoVerifyTestCase {
    protected ReadObjectQuery query;
    protected boolean exceptionThrown = false;

    public NLSMemoryQueryThrowExceptionTest() {
        setDescription("[NLS_Japanese] Test memory query throw indirection exception option");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        query = (ReadObjectQuery)getSession().getDescriptor(org.eclipse.persistence.testing.tests.nls.japanese.NLSEmployee.class).getQueryManager().getQuery("memoryQueryThrowExceptionQuery");
        getSession().executeQuery(query);
    }

    public void test() {
        ReadObjectQuery queryCopy = (ReadObjectQuery)query.clone();
        queryCopy.checkCacheOnly();//read from cache only
        queryCopy.setSelectionCriteria(new ExpressionBuilder().get("address").get("city").equal("\u3059\u30db\u30bb\u30c8\u30c4\u30aa\u30a2\u30b7"));//"Montreal" converted to japanese
        try {
            getSession().executeQuery(queryCopy);
        } catch (QueryException exception) {
            exceptionThrown = exception.getErrorCode() == QueryException.MUST_INSTANTIATE_VALUEHOLDERS;
        }
    }

    public void verify() {
        if (!exceptionThrown) {
            throw new TestErrorException("In Memory Query did not return all objects because of indirection");
        }
    }
}
