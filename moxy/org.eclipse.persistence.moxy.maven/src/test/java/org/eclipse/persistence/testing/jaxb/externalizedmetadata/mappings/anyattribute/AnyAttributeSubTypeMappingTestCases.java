/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anyattribute;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 * Tests XmlAnyAttributeMapping via eclipselink-oxm.xml
 *
 */
public class AnyAttributeSubTypeMappingTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/anyattribute/subtype-map.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/anyattribute/subtype-map.json";
    private static final String JSON_RESOURCE2 = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/anyattribute/subtype-map2.json";

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
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{XmlAnyAttributeSubTypeMapModel.class});

        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("http://www.example.com/other", "ns0");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, namespaces);
    }

    public JAXBMarshaller getJSONMarshaller() throws Exception{
        JAXBMarshaller jsonMarshaller = (JAXBMarshaller) jaxbContext.createMarshaller();
        jsonMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("http://www.example.com/other", "ns0");

        jsonMarshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, namespaces);
        return jsonMarshaller;

      }

    /**
     * Create the control XmlAnyAttributeSubTypeMapModel.
     */
    public Object getControlObject() {
        XmlAnyAttributeSubTypeMapModel anyAttributeSubTypeMapModel = new XmlAnyAttributeSubTypeMapModel();
        LinkedHashMap children = new LinkedHashMap();
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

    protected void compareStrings(String test, String testString) {
          log(test);
          if(shouldRemoveEmptyTextNodesFromControlDoc()){
              log("Expected (With All Whitespace Removed):");
          }else{
                log("Expected");
          }

          String expectedString = loadFileToString(JSON_RESOURCE);
          if(shouldRemoveEmptyTextNodesFromControlDoc()){
              expectedString = expectedString.replaceAll("[ \b\t\n\r ]", "");
          }
          log(expectedString);
          if(shouldRemoveEmptyTextNodesFromControlDoc()){
              log("\nActual (With All Whitespace Removed):");
          }else{
              log("\nActual");
          }

          if(shouldRemoveEmptyTextNodesFromControlDoc()){
              testString = testString.replaceAll("[ \b\t\n\r]", "");
          }
          log(testString);

          if(expectedString.equals(testString)){
              return;
          }
          //try second document with values in a different order
          expectedString = loadFileToString(JSON_RESOURCE2);
          if(shouldRemoveEmptyTextNodesFromControlDoc()){
               expectedString = expectedString.replaceAll("[ \b\t\n\r ]", "");
          }
          log(expectedString);
          if(shouldRemoveEmptyTextNodesFromControlDoc()){
              log("\nActual (With All Whitespace Removed):");
          }else{
              log("\nActual");
          }
          assertEquals(expectedString, testString);
    }
}

