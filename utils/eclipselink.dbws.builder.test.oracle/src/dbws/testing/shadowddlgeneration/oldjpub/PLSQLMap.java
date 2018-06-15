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

public class PLSQLMap extends Typemap {
    public PLSQLMap(TypeClass s, SqlReflector reflector) {
        super(s, reflector);
        mapInit();
    }

    void mapInit() {
        bfileMap = OS_BFILE;
        cursorMap = JS_RESULTSET;
        rowidMap = OS_ROWID;
    }

    public String writeTypeName(TypeClass type) {
        return writeTypeName2(type, false);
    }

    public String writeTypeName(TypeClass type, boolean intfIfPossible) {
        return writeTypeName2(type, intfIfPossible);
    }

    private String writeTypeName2(TypeClass type, boolean itfIfPossible) {
        // In case the type is a Java type, which has no SQL correspondence
        if (type instanceof JavaType) {
            return ((JavaType)type).getTypeName(this);
        }
        SqlType sqlType = (SqlType)type;
        String predefinedName = null;
        if (sqlType == null) {
            return null;
        }
        String sql = (sqlType.getSqlName() == null) ? null : sqlType.getSqlName().toString();
        if (sql != null && m_reflector.getTypeMap().get(sql) != null) {
            predefinedName = ((SqlType)m_reflector.getTypeMap().get(sql)).getSqlName().getUseClass(
                m_package);
        }
        else {
            predefinedName = writePredefinedName(sqlType.getJdbcTypecode());
        }
        if (predefinedName != null) {
            return predefinedName;
        }
        if (sqlType.isRef()) {
            // SqlType componentType =
            // (SqlType) Publisher.getComponentTypeAndCatch(sqlType);
            return writeTypeName(sqlType) + "Ref";
        }
        else {
            SqlName sqlName = sqlType.getSqlName();
            return interfaceIfPossible(sqlName, m_package, itfIfPossible);
        }
    }

    private static String interfaceIfPossible(SqlName sqlName, String mpackage,
        boolean itfIfPossible) {
        if (itfIfPossible && sqlName.hasUseItf()) {
            return sqlName.getUseItf(mpackage);
        }
        else if (sqlName.hasUseClass()) {
            return sqlName.getUseClass(mpackage);
        }
        else if (itfIfPossible && sqlName.hasDeclItf()) {
            return sqlName.getDeclItf(mpackage);
        }
        else {
            return sqlName.getDeclClass(mpackage);
        }
    }

    /**
     * Determine the java name for a given SQL field.
     */
    public String getMemberName(String sqlName, boolean wordBoundary, boolean onlyIfRegistered,
        Name name) {
        String s = null;

        if (m_field_map != null && (s = (String)m_field_map.get(sqlName)) != null) {
            if (s.equals("null")) {
                return null;
            }
            return s;
        }
        else {
            return onlyIfRegistered ? null : SqlName.sqlIdToJavaId(sqlName, wordBoundary, true);
        }
    }

    public String getMemberNameAsSuffix(String sqlName) {
        return getMemberName(sqlName, true, false);
    }
}
