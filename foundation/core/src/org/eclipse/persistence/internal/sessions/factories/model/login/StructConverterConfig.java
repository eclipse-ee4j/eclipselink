/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions.factories.model.login;

import java.util.Vector;

public class StructConverterConfig {
    private Vector<String> m_structConverterClasses;

    public StructConverterConfig() {
        m_structConverterClasses = new Vector<String>();
    }

    public void addStructConverterClass(String listener) {
        m_structConverterClasses.add(listener);
    }

    public void setStructConverterClasses(Vector<String> dataConverters) {
        m_structConverterClasses = dataConverters;
    }

    public Vector<String> getStructConverterClasses() {
        return m_structConverterClasses;
    }
    
}
