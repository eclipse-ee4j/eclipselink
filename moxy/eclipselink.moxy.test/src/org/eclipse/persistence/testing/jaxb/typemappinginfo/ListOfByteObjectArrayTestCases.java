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
 *     Denise Smith - January 8th, 2010 - 2.0.1
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;

public class ListOfByteObjectArrayTestCases extends TypeMappingInfoTestCases{

	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/listOfByteObjectArray.xml";

	public List<Byte[]> testField;
	
	public ListOfByteObjectArrayTestCases(String name) throws Exception {
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
	        TypeMappingInfo tpi = new TypeMappingInfo();
	        tpi.setXmlTagName(new QName("","testTagname"));		
	        tpi.setElementScope(ElementScope.Global);		
	        tpi.setType(getClass().getField("testField").getGenericType());	        
	        typeMappingInfos[0] = tpi;	        
	    }
		return typeMappingInfos;		
	}
		
	protected Object getControlObject() {
			    
	    Byte[] bytes3 = new Byte[]{0,1,2,3};
        Byte[] bytes4 = new Byte[]{1,2,3,4};
					
		List<Byte[]> bytesList = new ArrayList<Byte[]>();
		bytesList.add(bytes3);
		bytesList.add(bytes4);
		
		QName qname = new QName("", "root");
		JAXBElement jaxbElement = new JAXBElement(qname, Object.class, null);
		jaxbElement.setValue(bytesList);

		return jaxbElement;
	}


    public Map<String, InputStream> getControlSchemaFiles(){			 		   
	    InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/listOfByteObjectArray.xsd");
		
		Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
		controlSchema.put("", instream);
		return controlSchema;
	}

	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE;
	}

}