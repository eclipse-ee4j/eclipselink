/*
 * Copyright (c) 2022, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     07/29/2022-4.0.0 Tomas Kraus - 1573: Fixed types returned by JPQL EXTRACT()
package org.eclipse.persistence.internal.expressions;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.helper.ClassConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Expression operator customization for {@code EXTRACT(<date-time-part> FROM <date-time>)}.
 * Contains support for platform specific native SQL override for individual {@code <date-time-part>}
 * values.
 */
public class ExtractOperator extends ExpressionOperator {

    // Default native database Strings to be printed for EXTRACT expression
    private static List<String> defaultDbStrings() {
        final List<String> dbStrings = new ArrayList<>(5);
        dbStrings.add("EXTRACT(");
        dbStrings.add(" FROM ");
        dbStrings.add(")");
        return dbStrings;
    }

    /**
     * Creates an instance of {@link ExtractOperator} expression.
     * Default native database Strings are set for EXTRACT expression.
     */
    public ExtractOperator() {
        this(defaultDbStrings());
    }

    /**
     * Creates an instance of {@link ExtractOperator} expression.
     * Custom native database Strings are set for EXTRACT expression.
     *
     * @param dbStrings native database Strings
     */
    protected ExtractOperator(List<String> dbStrings) {
        this.setType(ExpressionOperator.FunctionOperator);
        this.setSelector(ExpressionOperator.Extract);
        this.setName("EXTRACT");
        this.printsAs(dbStrings);
        this.setArgumentIndices(new int[] {1, 0});
        this.bePrefix();
        this.setNodeClass(ClassConstants.FunctionExpression_Class);
    }

    /**
     * Default SQL printer for {@code EXTRACT(<date-time-part> FROM <date-time>)}.
     * It expects 2 arguments (first, second), argument indices array of size 2
     * and 3 database Strings (prefix, arguments separator and suffix).
     * Indices swap first and second expression order by default.
     *
     * @param first first expression ({@code <date-time>} argument)
     * @param second second expression ({@code <date-time-part>} argument)
     * @param printer target printer
     */
    private void printPartSql(final Expression first, Expression second, final ExpressionSQLPrinter printer) {
        if (printer.getPlatform().isDynamicSQLRequiredForFunctions() && Boolean.FALSE.equals(isBindingSupported())) {
            printer.getCall().setUsesBinding(false);
        }
        final int[] indices = getArgumentIndices(2);
        final String[] dbString = getDatabaseStrings();
        int dbStringPos = 0;
        // Print 1st database String (prefix) if defined
        if (isPrefix() && dbString.length > dbStringPos) {
            printer.printString(dbString[dbStringPos++]);
        }
        // Print first or second argument depending on indices
        if (indices[0] == 0) {
            first.printSQL(printer);
        }
        if (indices[0] == 1) {
            second.printSQL(printer);
        }
        // Print 2nd database String (arguments separator) if defined
        if (dbString.length > dbStringPos) {
            printer.printString(dbString[dbStringPos++]);
        }
        // Print first or second argument depending on indices
        if (indices[1] == 0) {
            first.printSQL(printer);
        }
        if (indices[1] == 1) {
            second.printSQL(printer);
        }
        // Print 3rd database String (suffix) if defined
        if (dbString.length > dbStringPos) {
            printer.printString(dbString[dbStringPos]);
        }
    }

    /**
     * Printer for YEAR ({@code <date-time-part>} argument.
     * This method shall be overriden to implement platform specific SQL printer.
     *
     * @param first first expression ({@code <date-time>} argument)
     * @param second second expression ({@code <date-time-part>} argument)
     * @param printer target printer
     */
    protected void printYearSQL(final Expression first, Expression second, final ExpressionSQLPrinter printer) {
        printPartSql(first, second, printer);
    }

    /**
     * Printer for QUARTER ({@code <date-time-part>} argument.
     * This method shall be overriden to implement platform specific SQL printer.
     *
     * @param first first expression ({@code <date-time>} argument)
     * @param second second expression ({@code <date-time-part>} argument)
     * @param printer target printer
     */
    protected void printQuarterSQL(final Expression first, Expression second, final ExpressionSQLPrinter printer) {
        printPartSql(first, second, printer);
    }

    /**
     * Printer for MONTH ({@code <date-time-part>} argument.
     * This method shall be overriden to implement platform specific SQL printer.
     *
     * @param first first expression ({@code <date-time>} argument)
     * @param second second expression ({@code <date-time-part>} argument)
     * @param printer target printer
     */
    protected void printMonthSQL(final Expression first, Expression second, final ExpressionSQLPrinter printer) {
        printPartSql(first, second, printer);
    }

    /**
     * Printer for WEEK ({@code <date-time-part>} argument.
     * This method shall be overriden to implement platform specific SQL printer.
     *
     * @param first first expression ({@code <date-time>} argument)
     * @param second second expression ({@code <date-time-part>} argument)
     * @param printer target printer
     */
    protected void printWeekSQL(final Expression first, Expression second, final ExpressionSQLPrinter printer) {
        printPartSql(first, second, printer);
    }

    /**
     * Printer for DAY ({@code <date-time-part>} argument.
     * This method shall be overriden to implement platform specific SQL printer.
     *
     * @param first first expression ({@code <date-time>} argument)
     * @param second second expression ({@code <date-time-part>} argument)
     * @param printer target printer
     */
    protected void printDaySQL(final Expression first, Expression second, final ExpressionSQLPrinter printer) {
        printPartSql(first, second, printer);
    }

    /**
     * Printer for HOUR ({@code <date-time-part>} argument.
     * This method shall be overriden to implement platform specific SQL printer.
     *
     * @param first first expression ({@code <date-time>} argument)
     * @param second second expression ({@code <date-time-part>} argument)
     * @param printer target printer
     */
    protected void printHourSQL(final Expression first, Expression second, final ExpressionSQLPrinter printer) {
        printPartSql(first, second, printer);
    }

    /**
     * Printer for MINUTE ({@code <date-time-part>} argument.
     * This method shall be overriden to implement platform specific SQL printer.
     *
     * @param first first expression ({@code <date-time>} argument)
     * @param second second expression ({@code <date-time-part>} argument)
     * @param printer target printer
     */
    protected void printMinuteSQL(final Expression first, Expression second, final ExpressionSQLPrinter printer) {
        printPartSql(first, second, printer);
    }

    /**
     * Printer for SECOND ({@code <date-time-part>} argument.
     * This method shall be overriden to implement platform specific SQL printer.
     *
     * @param first first expression ({@code <date-time>} argument)
     * @param second second expression ({@code <date-time-part>} argument)
     * @param printer target printer
     */
    protected void printSecondSQL(final Expression first, Expression second, final ExpressionSQLPrinter printer) {
        printPartSql(first, second, printer);
    }

    /**
     * Printer for DATE ({@code <date-time-part>} argument.
     * This method shall be overriden to implement platform specific SQL printer.
     *
     * @param first first expression ({@code <date-time>} argument)
     * @param second second expression ({@code <date-time-part>} argument)
     * @param printer target printer
     */
    protected void printDateSQL(final Expression first, Expression second, final ExpressionSQLPrinter printer) {
        printPartSql(first, second, printer);
    }

    /**
     * Printer for TIME ({@code <date-time-part>} argument.
     * This method shall be overriden to implement platform specific SQL printer.
     *
     * @param first first expression ({@code <date-time>} argument)
     * @param second second expression ({@code <date-time-part>} argument)
     * @param printer target printer
     */
    protected void printTimeSQL(final Expression first, Expression second, final ExpressionSQLPrinter printer) {
        printPartSql(first, second, printer);
    }

    @Override
    public void printDuo(Expression first, Expression second, ExpressionSQLPrinter printer) {
        if (second.isLiteralExpression()) {
            switch (((LiteralExpression) second).getValue().toUpperCase()) {
                case "YEAR":
                    printYearSQL(first, second, printer);
                    return;
                case "QUARTER":
                    printQuarterSQL(first, second, printer);
                    return;
                case "MONTH":
                    printMonthSQL(first, second, printer);
                    return;
                case "WEEK":
                    printWeekSQL(first, second, printer);
                    return;
                case "DAY":
                    printDaySQL(first, second, printer);
                    return;
                case "HOUR":
                    printHourSQL(first, second, printer);
                    return;
                case "MINUTE":
                    printMinuteSQL(first, second, printer);
                    return;
                case "SECOND":
                    printSecondSQL(first, second, printer);
                    return;
                case "DATE":
                    printDateSQL(first, second, printer);
                    return;
                case "TIME":
                    printTimeSQL(first, second, printer);
                    return;
                default:
                    throw new IllegalArgumentException("Unknown EXTRACT function datetime_field: "
                            + ((LiteralExpression) second).getValue().toUpperCase());
            }
        }
        super.printDuo(first, second, printer);
    }

    @Override
    public void printCollection(List<Expression> items, ExpressionSQLPrinter printer) {
        if (items.size() == 2) {
            final Expression second = items.get(1);
            if (second.isLiteralExpression()) {
                switch (((LiteralExpression) second).getValue().toUpperCase()) {
                    case "YEAR":
                        printYearSQL(items.get(0), second, printer);
                        return;
                    case "QUARTER":
                        printQuarterSQL(items.get(0), second, printer);
                        return;
                    case "MONTH":
                        printMonthSQL(items.get(0), second, printer);
                        return;
                    case "WEEK":
                        printWeekSQL(items.get(0), second, printer);
                        return;
                    case "DAY":
                        printDaySQL(items.get(0), second, printer);
                        return;
                    case "HOUR":
                        printHourSQL(items.get(0), second, printer);
                        return;
                    case "MINUTE":
                        printMinuteSQL(items.get(0), second, printer);
                        return;
                    case "SECOND":
                        printSecondSQL(items.get(0), second, printer);
                        return;
                    case "DATE":
                        printDateSQL(items.get(0), second, printer);
                        return;
                    case "TIME":
                        printTimeSQL(items.get(0), second, printer);
                        return;
                    default:
                        throw new IllegalArgumentException("Unknown EXTRACT function datetime_field: "
                                + ((LiteralExpression) second).getValue().toUpperCase());
                }
            }
        }
        super.printCollection(items, printer);
    }

    /**
     * Default Java printer for {@code EXTRACT(<date-time-part> FROM <date-time>)}.
     * It expects 2 arguments (first, second), argument indices array of size 2
     * and 3 database Strings (prefix, arguments separator and suffix).
     * Indices swap first and second expression order by default.
     *
     * @param first first expression ({@code <date-time>} argument)
     * @param second second expression ({@code <date-time-part>} argument)
     * @param printer target printer
     */
    private void printPartJava(final Expression first, Expression second, final ExpressionJavaPrinter printer) {
        final int[] indices = getArgumentIndices(2);
        final String[] dbString = getDatabaseStrings();
        // Print prefix from database Strings
        if (isPrefix()) {
            printer.printString(dbString[0]);
        }
        // Print first or second argument depending on indices
        if (indices[0] == 0) {
            first.printJava(printer);
        }
        if (indices[0] == 1) {
            second.printJava(printer);
        }
        // Print 2nd database String if defined
        if (dbString.length > 1) {
            printer.printString(dbString[1]);
        }
        // Print first or second argument depending on indices
        if (indices[1] == 0) {
            first.printJava(printer);
        }
        if (indices[1] == 1) {
            second.printJava(printer);
        }
        // Print 3rd database String if defined
        if (dbString.length > 2) {
            printer.printString(dbString[2]);
        }
    }

    /**
     * Printer for YEAR ({@code <date-time-part>} argument.
     * This method shall be overriden to implement platform specific Java printer.
     *
     * @param first first expression ({@code <date-time>} argument)
     * @param second second expression ({@code <date-time-part>} argument)
     * @param printer target printer
     */
    protected void printYearJava(final Expression first, Expression second, final ExpressionJavaPrinter printer) {
        printPartJava(first, second, printer);
    }

    /**
     * Printer for QUARTER ({@code <date-time-part>} argument.
     * This method shall be overriden to implement platform specific Java printer.
     *
     * @param first first expression ({@code <date-time>} argument)
     * @param second second expression ({@code <date-time-part>} argument)
     * @param printer target printer
     */
    protected void printQuarterJava(final Expression first, Expression second, final ExpressionJavaPrinter printer) {
        printPartJava(first, second, printer);
    }

    /**
     * Printer for MONTH ({@code <date-time-part>} argument.
     * This method shall be overriden to implement platform specific Java printer.
     *
     * @param first first expression ({@code <date-time>} argument)
     * @param second second expression ({@code <date-time-part>} argument)
     * @param printer target printer
     */
    protected void printMonthJava(final Expression first, Expression second, final ExpressionJavaPrinter printer) {
        printPartJava(first, second, printer);
    }

    /**
     * Printer for WEEK ({@code <date-time-part>} argument.
     * This method shall be overriden to implement platform specific Java printer.
     *
     * @param first first expression ({@code <date-time>} argument)
     * @param second second expression ({@code <date-time-part>} argument)
     * @param printer target printer
     */
    protected void printWeekJava(final Expression first, Expression second, final ExpressionJavaPrinter printer) {
        printPartJava(first, second, printer);
    }

    /**
     * Printer for DAY ({@code <date-time-part>} argument.
     * This method shall be overriden to implement platform specific Java printer.
     *
     * @param first first expression ({@code <date-time>} argument)
     * @param second second expression ({@code <date-time-part>} argument)
     * @param printer target printer
     */
    protected void printDayJava(final Expression first, Expression second, final ExpressionJavaPrinter printer) {
        printPartJava(first, second, printer);
    }

    /**
     * Printer for HOUR ({@code <date-time-part>} argument.
     * This method shall be overriden to implement platform specific Java printer.
     *
     * @param first first expression ({@code <date-time>} argument)
     * @param second second expression ({@code <date-time-part>} argument)
     * @param printer target printer
     */
    protected void printHourJava(final Expression first, Expression second, final ExpressionJavaPrinter printer) {
        printPartJava(first, second, printer);
    }

    /**
     * Printer for MINUTE ({@code <date-time-part>} argument.
     * This method shall be overriden to implement platform specific Java printer.
     *
     * @param first first expression ({@code <date-time>} argument)
     * @param second second expression ({@code <date-time-part>} argument)
     * @param printer target printer
     */
    protected void printMinuteJava(final Expression first, Expression second, final ExpressionJavaPrinter printer) {
        printPartJava(first, second, printer);
    }

    /**
     * Printer for SECOND ({@code <date-time-part>} argument.
     * This method shall be overriden to implement platform specific Java printer.
     *
     * @param first first expression ({@code <date-time>} argument)
     * @param second second expression ({@code <date-time-part>} argument)
     * @param printer target printer
     */
    protected void printSecondJava(final Expression first, Expression second, final ExpressionJavaPrinter printer) {
        printPartJava(first, second, printer);
    }

    /**
     * Printer for DATE ({@code <date-time-part>} argument.
     * This method shall be overriden to implement platform specific Java printer.
     *
     * @param first first expression ({@code <date-time>} argument)
     * @param second second expression ({@code <date-time-part>} argument)
     * @param printer target printer
     */
    protected void printDateJava(final Expression first, Expression second, final ExpressionJavaPrinter printer) {
        printPartJava(first, second, printer);
    }

    /**
     * Printer for TIME ({@code <date-time-part>} argument.
     * This method shall be overriden to implement platform specific Java printer.
     *
     * @param first first expression ({@code <date-time>} argument)
     * @param second second expression ({@code <date-time-part>} argument)
     * @param printer target printer
     */
    protected void printTimeJava(final Expression first, Expression second, final ExpressionJavaPrinter printer) {
        printPartJava(first, second, printer);
    }

    @Override
    public void printJavaDuo(Expression first, Expression second, ExpressionJavaPrinter printer) {
        if (second.isLiteralExpression()) {
            switch (((LiteralExpression) second).getValue().toUpperCase()) {
                case "YEAR":
                    printYearJava(first, second, printer);
                    return;
                case "QUARTER":
                    printQuarterJava(first, second, printer);
                    return;
                case "MONTH":
                    printMonthJava(first, second, printer);
                    return;
                case "WEEK":
                    printWeekJava(first, second, printer);
                    return;
                case "DAY":
                    printDayJava(first, second, printer);
                    return;
                case "HOUR":
                    printHourJava(first, second, printer);
                    return;
                case "MINUTE":
                    printMinuteJava(first, second, printer);
                    return;
                case "SECOND":
                    printSecondJava(first, second, printer);
                    return;
                case "DATE":
                    printDateJava(first, second, printer);
                    return;
                case "TIME":
                    printTimeJava(first, second, printer);
                    return;
                default:
                    throw new IllegalArgumentException("Unknown EXTRACT function datetime_field: "
                            + ((LiteralExpression) second).getValue().toUpperCase());
            }
        }
        super.printJavaDuo(first, second, printer);
    }

    @Override
    public void printJavaCollection(List<Expression> items, ExpressionJavaPrinter printer) {
        if (items.size() == 2) {
            final Expression second = items.get(1);
            if (second.isLiteralExpression()) {
                switch (((LiteralExpression) second).getValue().toUpperCase()) {
                    case "YEAR":
                        printYearJava(items.get(0), second, printer);
                        return;
                    case "QUARTER":
                        printQuarterJava(items.get(0), second, printer);
                        return;
                    case "MONTH":
                        printMonthJava(items.get(0), second, printer);
                        return;
                    case "WEEK":
                        printWeekJava(items.get(0), second, printer);
                        return;
                    case "DAY":
                        printDayJava(items.get(0), second, printer);
                        return;
                    case "HOUR":
                        printHourJava(items.get(0), second, printer);
                        return;
                    case "MINUTE":
                        printMinuteJava(items.get(0), second, printer);
                        return;
                    case "SECOND":
                        printSecondJava(items.get(0), second, printer);
                        return;
                    case "DATE":
                        printDateJava(items.get(0), second, printer);
                        return;
                    case "TIME":
                        printTimeJava(items.get(0), second, printer);
                        return;
                    default:
                        throw new IllegalArgumentException("Unknown EXTRACT function datetime_field: "
                                + ((LiteralExpression) second).getValue().toUpperCase());
                }
            }
        }
        super.printJavaCollection(items, printer);
    }

}
