/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - from Proof-of-concept, become production code
 ******************************************************************************/
package  org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

//javase imports
import java.sql.SQLException;

//EclipseLink imports
import org.eclipse.persistence.platform.database.oracle.publisher.Util;

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
