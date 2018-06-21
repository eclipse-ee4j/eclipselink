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
package org.eclipse.persistence.internal.sessions.factories.model.login;


/**
 * INTERNAL:
 */
public class EISLoginConfig extends LoginConfig {
    private String m_connectionSpecClass;
    private String m_connectionFactoryURL;

    public EISLoginConfig() {
        super();
    }

    public void setConnectionSpecClass(String connectionSpecClass) {
        m_connectionSpecClass = connectionSpecClass;
    }

    public String getConnectionSpecClass() {
        return m_connectionSpecClass;
    }

    public void setConnectionFactoryURL(String connectionFactoryURL) {
        m_connectionFactoryURL = connectionFactoryURL;
    }

    public String getConnectionFactoryURL() {
        return m_connectionFactoryURL;
    }
}
