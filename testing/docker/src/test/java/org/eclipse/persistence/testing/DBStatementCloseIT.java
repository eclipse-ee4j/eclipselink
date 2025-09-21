/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing;

import com.ibm.db2.jcc.DB2TraceManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import static com.ibm.db2.jcc.DB2BaseDataSource.TRACE_CONNECTION_CALLS;
import static com.ibm.db2.jcc.DB2BaseDataSource.TRACE_STATEMENT_CALLS;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_GENERATION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_PASSWORD;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_URL;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DBStatementCloseIT {

    @Test
    void doTest() throws SQLException {
        DB2TraceManager.getTraceManager()
                       .setLogWriter(
                               new PrintWriter(System.out),
                               TRACE_STATEMENT_CALLS | TRACE_CONNECTION_CALLS);

        String host = System.getProperty("db2.host");
        String port = System.getProperty("db2.port");
        String database = System.getProperty("db2.database");
        String user = System.getProperty("db2.user");
        String password = System.getProperty("db2.password");

        String url = String.format("jdbc:db2://%1$s:%2$s/%3$s", host, port, database);

        System.out.println("Using JDBC URL: " + url);

        Properties properties = new Properties();
        properties.setProperty(JDBC_URL, String.format("jdbc:db2://%1$s:%2$s/%3$s", host, port, database));
        properties.setProperty(JDBC_USER, user);
        properties.setProperty(JDBC_PASSWORD, password);
        properties.setProperty(DDL_GENERATION, "drop-and-create-tables");

        int exceptionCount = 0;

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("PU", properties);
        for (int i = 1; i < 4000; i++) {
            try {
                EntityManager entityManager = entityManagerFactory.createEntityManager();
                entityManager.getTransaction().begin();

                var entity = new SomeEntity();
                entity.value = 1234l;

                entityManager.persist(entity);
                entityManager.getTransaction().commit();
                entityManager.close();
            } catch (Exception e) {
                System.out.println("Failed at " + i);
                exceptionCount++;
            }
        }

        assertEquals(0, exceptionCount, "There should be zero exceptions, but " + exceptionCount + " occured.");
    }

}