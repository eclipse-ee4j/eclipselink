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
 *     Denise Smith  November 2011 - 2.3
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;

public class PrimitiveIntTestCases extends TypeMappingInfoWithJSONTestCases {
	 protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/int.xml";
	 protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/int.json";

	    public PrimitiveIntTestCases(String name) throws Exception {
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
	            TypeMappingInfo tpi = new TypeMappingInfo();
	            tpi.setXmlTagName(new QName("","testTagname"));     
	            tpi.setElementScope(ElementScope.Global);
	            tpi.setNillable(true);
	            tpi.setType(int.class);         
	            typeMappingInfos[0] = tpi;          
	        }
	        return typeMappingInfos;        
	    }
	        
	    protected Object getControlObject() {
	        QName qname = new QName("", "testTagname");
	        JAXBElement jaxbElement = new JAXBElement(qname, int.class, null);
	        return jaxbElement;
	    }


	    public Map<String, InputStream> getControlSchemaFiles(){                       
	        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/int.xsd");
	        
	        Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
	        controlSchema.put("", instream);
	        return controlSchema;
	    }

	    protected String getNoXsiTypeControlResourceName() {
	        return XML_RESOURCE;
	    }
}
