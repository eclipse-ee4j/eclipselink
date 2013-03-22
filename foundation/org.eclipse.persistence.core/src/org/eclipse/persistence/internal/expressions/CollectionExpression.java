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
        Object value = getLocalBase().getFieldValue(getValue(), getSession());
        printer.printList((Collection)value);
    }
    
    /**
     * INTERNAL:
     * Return the value for in memory comparison.
     * This is only valid for valueable expressions.
     */
    public Object valueFromObject(Object object, AbstractSession session, AbstractRecord translationRow, int valueHolderPolicy, boolean isObjectUnregistered) {
        if (value instanceof Collection) {
            Collection values = (Collection)value;
            Vector fieldValues = new Vector(values.size());
            for (Iterator iterator = values.iterator(); iterator.hasNext();) {
                Object value = iterator.next();
                if (value instanceof Expression){
                    value = ((Expression)value).valueFromObject(object, session, translationRow, valueHolderPolicy, isObjectUnregistered); 
                }else{
                    value = getLocalBase().getFieldValue(value, session);
                }
                fieldValues.add(value);
            }
            return fieldValues;            
        }
        
        return getLocalBase().getFieldValue(getValue(), session);
    }

    public void setLocalBase(Expression e) {
        super.setLocalBase(e);
        if (value instanceof Collection) {
            Collection values = (Collection)value;
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
        if (value instanceof Collection) {
            Collection values = (Collection)value;
            Vector newValues = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(values.size());
            for (Iterator iterator = values.iterator(); iterator.hasNext();) {
                Object val = iterator.next();
                if (val instanceof Expression){
                    newValues.add(((Expression)val).copiedVersionFrom(alreadyDone)); 
                } else {
                    newValues.add(val);
                }
            }
            value = newValues;
        }
    }
}
