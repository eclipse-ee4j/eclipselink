/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2022 IBM Corporation. All rights reserved.
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
//     tware - initial API and implementation from for JPA 2.0 criteria API
package org.eclipse.persistence.expressions;

import org.eclipse.persistence.internal.helper.Helper;

/**
 * INTERNAL:
 * A ListExpressionOperator is used with an ArgumentListFunctionExpression.  It is capable
 * of expanding the number of arguments it can be an operator for.  It is built from a set
 * of start strings, a repeating set of separators and a set of termination strings
 *
 * It typically represents a database function that has a variable list of arguments
 *
 * e.g. COALESCE(arg1, arg2, arg3, .... argn)
 *
 * In the example above "COALESCE(" is the start string, "," is the separator and ")" is the
 * end string
 *
 * @see org.eclipse.persistence.internal.expressions.ArgumentListFunctionExpression
 * @see Expression#coalesce()
 * @author tware
 *
 */
public class ListExpressionOperator extends ExpressionOperator {

    protected String[] startStrings = null;
    protected String[] separators = null;
    protected String[] terminationStrings = null;
    @Deprecated
    protected int numberOfItems = 0;
    protected boolean changed = false;
    protected boolean isComplete = false;

    @Override
    public ExpressionOperator clone(){
        ListExpressionOperator clone = new ListExpressionOperator();
        this.copyTo(clone);
        return clone;
    }

    public void copyTo(ExpressionOperator operator){
        super.copyTo(operator);
        if(operator == null)
            return;

        if (operator instanceof ListExpressionOperator){
            ((ListExpressionOperator)operator).startStrings = Helper.copyStringArray(startStrings);
            ((ListExpressionOperator)operator).separators = Helper.copyStringArray(separators);
            ((ListExpressionOperator)operator).terminationStrings = Helper.copyStringArray(terminationStrings);
        }
    }

    /**
     * INTERNAL:
     * Recalculate the database strings each time this is called in
     * case one has been added.
     */
    @Deprecated
    @Override
    public String[] getDatabaseStrings() {
        return getDatabaseStrings(0);
    }

    /**
     * Returns an array of Strings that expects a query argument between each String in the array to form the Expression.
     * The array is built from the defined startStrings, separators, and terminationStrings.
     * Start strings and termination strings take precedence over separator strings.
     * 
     * The first defined start string will be added to the array.
     * All subsequent start strings are optional, meaning they will only be added to the array if there are argument spaces available.
     * 
     * The defined set of separator strings will be repeated, as a complete set, as long as there are argument spaces available.
     * 
     * The last defined termination string will be added to the array.
     * All antecedent termination strings are optional, meaning they will only be added to the array if there are argument spaces available.
     */
    @Override
    public String[] getDatabaseStrings(int arguments) {
        int i = 0;
        String[] databaseStrings = new String[(arguments == 0) ? 2 : arguments + 1];

        int start = (arguments < (startStrings.length)) ? databaseStrings.length - 1 : startStrings.length;
        for (int j = 0; j < start; j++) {
            databaseStrings[i] = startStrings[j];
            i++;
        }

        // '- 1' to save a spot for the guaranteed 1 terminator
        int separ = ((databaseStrings.length - start - 1) / separators.length);
        for (int j = 0; j < separ; j++) {
            for (int k = 0; k < separators.length; k++) {
                databaseStrings[i] = separators[k];
                i++;
            }
        }

        int termi = databaseStrings.length - (start + (separ * separators.length));
        for (int j = (terminationStrings.length - termi); j < terminationStrings.length; j++) {
            databaseStrings[i] = terminationStrings[j];
            i++;
        }
        return databaseStrings;
    }

    @Deprecated
    public int getNumberOfItems(){
        return numberOfItems;
    }

    @Deprecated
    public void setNumberOfItems(int numberOfItems){
        this.numberOfItems = numberOfItems;
    }

    public String[] getStartStrings() {
        return startStrings;
    }

    public void setStartString(String startString) {
        this.startStrings = new String[]{startString};
    }

    public void setStartStrings(String[] startStrings) {
        this.startStrings = startStrings;
    }

    public String[] getSeparators() {
        return separators;
    }

    public void setSeparator(String separator) {
        this.separators = new String[]{separator};
    }

    public void setSeparators(String[] separators) {
        this.separators = separators;
    }

    public String[] getTerminationStrings() {
        return terminationStrings;
    }

    public void setTerminationString(String terminationString) {
        this.terminationStrings = new String[]{terminationString};
    }

    public void setTerminationStrings(String[] terminationStrings){
        this.terminationStrings = terminationStrings;
    }

    @Deprecated
    public void incrementNumberOfItems(){
        numberOfItems++;
    }

    public void setIsComplete(boolean isComplete){
        this.isComplete = isComplete;
    }

    public boolean isComplete() {
        return isComplete;
    }
}
