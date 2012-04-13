/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith  February 9, 2009 - 2.1
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.typemappinginfo.simple;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.TypeMappingInfoWithJSONTestCases;

public class EmptyClassTestCases extends TypeMappingInfoWithJSONTestCases{

	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/simple/emptyclass.xml";
	protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/simple/emptyclass.json";	
	public EmptyClassTestCases(String name) throws Exception {
		super(name);
		init();
	}
	
	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);
	    setupParser();	
		setTypeMappingInfos(getTypeMappingInfos());	
	}
	
	protected TypeMappingInfo[] getTypeMappingInfos()throws Exception {
		if(typeMappingInfos == null){
		    typeMappingInfos = new TypeMappingInfo[1];
		
		    TypeMappingInfo tpi = new TypeMappingInfo();
		    tpi.setXmlTagName(new QName("","testTagname"));		
		    tpi.setElementScope(ElementScope.Global);
		    tpi.setType(Person.class);		
		    typeMappingInfos[0] = tpi;
		}
		return typeMappingInfos;
	}
	
    protected Object getControlObject() {
		
    	Person person = new Person();
    	
		QName qname = new QName("examplenamespace", "root");
		
		JAXBElement jaxbElement = new JAXBElement(qname, Person.class, person);		
		return jaxbElement;
	}
    
    public Object getWriteControlObject(){
        Person person = new Person();
    	
		QName qname = new QName("examplenamespace", "root");
		
		JAXBElement jaxbElement = new JAXBElement(qname, Object.class, person);		
		return jaxbElement;
    	
    }
    
    public Map<String, InputStream> getControlSchemaFiles(){			 		   
		Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();

    	InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/simple/emptyclass.xsd");
		controlSchema.put("", instream);
				
		return controlSchema;
	}
}
