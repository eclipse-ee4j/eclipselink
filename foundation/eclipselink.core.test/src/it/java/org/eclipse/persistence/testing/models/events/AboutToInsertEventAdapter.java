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
package org.eclipse.persistence.testing.models.events;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.testing.framework.TestErrorException;

/**
 * Added for CR#3237.
 * Event listener for AboutToInsertEvent.
 * This listener will add to the Database row in the insert even.
 */
public class AboutToInsertEventAdapter extends DescriptorEventAdapter {
    private String tableToAddTo = null;
    private String columnToAdd = null;
    private String newColumn = null;

    public AboutToInsertEventAdapter(String table, String column) {
        this(table, column, "ID");
    }

    public AboutToInsertEventAdapter(String table, String column, String newColumn) {
        tableToAddTo = table;
        columnToAdd = column;
        this.newColumn = newColumn;
    }

    /**
 * Add to the row about to be inserted.
 * Add a field qualified with its table name.
 */
    public void aboutToInsert(DescriptorEvent event) {
        Record row = event.getRecord();
        Object value = row.get(newColumn);
        row.put(tableToAddTo + "." + columnToAdd, value);
        // Test that descriptor was set.
        if (event.getDescriptor() == null) {
            throw new TestErrorException("Descriptor not set on event.");
        }
    }
}
