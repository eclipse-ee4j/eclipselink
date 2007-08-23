/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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