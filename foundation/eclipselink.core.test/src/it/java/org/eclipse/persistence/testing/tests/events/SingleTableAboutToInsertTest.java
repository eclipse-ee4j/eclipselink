/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.events;

import org.eclipse.persistence.testing.models.events.AboutToInsertSingleTableObject;

/**
 *  CR#3237
 *  Test to make sure when the row provided in an aboutToInsert event is added to,
 * the addition will be reflected in the insert.
 */
public class SingleTableAboutToInsertTest extends AboutToInsertEventTest {
    public static final String tableToAddTo = "AboutToInsertSingle";
    public static final String columnToAdd = "EXTRA_NUMBER";
    public static final String columnToAddValueFrom = "ID";

    public SingleTableAboutToInsertTest(AboutToInsertSingleTableObject objectToInsert, boolean isMultithreaded) {
        super(objectToInsert, isMultithreaded);
    }

    @Override
    public String getSQLVerificationString() {
        return "SELECT " + columnToAdd + " FROM " + tableToAddTo + " WHERE ID = " + ((AboutToInsertSingleTableObject)objectToInsert).getId() + " AND " + columnToAdd + " = " + columnToAddValueFrom;
    }
}
