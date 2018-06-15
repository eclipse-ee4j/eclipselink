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
package org.eclipse.persistence.testing.tests.queries.oracle;

import java.util.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.mapping.*;
import org.eclipse.persistence.expressions.*;

public class JoinInWhereClauseTest extends HierarchicalQueryTest
{
  private Vector expected;
  public JoinInWhereClauseTest()
  {
    setName("JoinInWhereClauseTest");
    setDescription("Tests the use of a hierarchical query with a join in the where clause");

  }
  public Vector expectedResults() {
    if(expected == null) {
      expected = new Vector();
      ExpressionBuilder expb = new ExpressionBuilder();
      expected.addElement(getSession().readObject(Employee.class, expb.get("lastName").equal("White")));
      expected.addElement(getSession().readObject(Employee.class, expb.get("lastName").equal("Rue")));

    }
    return expected;
  }
  public ReadAllQuery getQuery() {
  //A query to read the hierarchy starting with Edward White, but excluding
  //anyone who reports to Tracy Rue and below.
    ReadAllQuery raq = new ReadAllQuery(Employee.class);
    ExpressionBuilder expb = new ExpressionBuilder();
    raq.setSelectionCriteria(expb.get("manager").get("lastName").notEqual("Rue"));
    Expression startWith = expb.get("lastName").equal("White");
    Expression connectBy = expb.get("managedEmployees");
    raq.setHierarchicalQueryClause(startWith, connectBy, null);
    return raq;
  }
}
