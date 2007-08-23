/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.conversion;

import org.eclipse.persistence.platform.database.oracle.Oracle9Platform;

// This test retrieves all the classes that can be converted from a given class by 
// calling getDataTypesConvertedFrom() in Oracle9Platform. 
public class DataTypesConvertedFromAClassForOracle9Test extends DataTypesConvertedFromAClassTest {

    public DataTypesConvertedFromAClassForOracle9Test() {
        setDescription("Test getDataTypesConvertedFrom() in Oracle9Platform.");
    }

    public void setup() {
        cm = getSession().getPlatform();
    }

    protected boolean isChar(Class aClass) {
        return super.isChar(aClass) || aClass == Oracle9Platform.NCHAR || aClass == Oracle9Platform.NSTRING || 
            aClass == Oracle9Platform.NCLOB;
    }
}
