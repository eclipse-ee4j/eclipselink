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
 * Interface defining the meta-data    required by the TopLink
 * Mapping Workbench.
 * @see ExternalTableDescription
 */
public interface ExternalTable {

    /**
     * Return an array of the table's columns.
     */
    ExternalColumn[] getColumns();

    /**
     * Return an array of the table's foreign keys.
     */
    ExternalForeignKey[] getForeignKeys();

}
