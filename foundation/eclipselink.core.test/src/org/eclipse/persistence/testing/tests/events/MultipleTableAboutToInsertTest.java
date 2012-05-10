/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
