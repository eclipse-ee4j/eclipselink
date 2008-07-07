/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle, Markus KARG(markus-karg@users.sourceforge.net). All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Markus Karg and Oracle - initial API and implementation from Oracle TopLink and TopLink Essentials
 ******************************************************************************/  
package org.eclipse.persistence.platform.database;


/**
 * <p><b>Purpose</b>: Provides SQL Anywhere specific behavior.
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