/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmimetype.MyAttachmentMarshaller;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmimetype.MyAttachmentUnmarshaller;

public class DatahandlerWithAnnotationsTestCases extends TypeMappingInfoWithJSONTestCases{

	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/dataHandlerAttachmentRefAndMimeType.xml";
	protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/dataHandlerAttachmentRefAndMimeType.json";
	
	@XmlMimeType(value="image/jpeg")
	public String xmlMimeTypeField;
	
	@XmlAttachmentRef
	public String xmlAttachementRefField;
	
	public DatahandlerWithAnnotationsTestCases(String name) throws Exception {
		super(name);
		init();
	}
	
	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);
	    setupParser();
	
		setTypeMappingInfos(getTypeMappingInfos());
		
		jaxbUnmarshaller.setAttachmentUnmarshaller(new MyAttachmentUnmarshaller());
		jaxbMarshaller.setAttachmentMarshaller(new MyAttachmentMarshaller());
		
		DataHandler data = new DataHandler("THISISATEXTSTRINGFORTHISDATAHANDLER", "text");
		MyAttachmentMarshaller.attachments.put(MyAttachmentUnmarshaller.ATTACHMENT_TEST_ID, data);
	}
	
	protected TypeMappingInfo[] getTypeMappingInfos()throws Exception {
	    if(typeMappingInfos == null) {
	    	typeMappingInfos = new TypeMappingInfo[1];
	        TypeMappingInfo tpi = new TypeMappingInfo();
	        tpi.setXmlTagName(new QName("someUri","testTagname"));		
	        tpi.setElementScope(ElementScope.Global);
	        Annotation[] annotations = new Annotation[2];
				
	        annotations[0] = getClass().getField("xmlMimeTypeField").getAnnotations()[0];
	        annotations[1] = getClass().getField("xmlAttachementRefField").getAnnotations()[0];
		
	        tpi.setAnnotations(annotations);
	        tpi.setType(DataHandler.class);	        
	        typeMappingInfos[0] = tpi;	        
	    }
		return typeMappingInfos;		
	}
		
	protected Object getControlObject() {
		
		DataHandler data = new DataHandler("THISISATEXTSTRINGFORTHISDATAHANDLER", "text");		
		QName qname = new QName("someUri", "testTagName");
		JAXBElement jaxbElement = new JAXBElement(qname, DataHandler.class, null);
		jaxbElement.setValue(data);

		return jaxbElement;
	}


    public Map<String, InputStream> getControlSchemaFiles(){			 		   
	    InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/dataHandlerAttachmentRefAndMimeType.xsd");
		
		Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
		controlSchema.put("someUri", instream);
		return controlSchema;
	}

	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE;
	}

}