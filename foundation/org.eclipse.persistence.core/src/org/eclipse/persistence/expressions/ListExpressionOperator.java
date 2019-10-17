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
//     tware - initial API and implementation from for JPA 2.0 criteria API
package org.eclipse.persistence.expressions;

import java.util.List;

import org.eclipse.persistence.internal.expressions.ArgumentListFunctionExpression;
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
 * <p>
 * <h2>Note</h2> This operator has an internal state, <tt>numberOfItems</tt>, which needs to be considered when caching parsed
 * queries. Therefore, {@link ArgumentListFunctionExpression#postCopyIn(java.util.Map)} needs to ensure that a new instance of the
 * operator is created when cloning a shared query.
 * <ul>
 * <li><a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=463042">https://bugs.eclipse.org/bugs/show_bug.cgi?id=463042</a></li>
 * <li><a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=382308">https://bugs.eclipse.org/bugs/show_bug.cgi?id=382308</a></li>
 * </ul>
 *
 * @see org.eclipse.persistence.internal.expressions.ArgumentListFunctionExpression
 * @see Expression#coalesce()
 * @author tware
 * @author patrick.haller@sap.com
 */
public class ListExpressionOperator extends ExpressionOperator {

    /** Quasi constant: not modified after initialization of operator instance */
    private String[] startStrings;

    /** Quasi constant: not modified after initialization of operator instance */
    private String[] separators;

    /** Quasi constant: not modified after initialization of operator instance */
    private String[] terminationStrings;

    /** volatile: number of items processed by this operator instance */
    private int numberOfItems = 0;

    private boolean isComplete = false;

    /**
     * Used to determine whether the database strings array needs to be recalculated, initialized to 0 by JVM
     */
    private int cachedNumberOfItems;

    /**
     * Cached array of database strings, initialized to null by JVM.
     */
    private String[] cachedDatabaseStrings;

    public void copyTo(ExpressionOperator operator)
    {
        // This has been verified to only operate on new ListExpressionOperator
        // instances, hence not susceptible to share volatile state between
        // threads.
        super.copyTo(operator);
        if (operator instanceof ListExpressionOperator)
        {
            // Quasi-constant strings for a given SQL operator (CASE, COALESCE, ...)
            ((ListExpressionOperator) operator).setStartStrings(Helper.copyStringArray(startStrings));
            ((ListExpressionOperator) operator).setSeparators(Helper.copyStringArray(separators));
            ((ListExpressionOperator) operator).setTerminationStrings(Helper.copyStringArray(terminationStrings));

            // don't copy numberOfItems since this copy method is used to duplicate an operator that
            // may have a different number of items
        }
    }

    /**
     * INTERNAL:
     * Recalculate the database strings each time this is called in
     * case one has been added.
     */
    @Override
    public String[] getDatabaseStrings()
    {
        final int _numberOfItems = numberOfItems;
        if (null == cachedDatabaseStrings || cachedNumberOfItems != _numberOfItems)
        {
            cachedDatabaseStrings = recalculateDatabaseStrings(_numberOfItems);
            cachedNumberOfItems = _numberOfItems;
        }

        return cachedDatabaseStrings;
    }

    /**
     * Calculate the <i>database strings</i>, based on the <tt>numberOfItems</tt> state.
     * 
     * @return the calculated "database strings", i. e. SQL literals that will be interleaved with expressions, to build the final
     *         SQL statement in the target database dialect.
     */
    private String[] recalculateDatabaseStrings(final int noOfItems)
    {
        // EJBQueryImpl.buildEJBQLDatabaseQuery() caches
        // unnamed, un-query-hinted queries, which is typical for dynamic
        // JPQL. This in turn leads to a shared state between threads as
        // ArgumentListFunctionExpression's postCopyIn cloning method
        // did not clone the ListExpressionOperator, but shared the very
        // same instance between multiple threads.
        //
        // This led to https://bugs.eclipse.org/bugs/show_bug.cgi?id=463042
        //
        // The only variables modified from the outside even after initialization
        // of a new object instance are:
        // - isComplete
        // - numberOfItems
        final String[] databaseStrings = new String[noOfItems + 1];
        int i = 0;
        while (i < startStrings.length){
            databaseStrings[i] = startStrings[i];
            i++;
        }
        while  (i < noOfItems - (terminationStrings.length - 1)){
            for (int j=0;j<separators.length;j++){
                databaseStrings[i] = separators[j];
                i++;
            }
        }
        while (i <= noOfItems){
            for (int j=0;j<terminationStrings.length;j++){
                databaseStrings[i] = terminationStrings[j];
                    i++;
            }
        }
        return databaseStrings;
    }
    
    public int getNumberOfItems(){
        return numberOfItems;
    }

    public void setNumberOfItems(int numberOfItems){
        this.numberOfItems = numberOfItems;
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

    public void setTerminationString(String terminationString) {
        this.terminationStrings = new String[]{terminationString};
    }

    public void setTerminationStrings(String[] terminationStrings){
        this.terminationStrings = terminationStrings;
    }

    public void incrementNumberOfItems(){
        numberOfItems++;
    }

    public void setIsComplete(boolean isComplete){
        this.isComplete = isComplete;
    }

    @Override
    public boolean isComplete() {
        return isComplete;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.persistence.expressions.ExpressionOperator#printsAs(java.util.List)
     */
    @Override
    public void printsAs(List<String> dbStrings)
    {
        // The parent class's side-effect modification of the externalized
        // databaseStrings array is unsupported.
        throw new UnsupportedOperationException("Use setters exclusively to modify ListExpressionOperator");
    }
}
