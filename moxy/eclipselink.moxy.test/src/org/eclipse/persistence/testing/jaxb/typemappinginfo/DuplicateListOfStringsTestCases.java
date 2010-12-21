/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith -  November, 2009 
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmimetype.MyAttachmentMarshaller;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmimetype.MyAttachmentUnmarshaller;

public class DuplicateListOfStringsTestCases extends TypeMappingInfoTestCases {
	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/duplicatelistofstrings.xml";
		
	@XmlList
	public List myList;
		
	public List<String> myListOfStrings;
	
	public DuplicateListOfStringsTestCases(String name) throws Exception {
		super(name);
		init();
	}
	
	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);
	    setupParser();
	
		setTypeMappingInfos(getTypeMappingInfos());
		
		jaxbUnmarshaller.setAttachmentUnmarshaller(new MyAttachmentUnmarshaller());
		jaxbMarshaller.setAttachmentMarshaller(new MyAttachmentMarshaller());
		
		DataHandler data = new DataHandler("THISISATEXTSTRINGFORTHISDATAHANDLER", "text");
		MyAttachmentMarshaller.attachments.put(MyAttachmentUnmarshaller.ATTACHMENT_TEST_ID, data);
	}
	
	protected TypeMappingInfo[] getTypeMappingInfos()throws Exception {
		if(typeMappingInfos == null){
		    typeMappingInfos = new TypeMappingInfo[4];
		
		    TypeMappingInfo tpi = new TypeMappingInfo();
		    tpi.setXmlTagName(new QName("someUri","testTagname"));		
		    tpi.setElementScope(ElementScope.Global);
		    Annotation[] annotations = new Annotation[1];				
		    annotations[0] = getClass().getField("myList").getAnnotations()[0];				
		    tpi.setAnnotations(annotations);
		    tpi.setType(List.class);		
		    typeMappingInfos[0] = tpi;
		
		    TypeMappingInfo tpi2 = new TypeMappingInfo();
		    tpi2.setXmlTagName(new QName("someUri","testTagname2"));		
		    tpi2.setElementScope(ElementScope.Global);		
	    	tpi2.setType(List.class);		
	    	typeMappingInfos[1] = tpi2;
		
		    TypeMappingInfo tpi3 = new TypeMappingInfo();
		    tpi3.setXmlTagName(new QName("someUri","testTagname3"));		
		    tpi3.setElementScope(ElementScope.Global);					
		    tpi3.setType(getClass().getField("myListOfStrings").getGenericType());		
		    typeMappingInfos[2] = tpi3;
		    
		    TypeMappingInfo tpi4 = new TypeMappingInfo();
		    tpi4.setXmlTagName(new QName("someUri","testTagname4"));		
		    tpi4.setElementScope(ElementScope.Global);		
		    tpi4.setType(List.class);		
	    	typeMappingInfos[3] = tpi4;
		}
		
		return typeMappingInfos;
	}
	

		
	protected Object getControlObject() {
		
		QName qname = new QName("examplenamespace", "root");
		List theList = new ArrayList();
		theList.add("aaa");
		theList.add("bbb");
		JAXBElement jaxbElement = new JAXBElement(qname, List.class, theList);
		
		return jaxbElement;
	}


    public Map<String, InputStream> getControlSchemaFiles(){			 		   
		Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();

    	InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/duplicatelistofstrings1.xsd");
		controlSchema.put("", instream);
		
		InputStream instream2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/duplicatelistofstrings2.xsd");
		controlSchema.put("someUri", instream2);
		return controlSchema;
	}

}

