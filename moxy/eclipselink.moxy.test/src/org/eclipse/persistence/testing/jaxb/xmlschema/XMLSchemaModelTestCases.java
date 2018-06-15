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
// Denise Smith - October 2011
package org.eclipse.persistence.testing.jaxb.xmlschema;

import org.eclipse.persistence.testing.jaxb.xmlschema.model.*;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;


public class XMLSchemaModelTestCases extends JAXBWithJSONTestCases{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlschema/xmlschemadoc.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlschema/xmlschemadoc.json";

    public XMLSchemaModelTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{ObjectFactory.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
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

    public void jsonToObjectTest(Object testObject) throws Exception {
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
