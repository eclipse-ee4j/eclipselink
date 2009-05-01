/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.mappings.querykeys;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.expressions.DataExpression;
import org.eclipse.persistence.internal.expressions.ExpressionIterator;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.internal.expressions.TableExpression;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;


/** <p>
 * <b>Purpose</b>:Represents a m-m join query.
 */
public class ManyToManyQueryKey extends ForeignReferenceQueryKey {

    /**
     * INTERNAL:
     * override the isCollectionQueryKey() method in the superclass to return true.
     * @return boolean
     */
    public boolean isCollectionQueryKey() {
        return true;
    }

    /**
     * INTERNAL:
     * override the isManyToManyQueryKey() method in the superclass to return true.
     * @return boolean
     */
    public boolean isManyToManyQueryKey() {
        return true;
    }
    
    /**
     * PUBLIC:
     * Returns the reference table.
     */
    public DatabaseTable getRelationTable(ClassDescriptor referenceDescriptor) {
        ExpressionIterator expIterator = new ExpressionIterator() {
            public void iterate(Expression each) {
                if(each.isTableExpression()) {
                    ((Collection)this.getResult()).add(((TableExpression)each).getTable());
                }
                else if(each.isDataExpression()) {
                    DatabaseField field = ((DataExpression)each).getField();
                    if(field != null && field.hasTableName()) {
                        ((Collection)this.getResult()).add(field.getTable());
                    }
                } else if(each.isParameterExpression()) {
                    DatabaseField field = ((ParameterExpression)each).getField();
                    if(field != null && field.hasTableName()) {
                        ((Collection)this.getResult()).add(field.getTable());
                    }
                }
            }
        };
        
        expIterator.setResult(new HashSet());
        expIterator.iterateOn(this.joinCriteria);
        HashSet<DatabaseTable> tables = (HashSet)expIterator.getResult();
        
        DatabaseTable relationTable = null;
        Iterator<DatabaseTable> it = tables.iterator();
        while(it.hasNext()) {
            DatabaseTable table = it.next();
            // neither source nor reference descriptor contains table - must be relationTable
            if(!descriptor.getTables().contains(table) && !referenceDescriptor.getTables().contains(table)) {
                relationTable = table;
                break;
            }
        }
        if(relationTable != null) {
            return relationTable;
        } else {
            throw QueryException.noRelationTableInManyToManyQueryKey(this, this.joinCriteria);        
        }
    }
}