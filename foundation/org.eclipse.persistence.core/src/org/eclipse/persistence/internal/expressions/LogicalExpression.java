/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.expressions;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * Used for logical AND and OR.  This is not used by NOT.
 */
public class LogicalExpression extends CompoundExpression {

    public LogicalExpression() {
        super();
    }

    /**
     * INTERNAL:
     * Used for debug printing.
     */
    public String descriptionOfNodeType() {
        return "Logical";
    }

    /**
     * INTERNAL:
     * Check if the object conforms to the expression in memory.
     * This is used for in-memory querying.
     * If the expression in not able to determine if the object conform throw a not supported exception.
     */
    public boolean doesConform(Object object, AbstractSession session, AbstractRecord translationRow, int valueHolderPolicy, boolean objectIsUnregistered) {
        // This should always be and or or.
        if (getOperator().getSelector() == ExpressionOperator.And) {
            return getFirstChild().doesConform(object, session, translationRow, valueHolderPolicy, objectIsUnregistered) && getSecondChild().doesConform(object, session, translationRow, valueHolderPolicy, objectIsUnregistered);
        } else if (getOperator().getSelector() == ExpressionOperator.Or) {
            return getFirstChild().doesConform(object, session, translationRow, valueHolderPolicy, objectIsUnregistered) || getSecondChild().doesConform(object, session, translationRow, valueHolderPolicy, objectIsUnregistered);
        }

        throw QueryException.cannotConformExpression();

    }

    /**
     * INTERNAL:
     * Extract the primary key from the expression into the row.
     * Ensure that the query is querying the exact primary key.
     * Return false if not on the primary key.
     */
    public boolean extractPrimaryKeyValues(boolean requireExactMatch, ClassDescriptor descriptor, AbstractRecord primaryKeyRow, AbstractRecord translationRow) {
        // If this is a primary key expression then it can only have and/or relationships.
        if (getOperator().getSelector() != ExpressionOperator.And) {
            // If this is an exact primary key expression it can not have ors.
            // After fixing bug 2782991 this must now work correctly.
            if (requireExactMatch || (getOperator().getSelector() != ExpressionOperator.Or)) {
                return false;
            }
        }
        boolean validExpression = getFirstChild().extractPrimaryKeyValues(requireExactMatch, descriptor, primaryKeyRow, translationRow);
        if (requireExactMatch && (!validExpression)) {
            return false;
        }
        return getSecondChild().extractPrimaryKeyValues(requireExactMatch, descriptor, primaryKeyRow, translationRow);
    }

    /**
     * INTERNAL:
     */
    public boolean isLogicalExpression() {
        return true;
    }
}
