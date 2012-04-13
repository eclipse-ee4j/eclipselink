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
 * Denise Smith - 2.4 - April 2012
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlbindings;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.jaxb.xmlmodel.*;
import org.eclipse.persistence.jaxb.xmlmodel.XmlProperties.XmlProperty;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.*;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType.JavaAttributes;
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
		jaxbMarshaller.setProperty(JAXBMarshaller.JSON_INCLUDE_ROOT, false);
		jaxbUnmarshaller.setProperty(JAXBUnmarshaller.JSON_INCLUDE_ROOT, false);
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
		jaxbUnmarshaller.setProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE, "application/xml");
		Object testObject = jaxbUnmarshaller.unmarshal(instream);
        
		//marshal to JSON
        jaxbMarshaller.setProperty(JAXBMarshaller.MEDIA_TYPE, "application/json");
        StringWriter sw = new StringWriter();
        jaxbMarshaller.marshal(testObject,sw);
        
        //unmarshal from JSON
        StringReader sr = new StringReader(sw.toString());
        jaxbUnmarshaller.setProperty(JAXBUnmarshaller.MEDIA_TYPE, "application/json");
        JAXBElement jbe = jaxbUnmarshaller.unmarshal(new StreamSource(sr), XmlBindings.class);
        Object unmarshalledJSON = jbe.getValue();
        
        //marshal to XML again
        jaxbMarshaller.setProperty(JAXBMarshaller.MEDIA_TYPE, "application/xml");
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
	        jaxbMarshaller.setProperty(JAXBMarshaller.MEDIA_TYPE, "application/xml");
	        
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
