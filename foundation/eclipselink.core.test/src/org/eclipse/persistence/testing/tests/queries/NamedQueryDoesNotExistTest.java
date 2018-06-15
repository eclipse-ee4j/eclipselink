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

import java.util.*;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.testing.framework.TestException;
/*
* try to test correct exception will be thrown when named query doesn't exist
*/
public class NamedQueryDoesNotExistTest extends NamedQueriesUOWTest {

    public NamedQueryDoesNotExistTest() {
        setDescription("Verifies if a Named Query doesn't exist, "
                    + " correct exception will be thrown instead of NPE");
    }

    public void test(){
        try{
        Vector empsByFirstName = (Vector)uow.executeQuery("namedQueryDoesNotExist", new String("Jill"));
        }catch(QueryException e){
            if (e.getMessage().indexOf("Query named [namedQueryDoesNotExist] is not defined. Domain class") == -1){
                throw new TestException("wrong exception thrown");
            }
        }
    }
}
