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

package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel;

public enum OraclePLSQLTypes implements SimpleDatabaseType, OraclePLSQLType {

    BinaryInteger("BINARY_INTEGER"),
    Dec("DEC") ,
    Int("INT"),
    Natural("NATURAL"),
    NaturalN("NATURALN"),
    PLSQLBoolean("BOOLEAN"),
    PLSQLInteger("PLS_INTEGER"),
    Positive("POSITIVE"),
    PositiveN("POSITIVEN"),
    SignType("SIGNTYPE"),
    ;

    private final String typeName;

    OraclePLSQLTypes(String typeName) {
        this.typeName = typeName;
    }

    public boolean isComplexDatabaseType() {
        return false;
    }

    public String getTypeName() {
        return typeName;
    }

    public boolean isJDBCType() {
        return false;
    }

}
