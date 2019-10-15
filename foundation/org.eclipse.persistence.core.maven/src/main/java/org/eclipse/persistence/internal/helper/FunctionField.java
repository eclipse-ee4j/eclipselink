/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.internal.helper;

import org.eclipse.persistence.expressions.Expression;

/**
 * INTERNAL:
 * <p><b>Purpose</b>:
 * Allow fields to have functions applied to them or be computed.<p>
 */
public class FunctionField extends DatabaseField {
    protected Expression expression;

    public FunctionField() {
        super();
    }

    /**
     * A unique field name should still be given to the function.
     */
    public FunctionField(String fieldName) {
        super(fieldName);
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }
}
