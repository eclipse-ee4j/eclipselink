/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Mike Norman - from Proof-of-concept, become production code
package dbws.testing.shadowddlgeneration.oldjpub;

//javase imports
import java.sql.SQLException;

//EclipseLink imports
import dbws.testing.shadowddlgeneration.oldjpub.Util;

public class DefaultArgsHolderName extends SqlName {

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
    public DefaultArgsHolderName(String schema, String type, boolean fromDB, int line, int col,
        String packageName, SqlType parentType, SqlType valueType, SqlReflector reflector)
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
            null /* fields */, null /* elemType */, valueType /* valueType */);
        m_isReused = reflector.isReused(m_name);
        m_sourceName = m_name = m_fromDB ? m_name : reflector.getViewCache().dbifyName(m_name);
    }
}
