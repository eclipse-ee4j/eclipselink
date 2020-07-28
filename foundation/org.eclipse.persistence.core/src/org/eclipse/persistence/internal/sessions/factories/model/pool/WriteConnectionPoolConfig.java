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
package org.eclipse.persistence.internal.sessions.factories.model.pool;


/**
 * INTERNAL:
 */
public class WriteConnectionPoolConfig extends ConnectionPoolConfig {
    public WriteConnectionPoolConfig() {
        m_name = "default";
    }

    /*
     * INTERNAL:
     * This method will ignore the name passed in. For the write connection
     * pool on a server session, its name must remain 'default'. It must not
     * change.
     */
    public void setName(String name) {
        // ignore it, must remain as 'default'
    }
}
