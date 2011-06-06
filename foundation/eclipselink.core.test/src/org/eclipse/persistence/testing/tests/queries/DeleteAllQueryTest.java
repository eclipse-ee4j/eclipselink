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
package org.eclipse.persistence.testing.tests.queries;

import java.util.*;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.queries.DeleteAllQuery;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Tests the delete all query.
 * When the query is called with an empty Vector, nothing should be deleted
 * from the database.
 * CR# 4286 ... 2612556
 *
 * @author Guy Pelletier
 * @version 1.0 October 10/02
 *
 * Changed ailitche_ri_delete_all_query_050920
 * DeleteAllQuery now throws a specific exception in case
 * the objects were set (even as an empty  collection),
 * but the selection criteria hasn't been provided.
 */
public class DeleteAllQueryTest extends AutoVerifyTestCase {
  private Exception exception;
  
  public DeleteAllQueryTest() {}

  public void reset()  {	
    getSession().getIdentityMapAccessor().initializeIdentityMaps(); // clears the cache and stuff?
    getAbstractSession().rollbackTransaction();
  }

  protected void setup()  {	
    exception = null;
    getAbstractSession().beginTransaction();
    getSession().getIdentityMapAccessor().initializeIdentityMaps(); // clears the cache and stuff?

    // no need to setup any data ... database should already be populated
    // since we are in the feature test model which loads the employee demo
  }

  public void test () {
    DeleteAllQuery deleteQuery = new DeleteAllQuery();
    deleteQuery.setReferenceClass(Employee.class);
    deleteQuery.setObjects(new Vector()); // empty vector ... shouldn't delete anything

    // This should throw QueryException.DELETE_ALL_QUERY_SPECIFIES_OBJECTS_BUT_NOT_SELECTION_CRITERIA    // breaking the database integrity. Catch the error and check in verify().
    try {
      getSession().executeQuery(deleteQuery);
    } catch (Exception e) {
      exception = e;
    }
  }

  protected void verify()  {
    if (exception == null) {
      throw new TestErrorException("No exception has been thrown");
    } else {
      if(!(exception instanceof QueryException) || ((QueryException)exception).getErrorCode() != QueryException.DELETE_ALL_QUERY_SPECIFIES_OBJECTS_BUT_NOT_SELECTION_CRITERIA) {
        throw new TestErrorException("Wrong exception thrown: ", exception);
      }
    }
    
    Vector employees = getSession().readAllObjects(Employee.class);

    // Ensure that our delete all didn't wack all the employees from the database
    if (employees.size() == 0) {
      throw new TestErrorException("The delete all query test failed.  All the employees were deleted.");
    }
  }
}
