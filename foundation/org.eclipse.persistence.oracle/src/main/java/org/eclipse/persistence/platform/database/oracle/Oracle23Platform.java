/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.platform.database.oracle;

/**
 * <p><b>Purpose:</b>
 * Supports certain new Oracle 23c data types, and usage of certain Oracle JDBC specific APIs.
 * <p> Supports Oracle JSON data type.
 * <p> Supports Oracle OracleJsonValue derived Java types.
 */
public class Oracle23Platform extends Oracle21Platform {

    /**
     * Creates an instance of Oracle 23c database platform.
     */
    public Oracle23Platform() {
        super();
    }


    /**
     * INTERNAL:
     * Check whether current platform is Oracle 23c or later.
     * @return Always returns {@code true} for instances of Oracle 23c platform.
     * @since 4.0.2
     */
    @Override
    public boolean isOracle23() {
        return true;
    }
}
