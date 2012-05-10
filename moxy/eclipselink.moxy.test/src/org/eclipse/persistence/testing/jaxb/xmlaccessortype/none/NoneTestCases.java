/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlaccessortype.none;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class NoneTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlaccessortype/none.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlaccessortype/none.json";
    private static final String BINDING_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlaccessortype/none-binding.xml";

    public NoneTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);

        Class[] classes = new Class[1];
        classes[0] = Root.class;
        setClasses(classes);
    }

    @Override
    protected Object getControlObject() {
        Root root = new Root();

        Mapped mapped = new Mapped();
        mapped.setValue("TEST");
        root.setMapped(mapped);

        root.setUnmapped(null);

        return root;
    }

    public void testXmlMetadata() throws Exception {
        Class[] classes = new Class[1];
        classes[0] = Root.class;

        Map<String, Object> properties = new HashMap<String, Object>(1);
        InputStream binding = Thread.currentThread().getContextClassLoader().getResourceAsStream(BINDING_RESOURCE);
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, binding);

        try {
            JAXBContextFactory.createContext(classes, properties);
        } catch(JAXBException e) {
            return;
        }
        fail("A JAXBException should have been thrown because Unmapped does not contain a no-arg constructor.");
    }

}