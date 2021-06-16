/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     03/19/2015 - Rick Curtis
//       - 462586 : Add national character support for z/OS.
//     04/30/2015 - Will Dazey
//       - 465063 : Changed VendorNameToPlatformMapping file. Updating tests.
package org.eclipse.persistence.jpa.test.ddl;

import jakarta.persistence.EntityManagerFactory;

import org.eclipse.persistence.internal.helper.DBPlatformHelper;
import org.eclipse.persistence.jpa.test.basic.model.Employee;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.logging.DefaultSessionLog;
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
        Assert.assertEquals(org.eclipse.persistence.platform.database.DB2ZPlatform.class.getName(), getPlatformClass("DB2", "10", "DSN10015"));
    }

    @Test
    public void testDB2I() {
        //Returned from jcc driver (DRDA)
        Assert.assertEquals(org.eclipse.persistence.platform.database.DB2MainframePlatform.class.getName(), getPlatformClass("AS", "10", "QSQ07020"));

        //Returned from type 2 native driver & type 4 open source driver (non-DRDA)
        Assert.assertEquals(org.eclipse.persistence.platform.database.DB2MainframePlatform.class.getName(), getPlatformClass("DB2 UDB for AS/400", "07.02.0000", "V7R2m0"));
    }

    @Test
    public void testMySQL() {
        //Returned from jcc driver (DRDA)
        Assert.assertEquals(org.eclipse.persistence.platform.database.MySQLPlatform.class.getName(), getPlatformClass("MySQL", "5", "5.5.5-10.1.33-MariaDB"));
    }

    @Test
    public void testDerby() {
        //Returned from jcc driver (DRDA)
        Assert.assertEquals(org.eclipse.persistence.platform.database.JavaDBPlatform.class.getName(), getPlatformClass("Apache Derby", "10", "10.12.1.1 - (1704137)"));
    }

    @Test
    public void testOracle() {
        //Returned from jcc driver (DRDA)
        Assert.assertEquals("org.eclipse.persistence.platform.database.oracle.OraclePlatform", getPlatformClass("Oracle", "7", "Oracle Database 7c"));
        Assert.assertEquals("org.eclipse.persistence.platform.database.oracle.OraclePlatform", getPlatformClass("Oracle", "8", "Oracle Database 8c"));
        Assert.assertEquals("org.eclipse.persistence.platform.database.oracle.Oracle9Platform", getPlatformClass("Oracle", "9", "Oracle Database 9c"));
        Assert.assertEquals("org.eclipse.persistence.platform.database.oracle.Oracle10Platform", getPlatformClass("Oracle", "10", "Oracle Database 10c"));
        Assert.assertEquals("org.eclipse.persistence.platform.database.oracle.Oracle11Platform", getPlatformClass("Oracle", "11", "Oracle Database 11c"));
        Assert.assertEquals("org.eclipse.persistence.platform.database.oracle.Oracle12Platform", getPlatformClass("Oracle", "12", "Oracle Database 12c"));
        Assert.assertEquals("org.eclipse.persistence.platform.database.oracle.Oracle18Platform", getPlatformClass("Oracle", "18", "Oracle Database 18c"));
    }

    @Test
    public void testHanaDB() {
        //Returned from jcc driver (DRDA)
        Assert.assertEquals(org.eclipse.persistence.platform.database.HANAPlatform.class.getName(), getPlatformClass("HDB", "2", "2.00.040.00.1553674765"));
    }

    private String getPlatformClass(String productName, String minorVersion, String majorVersion){
        return DBPlatformHelper.getDBPlatform(productName, minorVersion, majorVersion, log);
    }
}
