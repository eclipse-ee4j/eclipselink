/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2009-12-09
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.jaxbcontext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class JaxbTypeToSchemaTypeTestCases extends OXTestCase{

	@XmlAttachmentRef 
	public DataHandler attachmentRefField;
	
	public JaxbTypeToSchemaTypeTestCases(String name) {
		super(name);
	}
	
	public void testClassArray() throws Exception{
		Class[] classes = new Class[]{String.class};
		JAXBContext ctx = (JAXBContext) JAXBContextFactory.createContext(classes, null);
		Map<Type, QName> typeMap = ctx.getTypeToSchemaType();
		assertEquals(1, typeMap.size());
		
		Map<TypeMappingInfo, QName> tmiMap = ctx.getTypeMappingInfoToSchemaType();
		assertEquals(0, tmiMap.size());
	}

	public void testTypeArray() throws Exception{
		Type[] types = new Type[]{String.class};
		JAXBContext ctx = (JAXBContext) JAXBContextFactory.createContext(types, null, Thread.currentThread().getContextClassLoader());
		Map<Type, QName> typeMap = ctx.getTypeToSchemaType();
		assertEquals(1, typeMap.size());
		assertNotNull(typeMap.get(String.class));
		
		Map<TypeMappingInfo, QName> tmiMap = ctx.getTypeMappingInfoToSchemaType();
		assertEquals(0, tmiMap.size());		
	}
	
	public void testTypeMappingInfoArray() throws Exception{
		TypeMappingInfo tmi = new TypeMappingInfo();
		tmi.setType(String.class);

		TypeMappingInfo[] tmis = new TypeMappingInfo[]{tmi};
		
		JAXBContext ctx = (JAXBContext) JAXBContextFactory.createContext(tmis, null, Thread.currentThread().getContextClassLoader());
		Map<Type, QName> typeMap = ctx.getTypeToSchemaType();
		assertEquals(0, typeMap.size());
		
		Map<TypeMappingInfo, QName> tmiMap = ctx.getTypeMappingInfoToSchemaType();
		assertEquals(1, tmiMap.size());
		assertNotNull(tmiMap.get(tmi));
	}
	
	public void testDatahandlerTypes() throws Exception{
		TypeMappingInfo tmi = new TypeMappingInfo();
		tmi.setType(DataHandler.class);
		tmi.setElementScope(TypeMappingInfo.ElementScope.Local);
		tmi.setXmlTagName(new QName("uri1", "tmi1"));
		Annotation[] annotations = getClass().getField("attachmentRefField").getAnnotations();				
		tmi.setAnnotations(annotations);

		TypeMappingInfo tmi2 = new TypeMappingInfo();
		tmi2.setElementScope(TypeMappingInfo.ElementScope.Local);
		tmi2.setXmlTagName(new QName("uri1", "tmi2"));
		tmi2.setType(DataHandler.class);		
		
		TypeMappingInfo[] tmis = new TypeMappingInfo[]{tmi, tmi2};
		
		JAXBContext ctx = (JAXBContext) JAXBContextFactory.createContext(tmis, null, Thread.currentThread().getContextClassLoader());
		Map<Type, QName> typeMap = ctx.getTypeToSchemaType();
		assertEquals(0, typeMap.size());
		
		Map<TypeMappingInfo, QName> tmiMap = ctx.getTypeMappingInfoToSchemaType();
		assertEquals(2, tmiMap.size());
		assertNotNull(tmiMap.get(tmi2));
		assertEquals(XMLConstants.BASE_64_BINARY_QNAME, tmiMap.get(tmi2));
		assertNotNull(tmiMap.get(tmi));
		//assertEquals(XMLConstants.SWA_REF_QNAME, tmiMap.get(tmi));
	}
	
	public void testObject() throws Exception{
		TypeMappingInfo tmi = new TypeMappingInfo();
		tmi.setType(Object.class);
		
                TypeMappingInfo[] tmis = new TypeMappingInfo[]{tmi};
		
		JAXBContext ctx = (JAXBContext) JAXBContextFactory.createContext(tmis, null, Thread.currentThread().getContextClassLoader());
		Map<Type, QName> typeMap = ctx.getTypeToSchemaType();
		assertEquals(0, typeMap.size());
		Map<TypeMappingInfo, QName> tmiMap = ctx.getTypeMappingInfoToSchemaType();
		assertEquals(1, tmiMap.size());
		assertNotNull(tmiMap.get(tmi));
	}
}
