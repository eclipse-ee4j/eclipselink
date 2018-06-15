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

public class SDOXMLHelperDatatypeShortTestCases extends SDOXMLHelperDatatypeTestCase {

    public SDOXMLHelperDatatypeShortTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.datatype.SDOXMLHelperDatatypeShortTestCases" };
        TestRunner.main(arguments);
    }

    protected Class getDatatypeJavaClass() {
        return Short.class;
    }

    protected SDOType getValueType() {
        return SDOConstants.SDO_SHORT;
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datatype/myShort-1.xml");
    }

    protected String getControlRootURI() {
        return "myShort-NS";
    }

    protected String getControlRootName() {
        return "myShort";
    }

    protected String getSchemaNameForUserDefinedType() {
        return getSchemaLocation() + "myShort.xsd";
    }

    protected String getSchemaNameForBuiltinType() {
        return getSchemaLocation() + "myShort-builtin.xsd";
    }

}
