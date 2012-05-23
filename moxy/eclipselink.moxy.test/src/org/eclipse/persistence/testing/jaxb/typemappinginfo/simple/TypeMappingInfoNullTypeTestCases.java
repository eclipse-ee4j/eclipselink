/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith -  February 9, 2010 - Since 2.1
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.typemappinginfo.simple;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class TypeMappingInfoNullTypeTestCases extends OXTestCase  {
	
	public TypeMappingInfoNullTypeTestCases(String name) {
		super(name);
	}
	
	public void testNullTypeNullTagName() throws Exception{
		TypeMappingInfo[] tmis = new TypeMappingInfo[1];
		
		TypeMappingInfo tmi = new TypeMappingInfo();
		tmi.setType(null);
		tmis[0] = tmi;
		
		try{
		    JAXBContext ctx  = new org.eclipse.persistence.jaxb.JAXBContextFactory().createContext(tmis, null, Thread.currentThread().getContextClassLoader());
		}catch(JAXBException ex){
			assertEquals(JAXBException.NULL_TYPE_ON_TYPEMAPPINGINFO, ex.getErrorCode());
			return;
		}
		fail("A JAXBException should have happened but didn't");
		
		
	}
	
	public void testNullTypeNonNullTagName() throws Exception{
		TypeMappingInfo[] tmis = new TypeMappingInfo[1];
		
		TypeMappingInfo tmi = new TypeMappingInfo();
		tmi.setType(null);
		QName tagname = new QName("someUri", "someLocalName");
		tmi.setXmlTagName(tagname);
		tmis[0] = tmi;
		
		try{
		    JAXBContext ctx  = new org.eclipse.persistence.jaxb.JAXBContextFactory().createContext(tmis, null, Thread.currentThread().getContextClassLoader());
		}catch(JAXBException ex){
			assertEquals(JAXBException.NULL_TYPE_ON_TYPEMAPPINGINFO, ex.getErrorCode());
			return;
		}
		fail("A JAXBException should have happened but didn't");
		
		
	}

}
