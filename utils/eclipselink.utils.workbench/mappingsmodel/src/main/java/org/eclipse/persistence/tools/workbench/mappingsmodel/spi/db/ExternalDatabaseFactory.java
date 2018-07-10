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
 * Simple factory interface for building external databases.
 */
public interface ExternalDatabaseFactory {

    /**
     * Build and return an ExternalDatabase for the specified
     * connection. The connection will be null if a connection to
     * a database has not been established or is inappropriate
     * (e.g. when executing inside jdev).
     * The connection can be ignored, as appropriate.
     */
    ExternalDatabase buildDatabase(java.sql.Connection connection);

}
