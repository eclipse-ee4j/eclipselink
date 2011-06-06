/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nodenullpolicy;

import junit.textui.TestRunner;
import static org.eclipse.persistence.sdo.SDOConstants.*;
import commonj.sdo.helper.XMLDocument;
public class IsSetOptionalWithoutDefaultSetNOPNumericPrimsTestCases extends IsSetOptionalWithoutDefaultNumericPrimsTestCases {
	
	// UC 
	/*
	<xsd:element name='employee'>
	<xsd:complexType><xsd:sequence>
		<xsd:element name='fn' type='xsd:string'/>
	</xsd:sequence></xsd:complexType>
	</xsd:element>

	Use Case # - Is Set == False (NOP)
	Unmarshal From					fn Property						Marshal To
	<employee/>					Get = null	IsSet = false	<employee/>
	 */
    public IsSetOptionalWithoutDefaultSetNOPNumericPrimsTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
    }

    protected String getControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/nodenullpolicy/IsSetOptionalWithoutDefaultSetNOPNumericPrims.xml";
    }

    protected String getControlWriteFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/nodenullpolicy/IsSetOptionalWithoutDefaultSetNOPNumericPrimsWrite.xml";
    }

    protected String getNoSchemaControlWriteFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/nodenullpolicy/IsSetOptionalWithoutDefaultSetNOPNumericPrimsWriteNoSchema.xml";
    }

    protected String getNoSchemaControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/nodenullpolicy/IsSetOptionalWithoutDefaultSetNOPNumericPrimsNoSchema.xml";
    }

    protected void verifyAfterLoad(XMLDocument doc) {
        super.verifyAfterLoad(doc);
        Object value = doc.getRootObject().get(ID_NAME);
        boolean isSet = doc.getRootObject().isSet(ID_NAME);
        // verify defaults
        assertEquals(0, value);        
        assertNotNull(value);
        assertFalse(isSet);

        value = doc.getRootObject().get(FIRSTNAME_NAME);
        isSet = doc.getRootObject().isSet(FIRSTNAME_NAME);
        //assertEquals(FIRSTNAME_DEFAULT, value);
        assertNull(value);
        assertFalse(isSet);
        
        // check numeric primitives
        value = doc.getRootObject().get("int_prop");
        isSet = doc.getRootObject().isSet("int_prop");
        assertEquals(INTEGER_DEFAULT, value);
        assertNotNull(value);
        assertFalse(isSet);

        value = doc.getRootObject().get("long_prop");
        isSet = doc.getRootObject().isSet("long_prop");
        assertEquals(LONG_DEFAULT, value);
        assertNotNull(value);
        assertFalse(isSet);

        value = doc.getRootObject().get("float_prop");
        isSet = doc.getRootObject().isSet("float_prop");
        assertEquals(FLOAT_DEFAULT, value);
        assertNotNull(value);
        assertFalse(isSet);

        value = doc.getRootObject().get("double_prop");
        isSet = doc.getRootObject().isSet("double_prop");
        assertEquals(DOUBLE_DEFAULT, value);
        assertNotNull(value);
        assertFalse(isSet);

        value = doc.getRootObject().get("boolean_prop");
        isSet = doc.getRootObject().isSet("boolean_prop");
        assertEquals(BOOLEAN_DEFAULT, value);
        assertNotNull(value);
        assertFalse(isSet);

        value = doc.getRootObject().get("short_prop");
        isSet = doc.getRootObject().isSet("short_prop");
        assertEquals(SHORT_DEFAULT, value);
        assertNotNull(value);
        assertFalse(isSet);

        value = doc.getRootObject().get("byte_prop");
        isSet = doc.getRootObject().isSet("byte_prop");
        assertEquals(BYTE_DEFAULT, value);
        assertNotNull(value);
        assertFalse(isSet);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nodenullpolicy.IsSetOptionalWithoutDefaultSetNOPNumericPrimsTestCases" };
        TestRunner.main(arguments);
    }
}
