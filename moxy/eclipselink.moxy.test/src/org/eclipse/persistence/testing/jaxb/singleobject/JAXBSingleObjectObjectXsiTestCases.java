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
//     Denise Smith -  July 9, 2009 Initial test
package org.eclipse.persistence.testing.jaxb.singleobject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class JAXBSingleObjectObjectXsiTestCases extends JAXBWithJSONTestCases {

    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/singleobject/singleObjectXsiType.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/singleobject/singleObjectXsiType.json";

    public JAXBSingleObjectObjectXsiTestCases(String name) throws Exception {
        super(name);
        init();
    }

    public void init() throws Exception {
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = Object.class;
        setClasses(classes);
        initXsiType();
    }

    @Override
    protected Map<String, String> getAdditationalNamespaces() {
        Map<String, String> namespaces = new HashMap<>();
        namespaces.put("rootNamespace", "ns0");
        return namespaces;
    }

    public void testSchemaGen() throws Exception {
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        getJAXBContext().generateSchema(outputResolver);

        assertEquals("A Schema was generated but should not have been", 0, outputResolver.getSchemaFiles().size());
    }

    public List<InputStream> getControlSchemaFiles() {
        //not applicable for this test since we override testSchemaGen
        return null;
    }

    public Object getWriteControlObject() {
        return getControlObject();
    }

    protected Object getControlObject() {
        Integer testInteger = 25;
        QName qname = new QName("rootNamespace", "root");
        JAXBElement jaxbElement = new JAXBElement(qname, Object.class, testInteger);
        return jaxbElement;
    }

    protected String getNoXsiTypeControlResourceName() {
        return XML_RESOURCE;
    }

}
