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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nodenullpolicy;

import junit.textui.TestRunner;

import commonj.sdo.helper.XMLDocument;

public class IsSetNillableWithoutDefaultSetNonNullTestCases extends IsSetNillableWithoutDefaultTestCases {

    // UC 2-1
    // test both primitives (Integer wraps int) and string for DirectMappings
    /*
    <xsd:element name='employee'>
    <xsd:complexType><xsd:sequence>
        <xsd:element name=''id" type='xsd:int' nillable='true'/>
        <xsd:element name='firsname' type='xsd:string' nillable='true'/>
    </xsd:sequence></xsd:complexType>
    </xsd:element>

    Use Case #2-1 - set non null
    Unmarshal From                    fn Property                                    Marshal To
    <employee/>                        Get = 'John'    IsSet = false        <employee/>
     */
    public IsSetNillableWithoutDefaultSetNonNullTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
    }

    protected String getControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/nodenullpolicy/IsSetNillableWithoutDefaultSetNonNull.xml";
    }

    protected String getControlWriteFileName() {
        return getControlFileName();
    }

    protected String getNoSchemaControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/nodenullpolicy/IsSetNillableWithoutDefaultSetNonNullNoSchema.xml";
    }

    protected String getNoSchemaControlWriteFileName() {
        return getNoSchemaControlFileName();
    }

    protected void verifyAfterLoad(XMLDocument doc) {
        super.verifyAfterLoad(doc);
        Object value = doc.getRootObject().get(ID_NAME);
        boolean isSet = doc.getRootObject().isSet(ID_NAME);
        // verify defaults
        assertNotNull(value);
        assertNotSame(ID_DEFAULT, value);
        assertEquals(99, value);
        assertTrue(isSet);

        value = doc.getRootObject().get(FIRSTNAME_NAME);
        isSet = doc.getRootObject().isSet(FIRSTNAME_NAME);
        assertNotNull(value);
        assertNotSame(FIRSTNAME_DEFAULT, value);
        assertEquals("nonnull", value);
        assertTrue(isSet);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nodenullpolicy.IsSetNillableWithoutDefaultSetNonNullTestCases" };
        TestRunner.main(arguments);
    }
}
