/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.queries.oracle;

import java.util.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.mapping.*;
import org.eclipse.persistence.expressions.*;

public class HierarchicalOneToOneTest extends HierarchicalQueryTest 
{
  public HierarchicalOneToOneTest()
  {
  }
  public Vector expectedResults() {
    Vector v = new Vector();
    Employee norman = (Employee)getSession().readObject(Employee.class, new ExpressionBuilder().get("firstName").equal("Norman"));
    v.addElement(norman);
    Employee manager = norman.getManager();
    while(manager != null) {
      v.addElement(manager);
      manager = manager.getManager();
    }
    return v;
  }
  public ReadAllQuery getQuery() {
    ReadAllQuery query = new ReadAllQuery(Employee.class);
    ExpressionBuilder builder = new ExpressionBuilder();
    Expression startWith = builder.get("firstName").equal("Norman");
    Expression connectBy = builder.get("manager");
    query.setHierarchicalQueryClause(startWith, connectBy, null);
    return query;
  }

}
