/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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

    public String getSQLVerificationString() {
        return "SELECT " + columnToAdd + " FROM " + tableToAddTo + " WHERE ID = " + ((AboutToInsertSingleTableObject)objectToInsert).getId() + " AND " + columnToAdd + " = " + columnToAddValueFrom;
    }
}