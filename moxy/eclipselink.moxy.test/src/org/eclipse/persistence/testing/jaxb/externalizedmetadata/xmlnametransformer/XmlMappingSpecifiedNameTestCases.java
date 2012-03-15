/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlMappingSpecifiedNameTestCases  extends JAXBTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlnametransformer/XmlMappingSpecifiedName.xml";
    private static final String OXM_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlnametransformer/XmlMappingSpecifiedName.oxm";
    private static final String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlnametransformer/XmlMappingSpecifiedName.xsd";

    public XmlMappingSpecifiedNameTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {XmlMappingSpecifiedNameRoot.class});
        setControlDocument(XML_RESOURCE);
    }

    @Override
    protected XmlMappingSpecifiedNameRoot getControlObject() {
        XmlMappingSpecifiedNameRoot root = new XmlMappingSpecifiedNameRoot();
        root.setAttribute("ATTRIBUTE");
        root.setElement("ELEMENT");
        return root;
    }

    @Override
    protected Map getProperties() throws Exception {
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
