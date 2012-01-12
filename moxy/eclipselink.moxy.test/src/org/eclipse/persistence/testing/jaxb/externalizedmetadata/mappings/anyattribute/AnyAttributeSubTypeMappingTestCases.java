/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anyattribute;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

/**
 * Tests XmlAnyAttributeMapping via eclipselink-oxm.xml
 *
 */
public class AnyAttributeSubTypeMappingTestCases extends JAXBTestCases {
    
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/anyattribute/subtype-map.xml";
    
    private static final String NAME_1 = "Joe";
    private static final String NAME_2 = "Bob";
    private static final String CHILD1_NAME = "child-1";
    private static final String CHILD2_NAME = "child-2";
    private static final String OTHER_NS = "http://www.example.com/other";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public AnyAttributeSubTypeMappingTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setClasses(new Class[]{XmlAnyAttributeSubTypeMapModel.class});
    }
    
    /**
     * Create the control XmlAnyAttributeSubTypeMapModel.
     */
    public Object getControlObject() {
        XmlAnyAttributeSubTypeMapModel anyAttributeSubTypeMapModel = new XmlAnyAttributeSubTypeMapModel();
        HashMap children = new HashMap();
        QName qname = new QName(OTHER_NS, CHILD1_NAME);
        children.put(qname, NAME_1);
        qname = new QName(OTHER_NS, CHILD2_NAME);
        children.put(qname, NAME_2);
        anyAttributeSubTypeMapModel.children = children;
        return anyAttributeSubTypeMapModel;
    }
    
    public Map getProperties(){
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/anyattribute/subtype-map-oxm.xml");
    
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anyattribute", new StreamSource(inputStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
    
        return properties;
    }
    
    public void testSchemaGen() throws Exception{
        List controlSchemas = new ArrayList();
        InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/anyattribute/subtype-map.xsd");
        controlSchemas.add(is);
    
        super.testSchemaGen(controlSchemas);
    }
}

