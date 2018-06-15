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
//     02/19/2015 - Rick Curtis
//       - 458877 : Add national character support
//     03/06/2015-2.7.0 Dalia Abo Sheasha
//       - 461607: PropertiesUtils does not process methods with String parameters correclty.
package org.eclipse.persistence.config;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.platform.database.DB2Platform;
import org.eclipse.persistence.platform.database.MySQLPlatform;
import org.junit.Assert;
import org.junit.Test;

public class TestPropertiesUtils {

    @Test
    public void testSetInToDatabasePlatform() {
        MySQLPlatform db = new MySQLPlatform();
        Assert.assertFalse(db.shouldForceFieldNamesToUpperCase());
        Assert.assertTrue(db.shouldBindLiterals());

        PropertiesUtils.set(db, "eclipselink.target-database", "ShouldForceFieldNamesToUpperCase=true, shouldbindliterals = false");

        Assert.assertTrue(db.shouldForceFieldNamesToUpperCase());
        Assert.assertFalse(db.shouldBindLiterals());

    }

    @Test
    public void testInvalidPropertyType() {
        // StringBindingSize is a property, but it is a number
        String config = "StringBindingSize=one";
        MySQLPlatform db = new MySQLPlatform();
        try {
            PropertiesUtils.set(db, "eclipselink.target-database", config);
            Assert.fail();
        } catch (ConversionException ce) {
            // expected
        }
    }

    @Test
    public void testInvalidConfiguration() {
        MySQLPlatform db = new MySQLPlatform();

        try {
            PropertiesUtils.set(db, "eclipselink.target-database", "ShouldForceFieldNamesToUpperCase");
            Assert.fail();
        } catch (ConversionException ce) {
            // expected
        }
        try {
            PropertiesUtils.set(db, "eclipselink.target-database", "==");
            Assert.fail();
        } catch (ConversionException ce) {
            // expected
        }
        try {
            PropertiesUtils.set(db, "eclipselink.target-database", "");
        } catch (ConversionException ce) {
            Assert.fail();
        }
        try {
            PropertiesUtils.set(db, "eclipselink.target-database", ",,ShouldForceFieldNamesToUpperCase=true");
            Assert.fail();
        } catch (ConversionException ce) {
            // expected
        }

    }

    @Test
    public void testStoredProcedureTerminationToken() {
        DB2Platform db2 = new DB2Platform();
        Assert.assertTrue(db2.getStoredProcedureTerminationToken().equals(";"));

        String token = "test";
        PropertiesUtils.set(db2, PersistenceUnitProperties.TARGET_DATABASE_PROPERTIES, "StoredProcedureTerminationToken=" + token);

        Assert.assertTrue(db2.getStoredProcedureTerminationToken().equals(token));
    }
}
