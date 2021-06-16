/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
