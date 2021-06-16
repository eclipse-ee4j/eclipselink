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
    @Override
    public String descriptionOfNodeType() {
        return "DateConstant";
    }

    /**
     * INTERNAL:
     * Print SQL onto the stream, using the ExpressionPrinter for context
     */
    @Override
    public void printSQL(ExpressionSQLPrinter printer) {
        printer.printString((String)this.value);
    }

}
