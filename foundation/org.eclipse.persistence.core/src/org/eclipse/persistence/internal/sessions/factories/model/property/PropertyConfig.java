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
package org.eclipse.persistence.internal.sessions.factories.model.property;


/**
 * INTERNAL:
 */
public class PropertyConfig {
    private String m_name;
    private String m_value;

    public PropertyConfig() {
    }

    public void setName(String name) {
        m_name = name;
    }

    public String getName() {
        return m_name;
    }

    public void setValue(String value) {
        m_value = value;
    }

    public String getValue() {
        return m_value;
    }
}
