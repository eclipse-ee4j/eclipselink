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
package org.eclipse.persistence.testing.tests.expressions;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.framework.ReadObjectTest;

/**
 * Test expressions for reading objects.
 */
public class ReadObjectExpressionTest extends ReadObjectTest {
    Expression expression;
    /** The class of the target objects to be read from the database. */
    private Class<?> referenceClass;

    public ReadObjectExpressionTest(Object theOriginalObject, Expression theExpression) {
        originalObject = theOriginalObject;
        expression = theExpression;
        if (theOriginalObject != null) {
            referenceClass = theOriginalObject.getClass();
        }
    }

    public ReadObjectExpressionTest(Class<?> theReferenceClass, Expression theExpression) {
        referenceClass = theReferenceClass;
        expression = theExpression;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression theExpression) {
        expression = theExpression;
    }

    @Override
    protected void setup() {
        // Access and DB2 do not support UPPER and LOWER
        if (getQuery() == null) {
            setQuery(new ReadObjectQuery());
            getQuery().setReferenceClass(referenceClass);
            getQuery().setSelectionCriteria(getExpression());
        }
    }
}
