/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     09/29/2016-2.7 Tomas Kraus
//       - 426852: @GeneratedValue(strategy=GenerationType.IDENTITY) support in Oracle 12c
package org.eclipse.persistence.platform.database.oracle;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.Vector;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;

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
     * This method builds a Struct using the unwrapped connection within the session
     * @return Struct
     */
    @Override
    public Struct createStruct(String structTypeName, Object[] attributes, AbstractRecord row, Vector orderedFields, AbstractSession session, Connection connection) throws SQLException {
        for (int index = 0; index < orderedFields.size(); index++) {
            DatabaseField field = (DatabaseField)orderedFields.elementAt(index);
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
}
