/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     21/08/2013-2.6 Chris Delahunt 
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests;

import org.eclipse.persistence.internal.sessions.ArrayRecord;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test is used to allow clearing the schema before running automated tests.  
 * It is not intended to test behavior, but might be included in test runs where problems can be recorded.
 *
 */
public class ClearDatabaseSchemaTest extends TestCase {
    
    public ClearDatabaseSchemaTest() {
        setDescription("Clears the database for MYSQL.");
    }
    
    public void test() {
        if (getSession().getDatasourcePlatform().isMySQL()) {
            //for now, only MySQL is targeted
            ArrayRecord record = (ArrayRecord)getSession().executeSQL("select DATABASE()").get(0);
            getSession().executeNonSelectingSQL("drop database "+record.get("DATABASE()"));
            getSession().executeNonSelectingSQL("create database "+record.get("DATABASE()"));
            getDatabaseSession().logout();
            getDatabaseSession().login();
        }
    }
}
