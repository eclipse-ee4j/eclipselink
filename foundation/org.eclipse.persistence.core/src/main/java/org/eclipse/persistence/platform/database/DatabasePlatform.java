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
package org.eclipse.persistence.platform.database;

/**
 * DatabasePlatform is private to EclipseLink. It encapsulates behavior specific to a database platform
 * (eg. Oracle, Sybase, DBase), and provides protocol for EclipseLink to access this behavior. The behavior categories
 * which require platform specific handling are SQL generation and sequence behavior. While database platform
 * currently provides sequence number retrieval behavior, this will move to a sequence manager (when it is
 * implemented).
 *
 * @see AccessPlatform
 * @see DB2Platform
 * @see DBasePlatform
 * @see OraclePlatform
 * @see SybasePlatform
 *
 * @since TOPLink/Java 1.0
 */
public class DatabasePlatform extends org.eclipse.persistence.internal.databaseaccess.DatabasePlatform {
    public static final int DEFAULT_VARCHAR_SIZE = 255;

    /**
     * Creates an instance of default database platform.
     */
    public DatabasePlatform() {
        super();
    }

}
