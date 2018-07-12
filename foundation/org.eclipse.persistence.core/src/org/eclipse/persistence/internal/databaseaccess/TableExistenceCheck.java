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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.databaseaccess;

import java.util.Vector;

import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

/**
 * INTERNAL:
 * Interface to define method prototype for table existence check.
 */
public interface TableExistenceCheck {

    /**
     * INTERNAL:
     * Executes and evaluates query to check whether given table exists.
     * @param session current database session
     * @param table database table meta-data
     * @return value of {@code true} if given table exists or {@code false} otherwise
     */
    boolean exists(final DatabaseSessionImpl session, final TableDefinition table);

    // PERF: This check should work on any database but it's not very efficient
    //       because of an exception handling.
    /**
     * Table existence check based on exception being thrown from query.
     * Common query that should work on any relational database is used. Query result is ignored.
     * Check returns value of {@code true} if query execution did not throw an exception or {@code false} otherwise
     */
    public static final class Default implements TableExistenceCheck {

        /**
         * {@inheritDoc}
         */
        private DataReadQuery getQuery(final TableDefinition table) {
            String column = null;
            for (FieldDefinition field : table.getFields()) {
                if (column == null) {
                    column = field.getName();
                } else if (field.isPrimaryKey()) {
                    column = field.getName();
                    break;
                }
            }
            final String sql = "SELECT " + column + " FROM " + table.getFullName();
            final DataReadQuery query = new DataReadQuery(sql);
            query.setMaxRows(1);
            return query;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean exists(final DatabaseSessionImpl session, final TableDefinition table) {
            try {
                session.setLoggingOff(true);
                session.executeQuery(getQuery(table));
                return true;
            } catch (Exception notFound) {
                return false;
            }
        }

    }

    // PERF: Database specific check without exception being thrown shall be faster.
    /**
     * Table existence check based on query result. Exception evaluation should not be triggered.
     */
    public static abstract class ByResult implements TableExistenceCheck {
        
        /**
         * INTERNAL:
         * Defines query to check whether given table exists.
         * This query shall not cause exception. It shall return row only when database exists
         * or empty result set otherwise.
         * @param table database table meta-data
         * @return query to check whether given table exists
         */
        protected abstract DataReadQuery getQuery(final TableDefinition table);

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean exists(final DatabaseSessionImpl session, final TableDefinition table) {
            try {
                session.setLoggingOff(true);
                final Vector result = (Vector)session.executeQuery(getQuery(table));
                return !result.isEmpty();
            } catch (Exception notFound) {
                return false;
            }
        }

    }

}
