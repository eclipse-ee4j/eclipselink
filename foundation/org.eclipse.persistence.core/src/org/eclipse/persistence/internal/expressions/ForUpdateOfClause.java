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

import java.util.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.queries.ObjectBuildingQuery;

/**
 * <b>Purpose:</b>Represents The FOR UPDATE OF fine-grained pessimistically
 * locking clause.
 *  @author  Stephen McRitchie
 *  @since   Oracle Toplink 10g AS
 */
public class ForUpdateOfClause extends ForUpdateClause {
    protected Vector lockedExpressions;

    public ForUpdateOfClause() {
    }

    public void addLockedExpression(ObjectExpression expression) {
        getLockedExpressions().addElement(expression);
    }

    public void addLockedExpression(FieldExpression expression) {
        getLockedExpressions().addElement(expression);
    }

    public Vector getLockedExpressions() {
        if (lockedExpressions == null) {
            lockedExpressions = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
        }
        return lockedExpressions;
    }

    public boolean isForUpdateOfClause() {
        return true;
    }

    public boolean isReferenceClassLocked() {
        if (lockedExpressions == null) {
            return false;
        }

        // Normally the expressionBuilder is stored first but not necessarily 
        // when a child ForUpdateOfClause is built for a nested query, or if a 
        //user made this clause.
        for (int i = 0; i < lockedExpressions.size(); i++) {
            if (((Expression)lockedExpressions.elementAt(i)).isExpressionBuilder()) {
                return true;
            }
        }
        return false;
    }

    public void setLockedExpressions(Vector lockedExpressions) {
        this.lockedExpressions = lockedExpressions;
    }

    public void setLockMode(short lockMode) {
        this.lockMode = lockMode;
    }

    /**
     * INTERNAL:
     * Prints the as of clause for an expression inside of the FROM clause.
     */
    public void printSQL(ExpressionSQLPrinter printer, SQLSelectStatement statement) {
        // assert(lockedExpressions != null && lockedExpressions.size() > 0);
        // assert(	getLockMode() == ObjectBuildingQuery.LOCK || 
        //			getLockMode() == ObjectBuildingQuery.LOCK_NOWAIT);
        if(printer.getSession().getPlatform().shouldPrintLockingClauseAfterWhereClause()) {
            ExpressionBuilder clonedBuilder = statement.getBuilder();
    
            printer.printString(printer.getSession().getPlatform().getSelectForUpdateOfString());
    
            printer.setIsFirstElementPrinted(false);
            for (Enumeration enumtr = getLockedExpressions().elements(); enumtr.hasMoreElements();) {
                Expression next = (Expression)enumtr.nextElement();
                // Necessary as this was determined in query framework.
                next = (Expression)next.rebuildOn(clonedBuilder);
                if(next.isObjectExpression()) {
                    ObjectExpression objectExp = (ObjectExpression)next;        
                    objectExp.writeForUpdateOfFields(printer, statement);
                } else {
                    // must be FieldExpression
                    FieldExpression fieldExp = (FieldExpression)next;
                    fieldExp.writeForUpdateOf(printer, statement);
                }
            }
            if (lockMode == ObjectBuildingQuery.LOCK_NOWAIT) {
                printer.printString(printer.getSession().getPlatform().getNoWaitString());
            }
        } else {
            super.printSQL(printer, statement);
        }
    }
    
    /**
     * INTERNAL:
     * Returns collection of aliases of the tables to be locked.
     * Only used by platforms that lock tables individually in FROM clause
     * (platform.shouldPrintLockingClauseAfterWhereClause()==false)
     * like SQLServer
     */
    public Collection getAliasesOfTablesToBeLocked(SQLSelectStatement statement) {
        int expected = statement.getTableAliases().size();
        HashSet aliases = new HashSet(expected);
        ExpressionBuilder clonedBuilder = statement.getBuilder();
        Enumeration enumtr = getLockedExpressions().elements();
        while (enumtr.hasMoreElements() && aliases.size() < expected) {
            Expression next = (Expression)enumtr.nextElement();

            // Necessary as this was determined in query framework.
            next = (Expression)next.rebuildOn(clonedBuilder);
            // next is either ObjectExpression or FieldExpression
            if(next.isFieldExpression()) {
                next = ((FieldExpression)next).getBaseExpression();
            }
            DatabaseTable[] expAliases = next.getTableAliases().keys();
            for(int i=0; i<expAliases.length; i++) {
                aliases.add(expAliases[i]);
            }
        }
        return aliases;
    }
}
