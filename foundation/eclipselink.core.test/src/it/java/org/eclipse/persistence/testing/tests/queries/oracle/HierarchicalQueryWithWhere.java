/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.models.mapping.Employee;

import java.util.Vector;

public class HierarchicalQueryWithWhere extends HierarchicalQueryTest
{
  private Vector expected;
  public HierarchicalQueryWithWhere()
  {
    this.setName("HierarchicalQueryWithWhereClauseTest");
    this.setDescription("Tests the use of a Hierarchical Query with a Where Clause");
  }
  @Override
  public Vector expectedResults() {
    if (expected == null) {

      expected = new Vector();
      Employee daveVadis = (Employee)getSession().readObject(Employee.class, new ExpressionBuilder().get("lastName").equal("Vadis"));
      addEmployee(expected, daveVadis);
      expected.remove(daveVadis);
    }
    return expected;
  }
  @Override
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
