/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Nov 5, 2013-2.5 Chris Delahunt
//       - 421109: DB2 AS400 doesn't support "USE AND KEEP UPDATE LOCKS" used for pessimistic locking
//     04/30/2015 - Will Dazey
//       - 465063 : Added National Character support for DB2 on I
package org.eclipse.persistence.platform.database;

import java.util.Hashtable;

import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;

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
     * Used for pessimistic locking in DB2.
     * Without the "WITH RS" the lock is not held.
     * AS/400 does not support UPDATE type lock, so EXCLUSIVE is used instead.
     */
    @Override
    public String getSelectForUpdateString() {
        return " FOR READ ONLY WITH RS USE AND KEEP EXCLUSIVE LOCKS";
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

    @Override
    protected Hashtable buildFieldTypes() {
        Hashtable<Class<?>, Object> res = super.buildFieldTypes();
        if (getUseNationalCharacterVaryingTypeForString()) {
            res.put(String.class, new FieldTypeDefinition("VARCHAR", DEFAULT_VARCHAR_SIZE));
        }
        return res;
    }

    @Override
    public String getTableCreationSuffix() {
        // If we're on I and using unicode support we need to append CCSID
        // on the table rather than FOR MIXED DATA on each column
        if (getUseNationalCharacterVaryingTypeForString()) {
            return " CCSID";
        }
        return super.getTableCreationSuffix();
    }
}
