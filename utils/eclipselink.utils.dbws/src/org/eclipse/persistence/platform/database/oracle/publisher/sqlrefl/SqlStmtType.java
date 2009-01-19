package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import org.eclipse.persistence.platform.database.oracle.publisher.PublisherException;

/*
 * All SQL statements are published as methods in one Java class,
 * which is described by the instance of this singleton class
 */
@SuppressWarnings("unchecked")
public class SqlStmtType extends SqlType {

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
    public Method[] getDeclaredMethods() {
        Method[] methods = new Method[m_methods.size()];
        for (int i = 0; i < m_methods.size(); i++) {
            methods[i] = (Method)m_methods.elementAt(i);
        }
        return methods;
    }

    protected boolean acceptMethod(Method method) {
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

    public static ResultSet rset = null;

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

    Vector m_methods;

    public boolean hasMethods() throws SQLException, PublisherException {
        /* subclasses with methods override this */
        return m_methods != null && m_methods.size() > 0;
    }

}
