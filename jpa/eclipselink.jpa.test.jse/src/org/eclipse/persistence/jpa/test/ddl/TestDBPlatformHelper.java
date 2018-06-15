/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2015 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     03/19/2015 - Rick Curtis
//       - 462586 : Add national character support for z/OS.
//     04/30/2015 - Will Dazey
//       - 465063 : Changed VendorNameToPlatformMapping file. Updating tests.
package org.eclipse.persistence.jpa.test.ddl;

import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.internal.helper.DBPlatformHelper;
import org.eclipse.persistence.jpa.test.basic.model.Employee;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.logging.DefaultSessionLog;
import org.eclipse.persistence.platform.database.DB2MainframePlatform;
import org.eclipse.persistence.platform.database.DB2ZPlatform;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestDBPlatformHelper {
    @Emf(createTables = DDLGen.DROP_CREATE, classes = { Employee.class })
    private EntityManagerFactory emf;

    DefaultSessionLog log = new DefaultSessionLog();

    @Test
    public void test() {
        emf.createEntityManager().close();
        Class<?> dbClass = emf.unwrap(DatabaseSession.class).getDatasourcePlatform().getClass();
        Assert.assertFalse("Database Platform: " + dbClass, DatabasePlatform.class.equals(dbClass));
    }

    @Test
    public void testDB2ZOS() {
        //Returned from jcc driver
        Assert.assertEquals(DB2ZPlatform.class.getName(), getPlatformClass("DB2", "DSN10015"));
    }

    @Test
    public void testDB2I() {
        //Returned from jcc driver (DRDA)
        Assert.assertEquals(DB2MainframePlatform.class.getName(), getPlatformClass("AS", "QSQ07020"));

        //Returned from type 2 native driver & type 4 open source driver (non-DRDA)
        Assert.assertEquals(DB2MainframePlatform.class.getName(), getPlatformClass("DB2 UDB for AS/400", "07.02.0000 V7R2m0"));
    }

    private String getPlatformClass(String productName, String productVersion){
        return DBPlatformHelper.getDBPlatform(productName + productVersion, log);
    }
}
