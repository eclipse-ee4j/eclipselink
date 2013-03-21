/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - initial API and implementation from for JPA 2.0 criteria API
 ******************************************************************************/ 
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
 * @see ArgumentListFunctionExpression
 * @see Expression.coalesece()
 * @author tware
 *
 */
public class ListExpressionOperator extends ExpressionOperator {

    protected String[] startStrings = null;
    protected String[] separators = null;
    protected String[] terminationStrings = null;
    protected int numberOfItems = 0;
    protected boolean isComplete = false;
    
    public void copyTo(ExpressionOperator operator){
        super.copyTo(operator);
        if (operator instanceof ListExpressionOperator){
            ((ListExpressionOperator)operator).startStrings = Helper.copyStringArray(startStrings);
            ((ListExpressionOperator)operator).separators = Helper.copyStringArray(separators);
            ((ListExpressionOperator)operator).terminationStrings = Helper.copyStringArray(terminationStrings);
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
    public String[] getDatabaseStrings() {
        databaseStrings = new String[numberOfItems + 1];
        int i = 0;
        while (i < startStrings.length){
            databaseStrings[i] = startStrings[i];
            i++;
        }
        while  (i < numberOfItems - (terminationStrings.length - 1)){
            for (int j=0;j<separators.length;j++){
                databaseStrings[i] = separators[j];
                i++;
            }
        }
        while (i <= numberOfItems){
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
