/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.sessions.factories.model.sequencing;


/**
 * INTERNAL:
 */
public class TableSequenceConfig extends SequenceConfig {
    private String m_table;
    private String m_nameField;
    private String m_counterField;

    public TableSequenceConfig() {
        super();
    }

    public void setTable(String table) {
        m_table = table;
    }

    public String getTable() {
        return m_table;
    }

    public void setNameField(String nameField) {
        m_nameField = nameField;
    }

    public String getNameField() {
        return m_nameField;
    }

    public void setCounterField(String counterField) {
        m_counterField = counterField;
    }

    public String getCounterField() {
        return m_counterField;
    }
}
