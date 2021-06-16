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
// rbarkhouse - May 26 2008 - 1.0M8 - Initial implementation

package org.eclipse.persistence.testing.sdo.helper.xmlhelper.datatype;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;

import commonj.sdo.DataObject;

import junit.textui.TestRunner;

public class SDOXMLHelperDatatypeBooleanTestCases extends SDOXMLHelperDatatypeTestCase {

    public SDOXMLHelperDatatypeBooleanTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.datatype.SDOXMLHelperDatatypeBooleanTestCases" };
        TestRunner.main(arguments);
    }

    protected Class getDatatypeJavaClass() {
        return Boolean.class;
    }

    protected SDOType getValueType() {
        return SDOConstants.SDO_BOOLEAN;
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datatype/myBoolean-1.xml");
    }

    protected String getControlRootURI() {
        return "myBoolean-NS";
    }

    protected String getControlRootName() {
        return "myBoolean";
    }

    protected String getSchemaNameForUserDefinedType() {
        return getSchemaLocation() + "myBoolean.xsd";
    }

    protected String getSchemaNameForBuiltinType() {
        return getSchemaLocation() + "myBoolean-builtin.xsd";
    }

}
