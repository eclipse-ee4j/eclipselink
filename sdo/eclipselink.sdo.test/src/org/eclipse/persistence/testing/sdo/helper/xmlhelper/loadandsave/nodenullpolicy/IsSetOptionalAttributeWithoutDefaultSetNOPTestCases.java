/* Copyright (c) 2007, Oracle. All rights reserved. */
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nodenullpolicy;

import junit.textui.TestRunner;

import commonj.sdo.helper.XMLDocument;
// TODO: How is this test different from 3-1 set(non-null) == set(default)
public class IsSetOptionalAttributeWithoutDefaultSetNOPTestCases extends IsSetOptionalAttributeWithoutDefaultTestCases {
	
	// UC 1-2
	/*
	<xsd:element name='employee'>
	<xsd:complexType>
		<xsd:attribute name="id" type="xsd:int"/>
		<xsd:attribute name="firstname" type="xsd:string"/>
		<xsd:attribute name="lastname" type="xsd:string"/>
	</xsd:complexType>
	</xsd:element>

	Use Case #1-2 - Is Set == False (NOP)
	Unmarshal From					fn Property						Marshal To
	<employee/>					Get = null	IsSet = false	<employee/>
	 */
    public IsSetOptionalAttributeWithoutDefaultSetNOPTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
    }

    protected String getControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/nodenullpolicy/IsSetOptionalAttributeWithoutDefaultSetNOP.xml";
    }

    protected String getControlWriteFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/nodenullpolicy/IsSetOptionalAttributeWithoutDefaultSetNOPWrite.xml";
    }

    protected String getNoSchemaControlWriteFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/nodenullpolicy/IsSetOptionalAttributeWithoutDefaultSetNOPWriteNoSchema.xml";
    }

    protected String getNoSchemaControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/nodenullpolicy/IsSetOptionalAttributeWithoutDefaultSetNOPNoSchema.xml";
    }

    protected void verifyAfterLoad(XMLDocument doc) {
        super.verifyAfterLoad(doc);
        Object value = doc.getRootObject().get(ID_NAME);
        boolean isSet = doc.getRootObject().isSet(ID_NAME);
        // verify defaults
        // TODO: this test case will fail for noSchemaLoad until we resolve #6151874 Jira-253
        assertEquals(0, value);        
        assertNotNull(value);
        assertFalse(isSet);

        value = doc.getRootObject().get(FIRSTNAME_NAME);
        isSet = doc.getRootObject().isSet(FIRSTNAME_NAME);
        //assertEquals(FIRSTNAME_DEFAULT, value);
        assertNull(value);
        assertFalse(isSet);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nodenullpolicy.IsSetOptionalAttributeWithoutDefaultSetNOPTestCases" };
        TestRunner.main(arguments);
    }
}