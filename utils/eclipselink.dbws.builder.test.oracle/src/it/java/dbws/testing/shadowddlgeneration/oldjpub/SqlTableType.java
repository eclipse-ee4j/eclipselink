/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Mike Norman - from Proof-of-concept, become production code
package dbws.testing.shadowddlgeneration.oldjpub;

public class SqlTableType extends SqlCollectionType {

    public SqlTableType(SqlName sqlName, boolean generateMe, SqlType parentType,
        SqlReflector reflector) {
        super(sqlName, OracleTypes.TABLE, generateMe, parentType, reflector);
    }

    /**
     * Determines if this Type represents a table type.
     */
    @Override
    public boolean isTable() {
        return true;
    }
}
