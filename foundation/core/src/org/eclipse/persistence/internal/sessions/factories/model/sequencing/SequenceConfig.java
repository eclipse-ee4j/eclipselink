/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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