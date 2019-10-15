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
//     Denise Smith, February 2013
package org.eclipse.persistence.testing.jaxb.xmlinverseref.bindings;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class InverseWriteableBindingsTestCases extends JAXBWithJSONTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlinverseref/bindings/inverseRefWrite.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlinverseref/bindings/inverseRefWrite.json";
    private final static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlinverseref/bindings/inverseRefWrite.xsd";

    public InverseWriteableBindingsTestCases(String name) throws Exception {
        super(name);
        setControlJSON(JSON_RESOURCE);
        setControlDocument(XML_RESOURCE);
        setClasses(new Class[]{Person.class});

    }

    @Override
    protected Map getProperties() {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/xmlinverseref/bindings/inverseref-write-oxm.xml");

        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.xmlinverseref.bindings", new StreamSource(inputStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        return properties;
    }

    @Override
    protected Object getControlObject() {
        Person p = new Person();
        p.name = "theName";
        Address addr = new Address();
        addr.street = "theStreet";
        addr.owner = p;
        p.addr = addr;
        return addr;
    }

    public void testSchemaGen() throws Exception{
        List<InputStream> controlSchemas = new ArrayList<InputStream>();
        controlSchemas.add(getClass().getClassLoader().getResourceAsStream(XSD_RESOURCE));
        super.testSchemaGen(controlSchemas);
    }

}
