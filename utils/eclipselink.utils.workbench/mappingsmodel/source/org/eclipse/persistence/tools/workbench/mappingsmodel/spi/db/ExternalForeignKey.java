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
 * Interface describing the meta-data describing a database foreign key
 * required by the TopLink Mapping Workbench.
 * @see ExternalTable
 */
public interface ExternalForeignKey {

    /**
     * Return the foreign key's name. Often this name is system-generated.
     */
    String getName();

    /**
     * Return a description of the foreign key's target table.
     */
    ExternalTableDescription getTargetTableDescription();

    /**
     * Return an array of the foreign key's column pairs. These pairs match
     * up a foreign key column on the source table with a primary key column
     * on the target table.
     */
    ExternalForeignKeyColumnPair[] getColumnPairs();

}
