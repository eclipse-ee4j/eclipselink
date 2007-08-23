/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.platform.database;


/**
 * <p><b>Purpose</b>: Provides SQL Anywhere specific behaviour.
 * <p> For the most part this is the same as Sybase, the outer join syntax is suppose to be different.
 *
 * @since TOPLink/Java 2.1
 */
public class SQLAnyWherePlatform extends SybasePlatform {
    public boolean isSQLAnywhere() {
        return true;
    }

    public boolean isSybase() {
        return false;
    }

    /**
     * SQL Anywhere does not support the Sybase outer join syntax.
     */
    public boolean shouldPrintOuterJoinInWhereClause() {
        return false;
    }
}