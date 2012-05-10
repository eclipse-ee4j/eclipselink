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
 * mmacivor - December 15/2009 - 2.0.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;

public class ImageTestCases extends TypeMappingInfoWithJSONTestCases {

	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/image.xml";
	protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/image.json";
	
	@XmlMimeType(value="image/jpeg")
	public String xmlMimeTypeField;
	
	public Image imageField;
	
	public ImageTestCases(String name) throws Exception {
		super(name);
		init();
	}
	
	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);
		setTypeMappingInfos(getTypeMappingInfos());	
	}
	
	protected TypeMappingInfo[] getTypeMappingInfos()throws Exception {
	    if(typeMappingInfos == null) {
	    	typeMappingInfos = new TypeMappingInfo[1];
	        TypeMappingInfo tmi = new TypeMappingInfo();
	        tmi.setXmlTagName(new QName("someUri","testTagname"));		
	        tmi.setElementScope(ElementScope.Global);		
	        tmi.setType(Image.class);
	        Annotation[] annotations = new Annotation[1];
			
	        annotations[0] = getClass().getField("xmlMimeTypeField").getAnnotations()[0];
	        tmi.setAnnotations(annotations);
	        typeMappingInfos[0] = tmi;	        
	    }
		return typeMappingInfos;		
	}
		
	protected Object getControlObject() {
		Image myImage = new BufferedImage(10, 10, BufferedImage.TYPE_3BYTE_BGR);
		
		QName qname = new QName("someUri", "testTagName");
		JAXBElement jaxbElement = new JAXBElement(qname, Image.class, null);
		jaxbElement.setValue(myImage);

		return jaxbElement;
	}


    public Map<String, InputStream> getControlSchemaFiles(){			 		   
	    InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/image.xsd");
		
		Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
		controlSchema.put("someUri", instream);
		return controlSchema;
	}

	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE;
	}

}