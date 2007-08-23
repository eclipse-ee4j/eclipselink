/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions.factories.model.rcm.command;


/**
 * INTERNAL:
 */
public class CommandsConfig {
    private boolean m_cacheSync;

    public CommandsConfig() {
        m_cacheSync = false;
    }

    public void setCacheSync(boolean cacheSync) {
        m_cacheSync = cacheSync;
    }

    public boolean getCacheSync() {
        return m_cacheSync;
    }
}