/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 09 October 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlidref;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlidref.subpackage.Super;

public class XmlIdRefTwoPackagesTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlidref/subpackage/root.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlidref/subpackage/root.json";
    private static final String BINDINGS_RESOURCE_1 = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlidref/subpackage/bindings1.xml";
    private static final String BINDINGS_RESOURCE_2 = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlidref/subpackage/bindings2.xml";

    private static final String PACKAGE_1 = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlidref";
    private static final String PACKAGE_2 = PACKAGE_1 + ".subpackage";

    public XmlIdRefTwoPackagesTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[] { RootTwo.class, Super.class, Sub.class });
    }

    @Override
    protected Object getControlObject() {
        RootTwo root = new RootTwo();

        Sub sub1 = new Sub(); sub1.setName("SUB-1"); sub1.setValue(111);
        Sub sub2 = new Sub(); sub2.setName("SUB-2"); sub2.setValue(222);
        Sub sub3 = new Sub(); sub3.setName("SUB-3"); sub3.setValue(333);

        root.getRefList().add(sub1);
        root.getRefList().add(sub2);
        root.getRefList().add(sub3);
        root.setRef(sub2);

        return root;
    }

    @Override
    protected Map getProperties() throws JAXBException {
        InputStream inputStream1 = ClassLoader.getSystemResourceAsStream(BINDINGS_RESOURCE_1);
        InputStream inputStream2 = ClassLoader.getSystemResourceAsStream(BINDINGS_RESOURCE_2);

        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(PACKAGE_1, new StreamSource(inputStream1));
        metadataSourceMap.put(PACKAGE_2, new StreamSource(inputStream2));

        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataSourceMap);

        return properties;
    }

}
