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
 *     04/24/2015 Will Dazey
 *       - 464641: Test that 2.1 oracle12 platform is correctly returned
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.ddl;

import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.platform.database.OraclePlatform;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestDBPlatformDetector {

    @Emf(name = "oracle12Emf", createTables = DDLGen.NONE, properties = { 
            @Property(name = PersistenceUnitProperties.SCHEMA_DATABASE_PRODUCT_NAME, value = "Oracle"), 
            @Property(name = PersistenceUnitProperties.SCHEMA_DATABASE_MAJOR_VERSION, value = "12"), 
            @Property(name = PersistenceUnitProperties.SCHEMA_DATABASE_MINOR_VERSION, value = "1") })
    private EntityManagerFactory oracle12Emf;

    @Test
    public void testOracle12Platform() {
        Assert.assertTrue(getPlatform(oracle12Emf) instanceof OraclePlatform);
    }

    private DatabasePlatform getPlatform(EntityManagerFactory emf) {
        return ((EntityManagerFactoryImpl)emf).getServerSession().getPlatform();
    }
}
