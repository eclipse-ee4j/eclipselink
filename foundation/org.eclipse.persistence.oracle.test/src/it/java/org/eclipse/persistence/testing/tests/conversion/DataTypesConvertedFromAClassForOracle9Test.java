/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.conversion;

import org.eclipse.persistence.platform.database.oracle.Oracle9Platform;
import org.eclipse.persistence.testing.framework.TestWarningException;

// This test retrieves all the classes that can be converted from a given class by
// calling getDataTypesConvertedFrom() in Oracle9Platform.
public class DataTypesConvertedFromAClassForOracle9Test extends DataTypesConvertedFromAClassTest {

    public DataTypesConvertedFromAClassForOracle9Test() {
        setDescription("Test getDataTypesConvertedFrom() in Oracle9Platform.");
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
