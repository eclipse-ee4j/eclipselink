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
 *     02/19/2015 - Rick Curtis  
 *       - 458877 : Add national character support
 *****************************************************************************/
package org.eclipse.persistence.config;

import org.eclipse.persistence.exceptions.ConversionException;
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

}