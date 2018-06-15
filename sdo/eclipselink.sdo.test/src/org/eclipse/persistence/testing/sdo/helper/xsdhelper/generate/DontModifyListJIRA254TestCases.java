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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.SDOTestCase;
import junit.textui.TestRunner;

public class DontModifyListJIRA254TestCases extends SDOTestCase {
    public DontModifyListJIRA254TestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate.DontModifyListJIRA254TestCases" };
        TestRunner.main(arguments);
    }

    public void testListIsNotModified() {
        SDOType typeType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.TYPE);

        DataObject childTypeDO = dataFactory.create(typeType);
        childTypeDO.set("uri", "someUri");
        childTypeDO.set("name", "childName");
        addProperty(childTypeDO, "name", SDOConstants.SDO_STRING, false, false, true);
        Type childType = typeHelper.define(childTypeDO);

        DataObject testTypeDO = dataFactory.create(typeType);
        testTypeDO.set("uri", "someUri");
        testTypeDO.set("name", "testName");
        addProperty(testTypeDO, "id", SDOConstants.SDO_STRING, false, false, true);
        addProperty(testTypeDO, "childType", childType, true, false, true);
        Type testType = typeHelper.define(testTypeDO);

        List types = new ArrayList();
        types.add(testType);
        assertEquals(1, types.size());
        xsdHelper.generate(types);
        assertEquals(1, types.size());

    }
}
