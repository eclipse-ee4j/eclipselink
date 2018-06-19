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
//     Denise Smith - 2.4 - February 11, 2013
package org.eclipse.persistence.testing.jaxb.xmlattribute.imports;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.xmlattribute.imports2.IdentifierType;

public class XmlAttributeImportsTestCases extends JAXBWithJSONTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlattribute/imports.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlattribute/imports.json";
    private final static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlattribute/imports.xsd";
    private final static String XSD_RESOURCE2 = "org/eclipse/persistence/testing/jaxb/xmlattribute/imports2.xsd";

    public XmlAttributeImportsTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{Person.class});
    }

    protected Object getControlObject() {
        Person obj = new Person();
        obj.name = "theName";
        obj.setId(IdentifierType.thirdThing);
        return obj;
    }

    public void testSchemaGen() throws Exception{
        List<InputStream> controlSchemas = new ArrayList<InputStream>();
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(XSD_RESOURCE);
        InputStream is2 = Thread.currentThread().getContextClassLoader().getResourceAsStream(XSD_RESOURCE2);
        controlSchemas.add(is);
        controlSchemas.add(is2);
        super.testSchemaGen(controlSchemas);
    }
}
