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
package org.eclipse.persistence.platform.database;

import org.eclipse.persistence.expressions.ExpressionOperator;

/**
 * <b>Purpose</b>: Provides DB2 Mainframe specific behavior.<p>
 * This provides for some additional compatibility in certain DB2 versions on OS390.
 * <b>Responsibilities</b>:
 * <ul>
 *  <li>Specialized CONCAT syntax
 * </ul>
 *
 * @since TopLink 3.0.3
 */
public class DB2MainframePlatform extends DB2Platform {
    public DB2MainframePlatform() {
        super();
        this.pingSQL = "SELECT COUNT(*) from SYSIBM.SYSDUMMY1 WHERE 1 = 0";
    }

    /**
     * Initialize any platform-specific operators
     */
    @Override
    protected void initializePlatformOperators() {
        super.initializePlatformOperators();

        addOperator(ExpressionOperator.simpleLogicalNoParens(ExpressionOperator.Concat, "CONCAT"));
    }
    
    /**
     * INTERNAL:
     * Return if brackets can be used in the ON clause for outer joins.
     * Some DB2 versions on OS390 do not allow this.
     */
    @Override
    public boolean supportsOuterJoinsWithBrackets() {
        return false;
    }
}