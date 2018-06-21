/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.queries.oracle;

import java.util.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.mapping.*;
import org.eclipse.persistence.expressions.*;

public class HierarchicalQueryWithWhere extends HierarchicalQueryTest
{
  private Vector expected;
  public HierarchicalQueryWithWhere()
  {
    this.setName("HierarchicalQueryWithWhereClauseTest");
    this.setDescription("Tests the use of a Hierarchical Query with a Where Clause");
  }
  public Vector expectedResults() {
    if (expected == null) {

      expected = new Vector();
      Employee daveVadis = (Employee)getSession().readObject(Employee.class, new ExpressionBuilder().get("lastName").equal("Vadis"));
      addEmployee(expected, daveVadis);
      expected.removeElement(daveVadis);
    }
    return expected;
  }
  public ReadAllQuery getQuery() {
  //Create a query which returns all employees under Dave Vadis in Hierarchical
  //Order, but excludes Dave Vadis from the list
    ReadAllQuery query = new ReadAllQuery(Employee.class);
    ExpressionBuilder expb = new ExpressionBuilder();
    query.setSelectionCriteria(expb.get("firstName").notEqual("Dave").or(expb.get("lastName").notEqual("Vadis")));
    Expression startWith = expb.get("firstName").equal("Dave").and(expb.get("lastName").equal("Vadis"));
    Expression connectBy = expb.get("managedEmployees");
    query.setHierarchicalQueryClause(startWith, connectBy, null);
    return query;
  }
}
