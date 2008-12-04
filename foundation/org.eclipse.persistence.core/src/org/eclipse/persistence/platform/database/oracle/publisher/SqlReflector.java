package org.eclipse.persistence.platform.database.oracle.publisher;

import java.sql.Connection;

public interface SqlReflector {

    public enum SqlReflectorHelper {
        sqlReflectorHelper;
        static SqlReflector emptyReflector = new SqlReflector() {
        };
        public static SqlReflector getDefaultSqlReflector(Connection conn, String user) {
            return emptyReflector;
        }
    }
}