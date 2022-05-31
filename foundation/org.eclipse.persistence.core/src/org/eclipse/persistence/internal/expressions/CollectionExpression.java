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
//     Oracle - initial API and implementation from Oracle TopLink
//     IBM - Bug 537795: CASE THEN and ELSE scalar expression Constants should not be casted to CASE operand type
package org.eclipse.persistence.internal.expressions;

import java.util.Collection;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * Used for wrapping collection of values or expressions.
 */
public class CollectionExpression extends ConstantExpression {
    public CollectionExpression() {
    }
    public CollectionExpression(Object newValue, Expression baseExpression) {
        super(newValue, baseExpression);
    }

    public void printSQL(ExpressionSQLPrinter printer) {
        Object value = this.value;
        if(this.localBase != null) {
            value = this.localBase.getFieldValue(value, getSession());
        }
        printer.printList((Collection)value, this.canBind);
    }

    /**
     * INTERNAL:
     * Return the value for in memory comparison.
     * This is only valid for valueable expressions.
     */
    public Object valueFromObject(Object object, AbstractSession session, AbstractRecord translationRow, int valueHolderPolicy, boolean isObjectUnregistered) {
        if (this.value instanceof Collection) {
            Collection values = (Collection)this.value;
            Vector fieldValues = new Vector(values.size());
            for (Iterator iterator = values.iterator(); iterator.hasNext();) {
                Object value = iterator.next();
                if (value instanceof Expression){
                    value = ((Expression)value).valueFromObject(object, session, translationRow, valueHolderPolicy, isObjectUnregistered);
                } else if(this.localBase != null) {
                    value = this.localBase.getFieldValue(value, session);
                }
                fieldValues.add(value);
            }
            return fieldValues;
        }

        if(this.localBase != null) {
            return this.localBase.getFieldValue(this.value, session);
        }
        return this.value;
    }

    public void setLocalBase(Expression e) {
        super.setLocalBase(e);
        if (this.value instanceof Collection) {
            Collection values = (Collection)this.value;
            for (Iterator iterator = values.iterator(); iterator.hasNext();) {
                Object val = iterator.next();
                if (val instanceof Expression){
                    ((Expression)val).setLocalBase(e);
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Used for cloning.
     */
    protected void postCopyIn(Map alreadyDone) {
        super.postCopyIn(alreadyDone);
        if (this.value instanceof Collection) {
            Collection values = (Collection)this.value;
            Vector newValues = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(values.size());
            for (Iterator iterator = values.iterator(); iterator.hasNext();) {
                Object val = iterator.next();
                if (val instanceof Expression){
                    newValues.add(((Expression)val).copiedVersionFrom(alreadyDone));
                } else {
                    newValues.add(val);
                }
            }
            setValue(newValues);
        }
    }
}
