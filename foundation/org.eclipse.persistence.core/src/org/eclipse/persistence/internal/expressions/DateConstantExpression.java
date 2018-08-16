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
//     tware - added as part of JPQL extensions for JPA 2.0
package org.eclipse.persistence.internal.expressions;

import org.eclipse.persistence.expressions.Expression;

/**
 * INTERNAL:
 * This expression represents a date represented in JDBC escape syntax, for instance, the String:
 * {d '1901-01-01'} can be used to represent a Date and use used only by the JPQL parser.
 *
 * This is different from a ConstantExpression because this value is never converted with the conversion
 * manager and is printed out as-is
 *
 * The only validation for this type of expression will be from the JDBC driver.
 *
 * @author tware
 *
 */
public class DateConstantExpression extends ConstantExpression {

    public DateConstantExpression() {
        super();
    }

    public DateConstantExpression(Object newValue, Expression baseExpression) {
        super(newValue, baseExpression);
    }

    /**
     * INTERNAL:
     * Used for debug printing.
     */
    public String descriptionOfNodeType() {
        return "DateConstant";
    }

    /**
     * INTERNAL:
     * Print SQL onto the stream, using the ExpressionPrinter for context
     */
    public void printSQL(ExpressionSQLPrinter printer) {
        printer.printString((String)this.value);
    }

}
