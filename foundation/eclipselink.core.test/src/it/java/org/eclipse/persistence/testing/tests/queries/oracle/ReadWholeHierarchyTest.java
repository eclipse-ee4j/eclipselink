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

public class ReadWholeHierarchyTest extends HierarchicalQueryTest
{
  public ReadWholeHierarchyTest()
  {
  }
  public Vector expectedResults;
  public Vector expectedResults() {
    if(expectedResults == null) {
      expectedResults = new Vector();
      Employee dave = (Employee)getSession().readObject(Employee.class, new ExpressionBuilder().get("lastName").equal("Vadis"));
      addEmployee(expectedResults, dave);
    }
    return expectedResults;
  }
  public ReadAllQuery getQuery() {
    ReadAllQuery raq = new ReadAllQuery(Employee.class);
    ExpressionBuilder expb = new ExpressionBuilder();
    Expression startWith = expb.get("firstName").equal("Dave").and(expb.get("lastName").equal("Vadis"));
    Expression connectBy = expb.get("managedEmployees");
    raq.setHierarchicalQueryClause(startWith, connectBy, null);
    return raq;
  }
}
