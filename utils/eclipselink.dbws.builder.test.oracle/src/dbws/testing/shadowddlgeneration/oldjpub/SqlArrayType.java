/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Mike Norman - from Proof-of-concept, become production code
package dbws.testing.shadowddlgeneration.oldjpub;

//EclipseLink imports

public class SqlArrayType extends SqlCollectionType {

    public SqlArrayType(SqlName sqlName, boolean generateMe, SqlType parentType,
        SqlReflector reflector) {
        super(sqlName, OracleTypes.ARRAY, generateMe, parentType, reflector);
    }

    /**
     * Determines if this Type represents an Array type.
     * <p/>
     */
    public boolean isArray() {
        // INFEASIBLE
        return true;
    }
}
