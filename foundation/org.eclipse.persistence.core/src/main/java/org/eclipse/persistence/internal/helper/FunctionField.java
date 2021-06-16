/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
