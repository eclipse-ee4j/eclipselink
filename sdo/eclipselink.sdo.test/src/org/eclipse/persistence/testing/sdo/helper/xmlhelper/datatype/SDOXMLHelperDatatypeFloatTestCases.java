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
// rbarkhouse - May 26 2008 - 1.0M8 - Initial implementation

package org.eclipse.persistence.testing.sdo.helper.xmlhelper.datatype;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;

import junit.textui.TestRunner;

public class SDOXMLHelperDatatypeFloatTestCases extends SDOXMLHelperDatatypeTestCase {

    public SDOXMLHelperDatatypeFloatTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.datatype.SDOXMLHelperDatatypeFloatTestCases" };
        TestRunner.main(arguments);
    }

    protected Class getDatatypeJavaClass() {
        return Float.class;
    }

    protected SDOType getValueType() {
        return SDOConstants.SDO_FLOAT;
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datatype/myFloat-1.xml");
    }

    protected String getControlRootURI() {
        return "myFloat-NS";
    }

    protected String getControlRootName() {
        return "myFloat";
    }

    protected String getSchemaNameForUserDefinedType() {
        return getSchemaLocation() + "myFloat.xsd";
    }

    protected String getSchemaNameForBuiltinType() {
        return getSchemaLocation() + "myFloat-builtin.xsd";
    }

}
