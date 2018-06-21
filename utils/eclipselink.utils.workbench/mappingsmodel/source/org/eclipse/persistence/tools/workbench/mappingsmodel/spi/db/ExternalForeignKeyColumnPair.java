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
 * column pair required by the TopLink Mapping Workbench.
 * @see ExternalForeignKey
 *
 */
public interface ExternalForeignKeyColumnPair {

    /**
     * Return the "source" half of the column pair. This is the column
     * on the source table that must contain a value that matches
     * the value in the target column.
     */
    ExternalColumn getSourceColumn();

    /**
     * Return the "target" half of the column pair. This is typically
     * a column that is part of the target table's primary key.
     */
    ExternalColumn getTargetColumn();

}
