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
//     Mike Norman - from Proof-of-concept, become production code
//     quwang - Aug 9, 2006
package dbws.testing.shadowddlgeneration.oldjpub;

//javase imports
import java.sql.SQLException;
import java.util.List;

//EclipseLink imports
import dbws.testing.shadowddlgeneration.oldjpub.Util;

public class PlsqlRecordName extends SqlName {

    /**
     * Create a SqlName instance for a PL/SQL type, which requires extra identifying information,
     * such the names for the package and method that mentions this PL/SQL type.
     *
     * * @param schema
     *
     * @param type
     * @param parentType
     *            The PL/SQL package type that references to the SqlType for which the SqlName is
     *            created for
     */
    public PlsqlRecordName(String schema, String type, boolean fromDB, int line, int col,
        String packageName, SqlType parentType, List<AttributeField> fields, SqlReflector reflector)
        throws SQLException {
        super(schema, null, fromDB, line, col, false, false, null, null, null, reflector);

        m_context = Util.getSchema(schema, type);
        if (m_context == null) {
            m_context = NO_CONTEXT;
        }
        m_sourceName = Util.getType(schema, type);

        String[] mSourceName = new String[]{m_sourceName};
        boolean[] mIsRowType = new boolean[]{m_isRowType};
        m_name = reflector.determineSqlName(packageName, mSourceName, parentType, mIsRowType,
            fields, null /* elemType */, null /* valueType */);
        m_isReused = reflector.isReused(m_name);
        m_sourceName = mSourceName[0];
        m_isRowType = mIsRowType[0];
        m_name = m_fromDB ? m_name : reflector.getViewCache().dbifyName(m_name);
    }
}
