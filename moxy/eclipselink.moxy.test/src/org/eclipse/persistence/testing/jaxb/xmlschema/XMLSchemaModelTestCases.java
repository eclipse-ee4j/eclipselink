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
 * Denise Smith - October 2011
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlschema;

import org.eclipse.persistence.testing.jaxb.xmlschema.model.*;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;


public class XMLSchemaModelTestCases extends JAXBTestCases{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlschema/xmlschemadoc.xml";

	public XMLSchemaModelTestCases(String name) throws Exception {
		super(name);
		setClasses(new Class[]{Schema.class});
		setControlDocument(XML_RESOURCE); 
	}

	protected Object getControlObject() {
		Schema xmlschema = new Schema();
		
		TopLevelComplexType ct = new TopLevelComplexType();
		ct.setName("testComplexType");
		xmlschema.setAttributeFormDefault(FormChoice.QUALIFIED);
		xmlschema.setTargetNamespace("someTargetNamespace");
		
		xmlschema.getSimpleTypeOrComplexTypeOrGroup().add(ct);
		return xmlschema;
	}

	public void xmlToObjectTest(Object testObject) throws Exception {
        log("\n**xmlToObjectTest**");
        log("Expected:");
        log(getReadControlObject().toString());
        log("Actual:");     
        log(testObject.toString());
        
        assertTrue(testObject instanceof Schema);
        assertTrue(((Schema)testObject).getAttributeFormDefault().equals(FormChoice.QUALIFIED));
        assertEquals("someTargetNamespace", ((Schema)testObject).getTargetNamespace());
        assertNotNull(((Schema)testObject).getSimpleTypeOrComplexTypeOrGroup());
        assertEquals(1, ((Schema)testObject).getSimpleTypeOrComplexTypeOrGroup().size());
        OpenAttrs item =((Schema)testObject).getSimpleTypeOrComplexTypeOrGroup().get(0);
        assertTrue(item instanceof TopLevelComplexType);
        assertEquals("testComplexType", ((TopLevelComplexType)item).getName());
	}

}
