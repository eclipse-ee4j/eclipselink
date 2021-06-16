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
package org.eclipse.persistence.internal.expressions;

import java.io.*;
import java.util.Vector;
import java.util.Collection;

import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * @author Andrei Ilitchev
 * @since TOPLink/Java 1.0
 */
public class SQLUpdateAllStatementForTempTable extends SQLModifyAllStatementForTempTable {
    protected Collection assignedFields;
    public void setAssignedFields(Collection assignedFields) {
        this.assignedFields = assignedFields;
    }
    public Collection getAssignedFields() {
        return assignedFields;
    }

    @Override
    protected Collection getUsedFields() {
        Vector usedFields = new Vector(getPrimaryKeyFields());
        usedFields.addAll(getAssignedFields());
        return usedFields;
    }

    @Override
    protected void writeUpdateOriginalTable(AbstractSession session, Writer writer) throws IOException {
        session.getPlatform().writeUpdateOriginalFromTempTableSql(writer, getTable(),
                                                        new Vector(getPrimaryKeyFields()),
                                                        new Vector(getAssignedFields()));
    }
}
