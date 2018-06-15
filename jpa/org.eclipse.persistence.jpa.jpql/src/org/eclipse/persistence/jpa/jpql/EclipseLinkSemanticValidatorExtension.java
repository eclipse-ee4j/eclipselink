/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql;

/**
 * This extension provides additional support to semantic validation by adding support for non-JPA
 * specific artifacts, such as database objects.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public interface EclipseLinkSemanticValidatorExtension {

    /**
     * A <code>null</code>-instance of this extension.
     */
    EclipseLinkSemanticValidatorExtension NULL_EXTENSION = new EclipseLinkSemanticValidatorExtension() {
        @Override
        public boolean columnExists(String tableName, String columnName) {
            return false;
        }
        @Override
        public String getEntityTable(String entityName) {
            return null;
        }
        @Override
        public boolean tableExists(String tableName) {
            return false;
        }
    };

    /**
     * Determines whether the column with the given name exists or not in the table with the given name.
     *
     * @param tableName The name of the table to look for the column
     * @param columnName The name of the column to determine its existence
     * @return <code>true</code> if the column exists; <code>false</code> otherwise
     */
    boolean columnExists(String tableName, String columnName);

    /**
     * Returns the name of the primary table defined for the entity with the given name.
     *
     * @param entityName The name of the entity
     * @return The name of the entity's primary table
     */
    String getEntityTable(String entityName);

    /**
     * Determines whether the table with the given exists or not.
     *
     * @param tableName The name of the table to determine its existence
     * @return <code>true</code> if the table exists; <code>false</code> otherwise
     */
    boolean tableExists(String tableName);
}
