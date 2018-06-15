/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - mmacivor - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.inheritance;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlAccessorTypeInheritanceTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlaccessortype/b.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlaccessortype/b.json";
    private static final String BINDINGS = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlaccessortype/inheritance/eclipselink-oxm.xml";
    private static final String CTX_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.inheritance";

    /**
     * This is the preferred (and only) constructor.
     *
     * @param name
     */
    public XmlAccessorTypeInheritanceTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] { A.class, B.class });
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    protected Object getControlObject() {
        return new B();
    }

    public Object getWriteControlObject() {
        return new B();
    }

    public Map getProperties() {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(BINDINGS);

        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CTX_PATH, new StreamSource(inputStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataSourceMap);

        return properties;
    }

    public void testRoundTrip() throws Exception {
        // Not applicable since id is a write only mapping
    }

}
