package org.eclipse.persistence.platform.database.oracle.publisher;

//javase imports
import java.sql.Connection;

//EclipseLink
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlReflectorImpl;

public interface SqlReflector {

    public enum SqlReflectorHelper {
        sqlReflectorHelper;
        public static SqlReflector getDefaultSqlReflector(Connection conn, String user) {
            return new SqlReflectorImpl(conn, user);
        }
    }
}