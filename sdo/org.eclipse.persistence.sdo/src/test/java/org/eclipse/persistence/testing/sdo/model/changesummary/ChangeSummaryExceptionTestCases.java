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
package org.eclipse.persistence.testing.sdo.model.changesummary;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class ChangeSummaryExceptionTestCases extends SDOTestCase {
    public ChangeSummaryExceptionTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryExceptionTestCases" };
        TestRunner.main(arguments);
    }

    public void testManyPropForCS() {
        SDOType changeSummaryType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.CHANGESUMMARY);

        DataObject rootTypeDO = defineType("rootUri", "rootTypeName");
        addProperty(rootTypeDO, "csmProp", changeSummaryType, true, true, true);

        try {
            SDOType rootType = (SDOType)typeHelper.define(rootTypeDO);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().equals("ChangeSummary can not be on a property with many set to true."));
            return;
        }
        fail("An Illegalargument should have occurred");
    }
}
