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
// Denise Smith - 2.4 - April 2012
package org.eclipse.persistence.testing.jaxb.xmlbindings;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType.JavaAttributes;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAccessType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAttribute;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.JavaTypes;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.XmlEnums;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.XmlRegistries;
import org.eclipse.persistence.jaxb.xmlmodel.XmlClassExtractor;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElement;
import org.eclipse.persistence.jaxb.xmlmodel.XmlEnum;
import org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter;
import org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapters;
import org.eclipse.persistence.jaxb.xmlmodel.XmlNsForm;
import org.eclipse.persistence.jaxb.xmlmodel.XmlProperties;
import org.eclipse.persistence.jaxb.xmlmodel.XmlProperties.XmlProperty;
import org.eclipse.persistence.jaxb.xmlmodel.XmlRegistry;
import org.eclipse.persistence.jaxb.xmlmodel.XmlRootElement;
import org.eclipse.persistence.jaxb.xmlmodel.XmlSchema;
import org.eclipse.persistence.jaxb.xmlmodel.XmlSchemaType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlSchemaTypes;
import org.eclipse.persistence.jaxb.xmlmodel.XmlType;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class XMLBindingsPopulatedTestCases extends JAXBWithJSONTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlbindings/bindingsPopulated.xml";
    private final static String XML_RESOURCE_REORDERED = "org/eclipse/persistence/testing/jaxb/xmlbindings/bindingsPopulatedReordered.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlbindings/bindingsPopulated.json";


    public XMLBindingsPopulatedTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{XmlBindings.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
    }

    public Class getUnmarshalClass(){
        return XmlBindings.class;
    }

    @Override
    protected Object getControlObject() {
        XmlBindings xmlBindings = new XmlBindings();

        xmlBindings.setPackageName("test.model");
        xmlBindings.setXmlAccessorType(XmlAccessType.PUBLIC_MEMBER);
        xmlBindings.setXmlAccessorOrder(org.eclipse.persistence.jaxb.xmlmodel.XmlAccessOrder.ALPHABETICAL);
        XmlEnums enums = new XmlEnums();
        XmlEnum e1 = new XmlEnum();
        e1.setJavaEnum("A");
        e1.setValue("A_Value");
        XmlEnum e2 = new XmlEnum();
        e2.setJavaEnum("B");
        e2.setValue("B_Value");
        enums.getXmlEnum().add(e1);
        enums.getXmlEnum().add(e2);
        xmlBindings.setXmlEnums(enums);

        xmlBindings.setXmlNameTransformer("test.someNameTransformer");
        XmlSchema xmlSchema = new XmlSchema();
        xmlSchema.setAttributeFormDefault(XmlNsForm.UNQUALIFIED);
        xmlSchema.setElementFormDefault(XmlNsForm.UNQUALIFIED);
        xmlSchema.setLocation("someLocation");
        xmlSchema.setNamespace("testnamespace");
        xmlBindings.setXmlSchema(xmlSchema);

        XmlSchemaTypes xmlSchemaTypes = new XmlSchemaTypes();
        XmlSchemaType xmlSchemaType = new XmlSchemaType();
        xmlSchemaType.setName("someSchemaType");
        xmlSchemaType.setNamespace("someSchemaTypeNamespace");
        xmlSchemaType.setType("someSchemaType");

        XmlSchemaType xmlSchemaType2 = new XmlSchemaType();
        xmlSchemaType2.setName("someSchemaType2");
        xmlSchemaType2.setNamespace("someSchemaTypeNamespace2");
        xmlSchemaType2.setType("someSchemaType2");
        xmlSchemaTypes.getXmlSchemaType().add(xmlSchemaType);
        xmlSchemaTypes.getXmlSchemaType().add(xmlSchemaType2);
        xmlBindings.setXmlSchemaTypes(xmlSchemaTypes);


        xmlBindings.setXmlMappingMetadataComplete(Boolean.FALSE);

        XmlRegistries xmlRegistries = new XmlRegistries();
        XmlRegistry r1 = new XmlRegistry();
        r1.setName("someRegistry");
        xmlRegistries.getXmlRegistry().add(r1);
        xmlBindings.setXmlRegistries(xmlRegistries);

        XmlJavaTypeAdapters xmlJavaTypeAdapters = new XmlJavaTypeAdapters();
        XmlJavaTypeAdapter a1 = new XmlJavaTypeAdapter();
        a1.setType("someAdapterType");
        a1.setValue("someAdapterValue");
        a1.setValueType("somValueType");
        a1.setJavaAttribute("someJavaAttribute");
        xmlJavaTypeAdapters.getXmlJavaTypeAdapter().add(a1);
        xmlBindings.setXmlJavaTypeAdapters(xmlJavaTypeAdapters);

        JavaTypes types = new JavaTypes();
        JavaType javaType = new JavaType();
        javaType.setName("myType");
        javaType.setXmlTransient(Boolean.FALSE);
        javaType.setXmlAccessorOrder(org.eclipse.persistence.jaxb.xmlmodel.XmlAccessOrder.ALPHABETICAL);
        XmlProperties xmlProperties = new XmlProperties();
        XmlProperty p1 = new XmlProperty();
        p1.setName("prop1");
        p1.setValue("propValue");
        p1.setValueType("propValueType");
        xmlProperties.getXmlProperty().add(p1);
        javaType.setXmlProperties(xmlProperties);

        javaType.setXmlDiscriminatorNode("descriminator node");
        javaType.setXmlDiscriminatorValue("discriminator value");
        XmlClassExtractor extractor = new XmlClassExtractor();
        extractor.setClazz("a.b.c.someClass");
        javaType.setXmlClassExtractor(extractor);

        javaType.setXmlInlineBinaryData(Boolean.FALSE);

        XmlRootElement root = new XmlRootElement();
        root.setName("someRoot");
        root.setNamespace("someNamespace");
        javaType.setXmlRootElement(root);

        XmlType xmlType= new XmlType();
        xmlType.setFactoryClass("somepackage.someFactoryclass");
        xmlType.setName("someName");
        xmlType.setFactoryMethod("someMethod");
        xmlType.getPropOrder().add("p2");
        xmlType.getPropOrder().add("p1");
        javaType.setXmlType(xmlType);

        JavaAttributes javaAttributes = new JavaAttributes();
        XmlElement javaAttribute = new XmlElement();
        javaAttribute.setName("elementName");
        javaAttribute.setJavaAttribute("theJavaAttributeValue");
        JAXBElement jbe = new JAXBElement<XmlElement>(new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-element"), XmlElement.class, javaAttribute);

        XmlElement javaAttribute2 = new XmlElement();
        javaAttribute2.setName("elementName2");
        javaAttribute2.setJavaAttribute("theJavaAttributeValue2");
        JAXBElement jbe2 = new JAXBElement<XmlElement>(new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-element"), XmlElement.class, javaAttribute2);

        XmlAttribute javaAttribute3 = new XmlAttribute();
        javaAttribute3.setContainerType("someContainerType");
        javaAttribute3.setJavaAttribute("javaAttribute");
        javaAttribute3.setNamespace("somenamespace");
        javaAttribute3.setName("attributename1");
        javaAttribute3.setReadOnly(Boolean.TRUE);
        javaAttribute3.setRequired(Boolean.TRUE);

        JAXBElement jbe3 = new JAXBElement<XmlAttribute>(new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-attribute"), XmlAttribute.class, javaAttribute3);
        javaAttributes.getJavaAttribute().add(jbe);

        javaAttributes.getJavaAttribute().add(jbe3);
        javaAttributes.getJavaAttribute().add(jbe2);


        javaType.setJavaAttributes(javaAttributes);

        types.getJavaType().add(javaType);
        xmlBindings.setJavaTypes(types);

        return xmlBindings;
    }

    public void testXmlToObjectToJsonToObjectToXML() throws Exception{
        StringWriter writer = new StringWriter();
        //unmarshal control XML
        InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/xml");
        Object testObject = jaxbUnmarshaller.unmarshal(instream);

        //marshal to JSON
        jaxbMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        StringWriter sw = new StringWriter();
        jaxbMarshaller.marshal(testObject,sw);

        //unmarshal from JSON
        StringReader sr = new StringReader(sw.toString());
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
        JAXBElement jbe = jaxbUnmarshaller.unmarshal(new StreamSource(sr), XmlBindings.class);
        Object unmarshalledJSON = jbe.getValue();

        //marshal to XML again
        jaxbMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/xml");
        StringWriter xmlStringWriter = new StringWriter();
        jaxbMarshaller.marshal(testObject,xmlStringWriter);

        InputSource inputSource = new InputSource(new StringReader(xmlStringWriter.toString()));
        Document testDocument = parser.parse(inputSource);
        objectToXMLDocumentTest(testDocument);


   }

    public void xmlToObjectTest(Object testObject) throws Exception {
        log("\n**xmlToObjectTest**");
        log("Expected:");
        Object controlObject = getReadControlObject();
        if(null == controlObject) {
            log((String) null);
        } else {
            log(controlObject.toString());
        }
        log("Actual:");
        if(null == testObject) {
            log((String) null);
        } else {
            log(testObject.toString());
        }

        StringWriter sw = new StringWriter();
        jaxbMarshaller.marshal(testObject,sw);
        StringReader sr = new StringReader(sw.toString());

        InputSource inputSource = new InputSource(sr);
        Document testDocument = parser.parse(inputSource);
        objectToXMLDocumentTest(testDocument);
    }

     public void jsonToObjectTest(Object testObject) throws Exception {
            log("\n**xmlToObjectTest**");
            log("Expected:");
            log(getJSONReadControlObject().toString());
            log("Actual:");
            log(testObject.toString());

            StringWriter sw = new StringWriter();
            jaxbMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/xml");

            jaxbMarshaller.marshal(((JAXBElement)testObject).getValue(),sw);
            StringReader sr = new StringReader(sw.toString());
            InputSource inputSource = new InputSource(sr);
            Document testDocument = parser.parse(inputSource);

            log("**objectToXMLDocumentTest**");
            log("Expected:");
            log(getWriteControlDocument());
            log("\nActual:");
            log(testDocument);

            InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_REORDERED);
            Document reorderedDocument = parser.parse(inputStream);
            if (shouldRemoveEmptyTextNodesFromControlDoc()) {
                removeEmptyTextNodes(reorderedDocument);
            }
            inputStream.close();

            assertXMLIdentical(reorderedDocument, testDocument);

        }

     public boolean shouldRemoveEmptyTextNodesFromControlDoc() {
            return true;
        }




}
