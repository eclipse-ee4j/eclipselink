/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     03/19/2015 - Rick Curtis
 *       - 462586 : Add national character support for z/OS.
 *****************************************************************************/
package org.eclipse.persistence.jpa.test.ddl;

import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.internal.helper.DBPlatformHelper;
import org.eclipse.persistence.jpa.test.basic.model.Employee;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.logging.DefaultSessionLog;
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
        Assert.assertNotEquals(DatabasePlatform.class, dbClass);
    }

    @Test
    public void testDB2ZOS() {
        String platformClass = DBPlatformHelper.getDBPlatform("db2dsn", log);
        Assert.assertEquals(DB2ZPlatform.class.getName(), platformClass);
    }
}
