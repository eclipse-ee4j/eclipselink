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
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel;

// javase imports
import static java.sql.Types.ARRAY;
import static java.sql.Types.BIGINT;
import static java.sql.Types.BINARY;
import static java.sql.Types.BIT;
import static java.sql.Types.BLOB;
import static java.sql.Types.BOOLEAN;
import static java.sql.Types.CHAR;
import static java.sql.Types.CLOB;
import static java.sql.Types.DATALINK;
import static java.sql.Types.DATE;
import static java.sql.Types.DECIMAL;
import static java.sql.Types.DISTINCT;
import static java.sql.Types.DOUBLE;
import static java.sql.Types.FLOAT;
import static java.sql.Types.INTEGER;
import static java.sql.Types.JAVA_OBJECT;
import static java.sql.Types.LONGVARBINARY;
import static java.sql.Types.LONGVARCHAR;
import static java.sql.Types.NULL;
import static java.sql.Types.NUMERIC;
import static java.sql.Types.OTHER;
import static java.sql.Types.REAL;
import static java.sql.Types.REF;
import static java.sql.Types.SMALLINT;
import static java.sql.Types.STRUCT;
import static java.sql.Types.TIME;
import static java.sql.Types.TIMESTAMP;
import static java.sql.Types.TINYINT;
import static java.sql.Types.VARBINARY;
import static java.sql.Types.VARCHAR;

public enum JDBCTypes implements JDBCType {

        ARRAY_TYPE(ARRAY, "ARRAY"),
        BIGINT_TYPE(BIGINT, "BIGINT"),
        BINARY_TYPE(BINARY, "BINARY"),
        BIT_TYPE(BIT, "BIT"),
        BLOB_TYPE(BLOB, "BLOB"),
        BOOLEAN_TYPE(BOOLEAN, "BOOLEAN"),
        CHAR_TYPE(CHAR, "CHAR"),
        CLOB_TYPE(CLOB, "CLOB"),
        DATALINK_TYPE(DATALINK, "DATALINK"),
        DATE_TYPE(DATE, "DATE"),
        DECIMAL_TYPE(DECIMAL, "DECIMAL"),
        DISTINCT_TYPE(DISTINCT, "DISTINCT"),
        DOUBLE_TYPE(DOUBLE, "DOUBLE"),
        FLOAT_TYPE(FLOAT, "FLOAT"),
        INTEGER_TYPE(INTEGER, "INTEGER"),
        JAVA_OBJECT_TYPE(JAVA_OBJECT, "JAVA_OBJECT"),
        LONGVARBINARY_TYPE(LONGVARBINARY, "LONGVARBINARY"),
        LONGVARCHAR_TYPE(LONGVARCHAR, "LONGVARCHAR"),
        NULL_TYPE(NULL, "NULL"),
        NUMERIC_TYPE(NUMERIC, "NUMERIC"),
        OTHER_TYPE(OTHER, "OTHER"),
        REAL_TYPE(REAL, "REAL"),
        REF_TYPE(REF, "REF"),
        SMALLINT_TYPE(SMALLINT, "SMALLINT"),
        STRUCT_TYPE(STRUCT, "STRUCT"),
        TIME_TYPE(TIME, "TIME"),
        TIMESTAMP_TYPE(TIMESTAMP, "TIMESTAMP"),
        TINYINT_TYPE(TINYINT, "TINYINT"),
        VARBINARY_TYPE(VARBINARY, "VARBINARY"),
        VARCHAR_TYPE(VARCHAR, "VARCHAR"),
        ;

        private final int typeCode;
        private final String typeName;

        JDBCTypes(int typeCode, String typeName) {
            this.typeCode = typeCode;
            this.typeName = typeName;
        }

        public int getTypeCode() {
            return typeCode;
        }

        public String getTypeName() {
            return typeName;
        }

        public boolean isComplexDatabaseType() {
            return false;
        }

        public boolean isJDBCType() {
            return true;
        }
}
