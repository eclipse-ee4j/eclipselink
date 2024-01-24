/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
// rbarkhouse - May 26 2008 - 1.0M8 - Initial implementation

package org.eclipse.persistence.testing.sdo.helper.xmlhelper.datatype;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;

import junit.textui.TestRunner;

public class SDOXMLHelperDatatypeYearMonthDayTestCases extends SDOXMLHelperDatatypeTestCase {

    public SDOXMLHelperDatatypeYearMonthDayTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.datatype.SDOXMLHelperDatatypeYearMonthDayTestCases" };
        TestRunner.main(arguments);
    }

    @Override
    protected Class<?> getDatatypeJavaClass() {
        return String.class;
    }

    @Override
    protected SDOType getValueType() {
        return SDOConstants.SDO_YEARMONTHDAY;
    }

    @Override
    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datatype/myYearMonthDay-1.xml");
    }

    @Override
    protected String getControlRootURI() {
        return "myYearMonthDay-NS";
    }

    @Override
    protected String getControlRootName() {
        return "myYearMonthDay";
    }

    @Override
    protected String getSchemaNameForUserDefinedType() {
        return getSchemaLocation() + "myYearMonthDay.xsd";
    }

    @Override
    protected String getSchemaNameForBuiltinType() {
        return getSchemaLocation() + "myYearMonthDay-builtin.xsd";
    }

}
