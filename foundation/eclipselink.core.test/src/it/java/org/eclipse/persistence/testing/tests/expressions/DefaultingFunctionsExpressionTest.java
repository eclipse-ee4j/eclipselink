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
package org.eclipse.persistence.testing.tests.expressions;

import org.eclipse.persistence.expressions.*;

public class DefaultingFunctionsExpressionTest extends ReadAllExpressionTest {

    /**
     * ReadAllExpressionTest constructor comment.
     * @param referenceClass java.lang.Class
     * @param originalObjectsSize int
     */
    public DefaultingFunctionsExpressionTest(Class referenceClass, int originalObjectsSize) {
        super(referenceClass, originalObjectsSize);
    }

    /**
     * Test that when the user uses getFunction(FOO) we will print it as a function
     * FOO, even though we've never heard of it before.
     * This is tricky to test, since we can break the test by including function support,
     * so use a function we do support, but invoke it by database selector (LOWER)rather than
     * Java selector (toLowerCase)
     */
    public void setup() {
        // This is slightly tricky. We use the same function as toLowerCase,
        // but get the platform's name for it.
        ExpressionOperator op = getSession().getLogin().getPlatform().getOperator(ExpressionOperator.ToLowerCase);
        String lowerCaseFunctionName;
        if (op == null) {
            lowerCaseFunctionName = "LOWER";
        } else {
            String withBracket = op.getDatabaseStrings()[0];
            lowerCaseFunctionName = withBracket.substring(0, withBracket.length() - 1);
        }

        ExpressionBuilder emp = new ExpressionBuilder();
        expression = emp.get("firstName").getFunction(lowerCaseFunctionName).equal("sarah");
        getQuery(true).setSelectionCriteria(expression);

        super.setup();
    }
}
