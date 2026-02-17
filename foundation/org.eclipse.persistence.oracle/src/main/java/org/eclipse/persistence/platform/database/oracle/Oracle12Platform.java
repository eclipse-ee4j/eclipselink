/*
 * Copyright (c) 2014, 2026 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.platform.database.oracle;

import java.io.IOException;
import java.io.Writer;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.List;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ValueReadQuery;

/**
 * <p><b>Purpose:</b>
 * Supports usage of certain Oracle JDBC specific APIs for the Oracle 12 database.
 */
public class Oracle12Platform extends Oracle11Platform {

    public Oracle12Platform() {
        super();
    }

    /**
     * INTERNAL:
     * Append the receiver's field 'identity' constraint clause to a writer.
     * @param writer Target writer.
     * @since 2.7
     */
    @Override
    public void printFieldIdentityClause(Writer writer) throws ValidationException {
        try {
            writer.write(" GENERATED AS IDENTITY");
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    /**
     * INTERNAL:
     * This method builds a Struct using the unwrapped connection within the session
     * @return Struct
     */
    @Override
    public Struct createStruct(String structTypeName, Object[] attributes, AbstractRecord row, List<DatabaseField> orderedFields, AbstractSession session, Connection connection) throws SQLException {
        for (int index = 0; index < orderedFields.size(); index++) {
            DatabaseField field = orderedFields.get(index);
            if (row.getField(field) != null && row.getField(field).getTypeName() != null) {
                if (ClassConstants.BLOB.getTypeName().equals(row.getField(field).getTypeName())) {
                    Blob blob = connection.createBlob();
                    blob.setBytes(1L, (byte[]) row.get(field));
                    attributes[index] = blob;
                } else if (ClassConstants.CLOB.getTypeName().equals(row.getField(field).getTypeName())) {
                    Clob clob = connection.createClob();
                    clob.setString(1L, (String) attributes[index]);
                    attributes[index] = clob;
                }
            } else {
                attributes[index] = row.get(field);
            }
        }
        return createStruct(structTypeName, attributes, connection);
    }

    /**
     * Create java.sql.Struct from given parameters.
     * @param structTypeName - the SQL type name of the SQL structured type that this Struct object maps to.
     * @param attributes - the attributes that populate the returned object
     * @param connection - DB connection
     * @return Struct
     */
    @Override
    public Struct createStruct(String structTypeName, Object[] attributes, Connection connection) throws SQLException {
        return connection.createStruct(structTypeName, attributes);
    }

    /**
     * INTERNAL:
     * This method returns the query to select the UUID
     * from the server for Oracle.
     */
    @Override
    public ValueReadQuery getUUIDQuery() {
        if (uuidQuery == null) {
            uuidQuery = new ValueReadQuery();
            uuidQuery.setSQLString("SELECT LOWER(REGEXP_REPLACE(RAWTOHEX(SYS_GUID()), '([A-F0-9]{8})([A-F0-9]{4})([A-F0-9]{4})([A-F0-9]{4})([A-F0-9]{12})', '\\1-\\2-\\3-\\4-\\5')) AS uuid FROM dual");
            uuidQuery.setAllowNativeSQLQuery(true);
        }
        return uuidQuery;
    }
}
