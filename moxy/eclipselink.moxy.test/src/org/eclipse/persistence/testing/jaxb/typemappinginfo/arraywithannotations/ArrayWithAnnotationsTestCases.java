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
 * dmccann - 2.3 - Initial implementation
 ******************************************************************************/ 
package org.eclipse.persistence.testing.jaxb.typemappinginfo.arraywithannotations;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
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
import org.eclipse.persistence.testing.jaxb.typemappinginfo.TypeMappingInfoTestCases;

public class ArrayWithAnnotationsTestCases extends TypeMappingInfoTestCases {
	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/arraywithannotations/instance.xml";
	protected final static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/arraywithannotations/schema.xsd";
	protected final static QName XML_TAG_NAME = new QName("http://jaxb.dev.java.net/array", "FloatListType");
	
	@XmlList
	public Float[] f = new Float[] { 3.14f, 3.25f };
	
	public ArrayWithAnnotationsTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		setTypeMappingInfos(getTypeMappingInfos());
	}
	
	protected TypeMappingInfo[] getTypeMappingInfos() throws Exception {
		if (typeMappingInfos == null) {
		    typeMappingInfos = new TypeMappingInfo[1];

		    TypeMappingInfo ti = new TypeMappingInfo();
			ti.setXmlTagName(XML_TAG_NAME);
			ti.setType(Float[].class);
			ti.setElementScope(ElementScope.Global);

			Field fld = getClass().getField("f");
			ti.setAnnotations(fld.getAnnotations());
			typeMappingInfos[0] = ti;
		}
		return typeMappingInfos;
	}
		
	protected Object getControlObject() {
		return new JAXBElement<Float[]>(XML_TAG_NAME, Float[].class, f);
	}

	public Map<String, InputStream> getControlSchemaFiles() {
	    InputStream instream = ClassLoader.getSystemResourceAsStream(XSD_RESOURCE);
		Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
		controlSchema.put(XML_TAG_NAME.getNamespaceURI(), instream);
		return controlSchema;
	}
}