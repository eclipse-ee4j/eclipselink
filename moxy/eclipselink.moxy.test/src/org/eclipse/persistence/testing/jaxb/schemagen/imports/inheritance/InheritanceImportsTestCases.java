/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
// Denise Smith- 2.4
package org.eclipse.persistence.testing.jaxb.schemagen.imports.inheritance;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.imports.inheritance.child.Child;

public class InheritanceImportsTestCases extends JAXBWithJSONTestCases{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/schemagen/imports/inheritance/child.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/schemagen/imports/inheritance/child.json";

    public InheritanceImportsTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Child.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);


        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("childnamespace","ns0");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, namespaces);
    }

    protected JAXBMarshaller getJSONMarshaller() throws Exception {
        JAXBMarshaller jsonMarshaller = (JAXBMarshaller) jaxbContext.createMarshaller();
        jsonMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("childnamespace","ns0");
        jsonMarshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, namespaces);
        return jsonMarshaller;

    }

    protected Object getControlObject() {
        Child c = new Child();
        c.childThing = "childValue";
        c.parentThing = "parentValue";
        return c;
    }

    public void testSchemaGen() throws Exception{
        List<InputStream> controlSchemas = new ArrayList<InputStream>();
        InputStream is1 =getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/schemagen/imports/inheritance/schema.xsd");
        InputStream is2 = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/schemagen/imports/inheritance/schema2.xsd");
        controlSchemas.add(is1);
        controlSchemas.add(is2);
        super.testSchemaGen(controlSchemas);
    }


}
