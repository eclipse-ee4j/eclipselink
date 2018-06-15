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
//  - rbarkhouse - 18 December 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.typemappinginfo;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;
import org.eclipse.persistence.oxm.XMLConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class NodeTestCases extends TypeMappingInfoTestCases {

    private final static String TNS = "testNS";
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/node.xml";
    protected final static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/node.xsd";

    public NodeTestCases(String name) throws Exception {
        super(name);
        init();
    }

    public void init() throws Exception {
        setControlDocument(XML_RESOURCE);
        setTypeMappingInfos(getTypeMappingInfos());
    }

    protected TypeMappingInfo[] getTypeMappingInfos() throws Exception {
        if (typeMappingInfos == null) {
            typeMappingInfos = new TypeMappingInfo[1];
            TypeMappingInfo t1 = new TypeMappingInfo();
            t1.setType(Element.class);
            t1.setElementScope(ElementScope.Global);
            t1.setXmlTagName(new QName(TNS, "processAny"));
            typeMappingInfos[0] = t1;
        }
        return typeMappingInfos;

    }

    protected Object getControlObject() {
        QName qname = new QName(TNS, "root");
        JAXBElement jaxbElement = new JAXBElement(qname, Element.class, responseWrapper());
        return jaxbElement;
    }

    public Map<String, InputStream> getControlSchemaFiles() {
        InputStream instream = ClassLoader.getSystemResourceAsStream(XSD_RESOURCE);
        Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
        controlSchema.put(TNS, instream);
        return controlSchema;
    }
    // ========================================================================
    private Element responseWrapper() {
        try {
            Document doc = parser.parse(ClassLoader.getSystemResourceAsStream(XML_RESOURCE));
            return doc.getDocumentElement();
        } catch (Exception e) {
            return null;
        }
    }

    public void testXMLToObjectFromSAXSourceWithTypeMappingInfoXML() throws Exception {
        InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
        SAXSource ss = new SAXSource(new InputSource(instream));
        Object testObject = ((JAXBUnmarshaller)jaxbUnmarshaller).unmarshal(ss, getTypeMappingInfo());
        instream.close();

        Object controlObj = getReadControlObject();
        xmlToObjectTest(testObject, controlObj);
    }

    public void testXMLToObjectFromStAXSourceWithTypeMappingInfoXML() throws Exception {
        InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
        XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(instream);
        StAXSource ss = new StAXSource(xmlStreamReader);
        Object testObject = ((JAXBUnmarshaller)jaxbUnmarshaller).unmarshal(ss, getTypeMappingInfo());
        instream.close();

        Object controlObj = getReadControlObject();
        xmlToObjectTest(testObject, controlObj);
    }
}
