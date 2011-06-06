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
 *     Denise Smith - January 7th, 2010 - 2.0.1
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
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmimetype.MyAttachmentMarshaller;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmimetype.MyAttachmentUnmarshaller;

public class ListOfDataHandlerTestCases extends TypeMappingInfoTestCases{
protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/listDataHandler.xml";
	
	@XmlMimeType(value="image/jpeg")
	public String xmlMimeTypeField;
	
	@XmlAttachmentRef
	public String xmlAttachementRefField;
	
	public List<DataHandler> listField;;
	
	public ListOfDataHandlerTestCases(String name) throws Exception {
		super(name);
		init();
	}
	
	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);
	
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
	        tpi.setType(getClass().getField("listField").getGenericType());	        
	        typeMappingInfos[0] = tpi;	        
	    }
		return typeMappingInfos;		
	}
		
	protected Object getControlObject() {
		
		DataHandler data = new DataHandler("THISISATEXTSTRINGFORTHISDATAHANDLER", "text");		
		
		DataHandler data2 = new DataHandler("THISISATEXTSTRINGFORTHISDATAHANDLER", "text");		
		
		QName qname = new QName("someUri", "testTagName");

		List<DataHandler> theList = new ArrayList<DataHandler>();
		theList.add(data);
		theList.add(data2);
		
		JAXBElement jaxbElement = new JAXBElement(qname, Object.class, null);
		jaxbElement.setValue(theList);

		return jaxbElement;
	}


    public Map<String, InputStream> getControlSchemaFiles(){			 		   
		
		Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();

		InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/listDataHandler.xsd");
		controlSchema.put("", instream);
		
        InputStream instream2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/listDataHandler2.xsd");
		controlSchema.put("someUri", instream2);
		
		return controlSchema;
	}

	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE;
	}
}
