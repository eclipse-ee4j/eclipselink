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
 *     Matt MacIvor -  January, 2010
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

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;

public class DefaultTargetNamespaceTestCases extends TypeMappingInfoTestCases{

	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/customer_dtn.xml";
	

	public DefaultTargetNamespaceTestCases(String name) throws Exception {
		super(name);
		init();
	}
	
	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);	
		setTypeMappingInfos(getTypeMappingInfos());	
	}
	
	protected TypeMappingInfo[] getTypeMappingInfos()throws Exception {
	    if(typeMappingInfos == null) {
	    	typeMappingInfos = new TypeMappingInfo[1];
	        TypeMappingInfo tmi = new TypeMappingInfo();
	        tmi.setType(Customer.class);
	        tmi.setXmlTagName(new QName("overridden/namespace", "customer"));
	        tmi.setElementScope(ElementScope.Global);
	        typeMappingInfos[0] = tmi;	        
	    }
		return typeMappingInfos;		
	}
		
	protected Object getControlObject() {
		Customer cust = new Customer();
		cust.firstName = "John";
		cust.lastName = "Doe";
		cust.phoneNumber = "123-456-7890";

		JAXBElement elem = new JAXBElement<Customer>(new QName("overridden/namespace", "customer"), Customer.class, cust);
		return elem;
	}


    public Map<String, InputStream> getControlSchemaFiles(){			 		   
	    InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/customer_dtn.xsd");
		
		Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
		controlSchema.put("overridden/namespace", instream);
		return controlSchema;
	}

	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE;
	}
    protected Map getProperties() throws Exception{
    	HashMap props = new HashMap();
    	props.put(JAXBContextFactory.DEFAULT_TARGET_NAMESPACE_KEY, "overridden/namespace");
    	
    	return props;
    }	
}

