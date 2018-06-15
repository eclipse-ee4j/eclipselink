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
