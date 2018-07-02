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
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db;

/**
 * This defines a common interface for all the tables (internal, external, and user-defined)
 * so they can be consolidated whenever necessary (e.g. in the UI choosers).
 */
public interface TableDescription {

    /**
     * Return the table's "catalog" name.
     */
    String getCatalogName();

    /**
     * Return the table's "schema" name.
     */
    String getSchemaName();

    /**
     * Return the table's unqualified name.
     */
    String getName();

    /**
     * Return the table's fully-qualified name,
     * typically in the form "catalog.schema.name".
     */
    String getQualifiedName();

    /**
     * Return any additional information about the table
     * represented by this TableDescription object, as a String.
     * This information can be used to differentiate among
     * TableDescription objects that might have the same name.
     * It can also be used for debugging user-developed
     * external databases while using the TopLink
     * Mapping Workbench.
     */
    String getAdditionalInfo();

}
