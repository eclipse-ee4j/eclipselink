/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
// Denise Smith - 2.4
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelements;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XMLElementsWithWrapperTestCases extends JAXBWithJSONTestCases{

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelements/foo-wrapper.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelements/foo-wrapper.json";

    public XMLElementsWithWrapperTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] { Foo.class });
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    public Map getProperties() {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelements/eclipselink-oxm-wrapper.xml");

        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelements",
                        new StreamSource(inputStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY,
                metadataSourceMap);

        return properties;
    }

    public void testSchemaGen() throws Exception {
        List controlSchemas = new ArrayList();
        InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelements/schema_wrapper.xsd");
        controlSchemas.add(is);
        super.testSchemaGen(controlSchemas);

    }

    public void testInstanceDocValidation() {
        InputStream schema = ClassLoader
                .getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelements/schema_wrapper.xsd");
        StreamSource schemaSource = new StreamSource(schema);

        InputStream instanceDocStream = ClassLoader
                .getSystemResourceAsStream(XML_RESOURCE);
        String result = validateAgainstSchema(instanceDocStream, schemaSource);
        assertTrue("Instance doc validation (employee.xml) failed unxepectedly: "+ result, result == null);
    }

    @Override
    protected Object getControlObject() {
        // setup control objects
        Foo foo = new Foo();
        List theItems = new ArrayList();
        theItems.add(new Float(2.5));
        theItems.add(new Integer(1));
        foo.items = theItems;
        return foo;
    }

}
