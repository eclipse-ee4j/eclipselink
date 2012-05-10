/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnametransformer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlMappingDefaultNameTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlnametransformer/XmlMappingDefaulName.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlnametransformer/XmlMappingDefaulName.json";
    private static final String OXM_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlnametransformer/XmlMappingDefaulName.oxm";
    private static final String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlnametransformer/XmlMappingDefaulName.xsd";

    public XmlMappingDefaultNameTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {XmlMappingDefaultNameRoot.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    @Override
    protected XmlMappingDefaultNameRoot getControlObject() {
        XmlMappingDefaultNameRoot root = new XmlMappingDefaultNameRoot();
        root.setAttribute("ATTRIBUTE");
        root.setElement("ELEMENT");
        return root;
    }

    @Override
    protected Map getProperties() {
        Map<String, Object> properties = new HashMap<String, Object>(1);
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, OXM_RESOURCE);
        return properties;
    }

    public void testSchemaGen() throws Exception {
        List<InputStream> controlSchemas = new ArrayList<InputStream>(1);
        InputStream is = ClassLoader.getSystemResourceAsStream(XSD_RESOURCE);
        controlSchemas.add(is);
        super.testSchemaGen(controlSchemas);
    }

}
