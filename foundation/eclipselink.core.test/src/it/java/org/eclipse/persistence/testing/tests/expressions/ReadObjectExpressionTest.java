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
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.ReadObjectTest;

/**
 * Test expressions for reading objects.
 */
public class ReadObjectExpressionTest extends ReadObjectTest {
    Expression expression;
    /** The class of the target objects to be read from the database. */
    private Class referenceClass;

    public ReadObjectExpressionTest(Object theOriginalObject, Expression theExpression) {
        originalObject = theOriginalObject;
        expression = theExpression;
        if (theOriginalObject != null) {
            referenceClass = theOriginalObject.getClass();
        }
    }

    public ReadObjectExpressionTest(Class theReferenceClass, Expression theExpression) {
        referenceClass = theReferenceClass;
        expression = theExpression;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression theExpression) {
        expression = theExpression;
    }

    protected void setup() {
        // Access and DB2 do not support UPPER and LOWER
        if (getQuery() == null) {
            setQuery(new ReadObjectQuery());
            getQuery().setReferenceClass(referenceClass);
            getQuery().setSelectionCriteria(getExpression());
        }
    }
}
