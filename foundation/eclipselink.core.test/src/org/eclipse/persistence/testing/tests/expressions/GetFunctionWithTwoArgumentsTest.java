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
package org.eclipse.persistence.testing.tests.expressions;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.expressions.*;

public class GetFunctionWithTwoArgumentsTest extends ReadAllExpressionTest {
    public GetFunctionWithTwoArgumentsTest(Class referenceClass, int originalObjectsSize) {
        super(referenceClass, originalObjectsSize);
    }

    public void setup() {
        if (this.getSession().getLogin().getPlatform().isOracle() || getSession().getLogin().getPlatform().isMySQL()) {

            ExpressionBuilder emp = new ExpressionBuilder();

            expression = emp.get("firstName").getFunction("CONCAT", " is cool!").equal("Sarah" + " is cool!");
            getQuery(true).setSelectionCriteria(expression);

            super.setup();
        } else {
            throw new TestWarningException("This test can only be done on Oracle");
        }
    }
}
