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
//     03/04/2015 - William Dazey
//       - 460862 : Added support for schema generation with persistence units with JTA transaction type.
package org.eclipse.persistence.jpa.test.ddl;

import java.util.Properties;
import javax.persistence.Persistence;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.EntityManagerSetupException;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestSchemaGen {
    private static final String jtaPersistenceUnit = "jta-pu";

    public static Throwable getBaseException(Exception e) {
        Throwable base = e;
        while (base.getCause() != null) {
            base = base.getCause();
        }
        return base;
    }

    /**
     * This test checks that PersistenceUnits with JTA transaction types can be
     * used to generate schema given that the 2.1 required properties are
     * included: javax.persistence.database-product-name
     * javax.persistence.database-major-version
     * javax.persistence.database-minor-version
     */
    @Test
    public void testJTASchemaGenerationWithProps() throws Exception {
        Properties map = new Properties();
        //map.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION, PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_ACTION);
        //map.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, out.getAbsolutePath());
        map.put(PersistenceUnitProperties.SCHEMA_DATABASE_PRODUCT_NAME, "Oracle");
        map.put(PersistenceUnitProperties.SCHEMA_DATABASE_MAJOR_VERSION, "10");
        map.put(PersistenceUnitProperties.SCHEMA_DATABASE_MINOR_VERSION, "1");
        Exception e = null;
        try {
            Persistence.generateSchema(jtaPersistenceUnit, map);
        } catch (Exception pe) {
            e = pe;
        }
        Assert.assertNull(e);
    }

    /**
     * This test checks the inverse of
     * TestSchemaGen.testJTASchemaGenerationWithProps() Without the 2.1
     * properties defined, generating schema should fail with no JTA-DS defined.
     */
    @Test
    public void testJTASchemaGenerationWithoutProps() throws Exception {
        Exception e = null;
        try {
            Persistence.generateSchema(jtaPersistenceUnit, new Properties());
        } catch (Exception pe) {
            e = pe;
        }
        Assert.assertNotNull(e);
        Throwable base = getBaseException(e);
        Assert.assertTrue(base instanceof EntityManagerSetupException);
    }
}
