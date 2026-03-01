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
import org.eclipse.persistence.testing.models.inheritance.Engineer;
import org.eclipse.persistence.testing.models.inheritance.Person;

import java.util.Vector;

public class HierarchicalQueryWithInheritenceTest extends HierarchicalQueryTest
{
  public HierarchicalQueryWithInheritenceTest()
  {
  }
  @Override
  public void setup() {
  }
  @Override
  public void reset() {
  }
  @Override
  public ReadAllQuery getQuery() {
    ReadAllQuery query = new ReadAllQuery(Engineer.class);
    ExpressionBuilder builder = new ExpressionBuilder();
    Expression startWith = builder.get("name").equal("Steve");
    Expression connectBy = builder.get("bestFriend");
    query.setHierarchicalQueryClause(startWith, connectBy, null);
    return query;
  }
  @Override
  public Vector expectedResults() {
    Person p = (Person)getSession().readObject(Engineer.class, new ExpressionBuilder().get("name").equal("Steve"));
    Vector result = new Vector();
    result.add(p);
    p = p.bestFriend;
    while ((p != null) && (!result.contains(p))) {
      result.add(p);
      p = p.bestFriend;
    }
    return result;
  }
}
