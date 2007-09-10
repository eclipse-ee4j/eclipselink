/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
