/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.XSDHelperTestCases;

public class XSDHelperGenerateExceptionTestCases extends XSDHelperTestCases {
    public XSDHelperGenerateExceptionTestCases(String name) {
        super(name);
    }

    public void testGenerateFromNullList() {
        List types = null;
        try {
            xsdHelper.generate(types);
            fail("An IllegalArgument Exception should have occurred.");
        } catch (Exception e) {
        }
    }

        public void testGenerateFromEmptyList() {
        List types = new ArrayList();
        try {
            xsdHelper.generate(types);
            fail("An IllegalArgument Exception should have occurred.");
        } catch (Exception e) {
        }
    }
}
