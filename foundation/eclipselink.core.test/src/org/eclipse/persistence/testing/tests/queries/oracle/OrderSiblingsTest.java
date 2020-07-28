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

public class OrderSiblingsTest extends HierarchicalQueryTest
{
  private Vector expected;
  public OrderSiblingsTest()
  {
    this.setName("OrderSiblingsTest");
    this.setDescription("Tests the use of the Order Siblings Clause");
  }
  public Vector expectedResults() {
    if(expected == null) {
      expected = new Vector();
      ExpressionBuilder expb = new ExpressionBuilder();
      Employee daveVadis = (Employee)getSession().readObject(Employee.class, expb.get("lastName").equal("Vadis"));
      Employee edwardWhite = (Employee)getSession().readObject(Employee.class, expb.get("lastName").equal("White"));
      Employee tracyChapman = (Employee)getSession().readObject(Employee.class, expb.get("lastName").equal("Chapman"));
      expected.addElement(daveVadis);
      addEmployee(expected, edwardWhite);
      addEmployee(expected, tracyChapman);
    }
    return expected;
  }
  public ReadAllQuery getQuery() {
    ReadAllQuery query = new ReadAllQuery(Employee.class);
    ExpressionBuilder expb = new ExpressionBuilder();
    Expression startWith = expb.get("firstName").equal("Dave").and(expb.get("lastName").equal("Vadis"));
    Expression connectBy = expb.get("managedEmployees");
    Vector order = new Vector();
    order.addElement(expb.get("firstName"));
    query.setHierarchicalQueryClause(startWith, connectBy, order);
    return query;
  }

}
