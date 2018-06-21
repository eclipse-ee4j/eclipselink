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
 * Interface defining a lightweight wrapper around the
 * database metadata required by the TopLink Mapping Workbench.
 *
 * @see ExternalDatabase
 */
public interface ExternalTableDescription extends TableDescription {

    /**
     * Returns the ExternalTable object corresponding to this
     * ExternalTableDescription object.
     * This allows the ExternalTableDescription object to postpone
     * loading extra meta-data until it is actually needed by the
     * TopLink Mapping Workbench. The name and
     * description are required beforehand because
     * they are used by the user to select an ExternalTable
     * to load.
     */
    ExternalTable getTable();

}
