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
// rbarkhouse - May 26 2008 - 1.0M8 - Initial implementation

package org.eclipse.persistence.testing.sdo.helper.xmlhelper.datatype;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;

import junit.textui.TestRunner;

public class SDOXMLHelperDatatypeLongTestCases extends SDOXMLHelperDatatypeTestCase {

    public SDOXMLHelperDatatypeLongTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.datatype.SDOXMLHelperDatatypeLongTestCases" };
        TestRunner.main(arguments);
    }

    protected Class getDatatypeJavaClass() {
        return Long.class;
    }

    protected SDOType getValueType() {
        return SDOConstants.SDO_LONG;
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datatype/myLong-1.xml");
    }

    protected String getControlRootURI() {
        return "myLong-NS";
    }

    protected String getControlRootName() {
        return "myLong";
    }

    protected String getSchemaNameForUserDefinedType() {
        return getSchemaLocation() + "myLong.xsd";
    }

    protected String getSchemaNameForBuiltinType() {
        return getSchemaLocation() + "myLong-builtin.xsd";
    }

}
