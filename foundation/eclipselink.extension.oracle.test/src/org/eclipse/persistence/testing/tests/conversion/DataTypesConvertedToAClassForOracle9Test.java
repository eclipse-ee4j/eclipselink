/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.conversion;

import java.util.Calendar;

import java.sql.*;

import java.math.*;

import org.eclipse.persistence.platform.database.oracle.Oracle9Platform;
import org.eclipse.persistence.testing.framework.TestWarningException;

//This test retrieves all the classes that can be converted to a given class by
//calling getDataTypesConvertedTo() in Oracle9Platform.
public class DataTypesConvertedToAClassForOracle9Test extends DataTypesConvertedToAClassTest {

    protected Class[] convertedToClasses =
        new Class[] { BigDecimal.class, BigInteger.class, Boolean.class, Byte.class, byte[].class, Byte[].class,
                      Calendar.class, Character.class, Character[].class, char[].class, java.sql.Date.class, Double.class,
                      Float.class, Integer.class, Long.class, Number.class, Short.class, String.class, Timestamp.class,
                      Time.class, java.util.Date.class, oracle.sql.TIMESTAMP.class };

    public DataTypesConvertedToAClassForOracle9Test() {
        setDescription("Test getDataTypesConvertedTo() in Oracle9Platform.");
    }

    public void setup() {
        if(!getSession().getPlatform().isOracle()) {
            throw new TestWarningException("This test requires Oracle database");
        }
        cm = getSession().getPlatform();
    }

    protected boolean isChar(Class aClass) {
        return super.isChar(aClass) || aClass == Oracle9Platform.NCHAR || aClass == Oracle9Platform.NSTRING ||
            aClass == Oracle9Platform.NCLOB;
    }
}
