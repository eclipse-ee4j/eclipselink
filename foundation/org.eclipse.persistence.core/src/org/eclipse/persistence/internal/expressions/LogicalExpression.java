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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.expressions;

import java.util.List;
import java.util.Set;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.helper.DatabaseField;
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
        if (this.operator.getSelector() == ExpressionOperator.And) {
            return this.firstChild.doesConform(object, session, translationRow, valueHolderPolicy, objectIsUnregistered) && this.secondChild.doesConform(object, session, translationRow, valueHolderPolicy, objectIsUnregistered);
        } else if (this.operator.getSelector() == ExpressionOperator.Or) {
            return this.firstChild.doesConform(object, session, translationRow, valueHolderPolicy, objectIsUnregistered) || this.secondChild.doesConform(object, session, translationRow, valueHolderPolicy, objectIsUnregistered);
        }

        throw QueryException.cannotConformExpression();

    }

    /**
     * INTERNAL:
     * Extract the values from the expression into the row.
     * Ensure that the query is querying the exact primary key.
     * Return false if not on the primary key.
     */
    @Override
    public boolean extractValues(boolean primaryKeyOnly, boolean requireExactMatch, ClassDescriptor descriptor, AbstractRecord primaryKeyRow, AbstractRecord translationRow) {
        // If this is a primary key expression then it can only have and/or relationships.
        if (this.operator.getSelector() != ExpressionOperator.And) {
            // If this is an exact primary key expression it can not have ors.
            // After fixing bug 2782991 this must now work correctly.
            if (requireExactMatch || (this.operator.getSelector() != ExpressionOperator.Or)) {
                return false;
            }
        }
        boolean validExpression = this.firstChild.extractValues(primaryKeyOnly, requireExactMatch, descriptor, primaryKeyRow, translationRow);
        if (requireExactMatch && (!validExpression)) {
            return false;
        }
        return this.secondChild.extractValues(primaryKeyOnly, requireExactMatch, descriptor, primaryKeyRow, translationRow);
    }

    /**
     * INTERNAL:
     * Return if the expression is not a valid primary key expression and add all primary key fields to the set.
     */
    @Override
    public boolean extractFields(boolean requireExactMatch, boolean primaryKey, ClassDescriptor descriptor, List<DatabaseField> searchFields, Set<DatabaseField> foundFields) {
        // If this is a primary key expression then it can only have and/or relationships.
        if (this.operator.getSelector() != ExpressionOperator.And) {
            // If this is an exact primary key expression it can not have ors.
            // After fixing bug 2782991 this must now work correctly.
            if (requireExactMatch || (this.operator.getSelector() != ExpressionOperator.Or)) {
                return false;
            }
        }
        boolean validExpression = this.firstChild.extractFields(requireExactMatch, primaryKey, descriptor, searchFields, foundFields);
        if (requireExactMatch && (!validExpression)) {
            return false;
        }
        return this.secondChild.extractFields(requireExactMatch, primaryKey, descriptor, searchFields, foundFields);
    }

    /**
     * INTERNAL:
     */
    public boolean isLogicalExpression() {
        return true;
    }
}
