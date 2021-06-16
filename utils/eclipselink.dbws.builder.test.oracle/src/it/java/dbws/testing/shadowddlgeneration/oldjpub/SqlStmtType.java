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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//EclipseLink imports
import java.lang.reflect.Modifier;

/*
 * All SQL statements are published as methods in one Java class,
 * which is described by the instance of this singleton class
 */

public class SqlStmtType extends SqlType {

    public static ResultSet rset = null;

    protected List<ProcedureMethod> m_methods = new ArrayList<ProcedureMethod>();

    SqlStmtType(SqlName sqlName, SqlReflector reflector) throws SQLException {
        super(sqlName, OracleTypes.SQL_STATEMENTS, true, null, reflector);
    }

    public boolean isSqlStmt() {
        return true;
    }

    /**
     * Returns an array of Method objects reflecting all the methods declared by this
     * SqlTypeWithMethods object. Returns an array of length 0 if the SqlTypeWithMethods declares no
     * methods
     */
    public List<ProcedureMethod> getDeclaredMethods() {
        List<ProcedureMethod> methods = new ArrayList<ProcedureMethod>(m_methods);
        return methods;
    }

    protected boolean acceptMethod(ProcedureMethod method) {
        return true;
    }

    /**
     * Returns the modifiers for this type, encoded in an integer. The modifiers currently in use
     * are: public (always set) final abstract incomplete The modifiers are decoded using the
     * methods of java.lang.reflect.Modifier. If we ever need additional modifiers for C++, we can
     * subclass this.
     */
    public int getModifiers() throws SQLException {
        return Modifier.PUBLIC;
    }


    protected static void closeResultSet(ResultSet ps) {
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            // Closing resources. OK to ignore exception.
        }
    }

    protected static void closeStatement(PreparedStatement ps) {
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            // Closing resources. OK to ignore exception.
        }
    }

    public boolean hasMethods() throws SQLException, PublisherException {
        /* subclasses with methods override this */
        return m_methods != null && m_methods.size() > 0;
    }

}
