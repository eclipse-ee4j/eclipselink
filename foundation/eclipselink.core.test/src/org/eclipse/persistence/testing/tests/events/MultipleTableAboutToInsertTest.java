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
package org.eclipse.persistence.testing.tests.events;

import org.eclipse.persistence.testing.models.events.AboutToInsertMultiTableObject;

/**
 *  CR#3237
 *  Test to make sure when the row provided in an aboutToInsert event is added to,
 * the addition will be reflected in the insert.
 */
public class MultipleTableAboutToInsertTest extends AboutToInsertEventTest {
    public static final String tableToAddTo = "AboutToInsertMulti1";
    public static final String columnToAdd = "EXTRA_NUMBER";
    public static final String columnToAddValueFrom = "ID";

    public MultipleTableAboutToInsertTest(AboutToInsertMultiTableObject objectToInsert, boolean isMultithreaded) {
        super(objectToInsert, isMultithreaded);
    }

    public String getSQLVerificationString() {
        return "SELECT " + columnToAdd + " FROM " + tableToAddTo + " WHERE ID = " + ((AboutToInsertMultiTableObject)objectToInsert).getId() + " AND " + columnToAdd + " = " + columnToAddValueFrom;
    }
}
