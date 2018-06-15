/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db;

/**
 * Interface for an external database that can be queried for metadata.
 * @see ExternalDatabaseFactory
 */
public interface ExternalDatabase {

    /**
     * Return the database's catalog names.
     * What connotes a "catalog" is typically platform-dependent.
     */
    String[] getCatalogNames();

    /**
     * Return the database's schema names.
     * What connotes a "schema" is typically platform-dependent.
     */
    String[] getSchemaNames();

    /**
     * Return the database's table types.
     * What connotes a "table type" is typically platform-dependent.
     */
    String[] getTableTypeNames();

    /**
     * Return the database's table descriptions corresponding to the specified
     * search criteria.
     * Depending on the implementation and/or runtime configuration, clients
     * will use this method or #getTableDescriptions().
     * @see java.sql.DatabaseMetaData#getTables(String, String, String, String[])
     */
    ExternalTableDescription[] getTableDescriptions(String catalogName, String schemaNamePattern, String tableNamePattern, String[] tableTypeNames);

    /**
     * Return all the database's table descriptions.
     * Depending on the implementation and/or runtime configuration, clients
     * will use this method or #getTableDescriptions(String, String, String, String[]).
     */
    ExternalTableDescription[] getTableDescriptions();

}
