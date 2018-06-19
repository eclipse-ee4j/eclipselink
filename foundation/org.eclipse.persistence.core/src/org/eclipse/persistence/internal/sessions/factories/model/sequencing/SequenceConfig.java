/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.sessions.factories.model.sequencing;


/**
 * INTERNAL:
 */
public class SequenceConfig {
    private String m_name;
    private Integer m_preallocationSize;

    public SequenceConfig() {
        super();
    }

    public void setName(String name) {
        m_name = name;
    }

    public String getName() {
        return m_name;
    }

    public void setPreallocationSize(Integer preallocationSize) {
        m_preallocationSize = preallocationSize;
    }

    public Integer getPreallocationSize() {
        return m_preallocationSize;
    }
}
