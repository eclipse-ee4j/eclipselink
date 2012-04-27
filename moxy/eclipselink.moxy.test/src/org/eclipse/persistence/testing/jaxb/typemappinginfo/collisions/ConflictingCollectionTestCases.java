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
 *     Denise Smith -  January, 2010 - 2.0.1 
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.typemappinginfo.collisions;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.TypeMappingInfoWithJSONTestCases;

public class ConflictingCollectionTestCases  extends TypeMappingInfoWithJSONTestCases{

	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/collisions/conflictingCollections.xml";
	protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/collisions/conflictingCollections.json";
    public Collection collection1;
    public Collection collection2;
    public Collection<Object> collection3;
    public Collection<Object> collection4;
    public Collection<?> collection5;
    public Collection<?> collection6;
	  
	
	public ConflictingCollectionTestCases(String name) throws Exception {
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
            typeMappingInfos = new TypeMappingInfo[6];
	        
	    	TypeMappingInfo tmi1 = new TypeMappingInfo();
	   	    tmi1.setElementScope(ElementScope.Global);
	   	    tmi1.setXmlTagName(new QName("collectionTag1"));
	   	    tmi1.setType(getClass().getField("collection1").getGenericType());
	   	  
	   	    TypeMappingInfo tmi2 = new TypeMappingInfo();
	   	    tmi2.setElementScope(ElementScope.Global);
	   	    tmi2.setXmlTagName(new QName("collectionTag2"));
	   	    tmi2.setType(getClass().getField("collection2").getGenericType());

	   	    TypeMappingInfo tmi3 = new TypeMappingInfo();
	   	    tmi3.setElementScope(ElementScope.Global);
	   	    tmi3.setXmlTagName(new QName("collectionTag3"));
	   	    tmi3.setType(getClass().getField("collection3").getGenericType());
	   	  
	   	    TypeMappingInfo tmi4 = new TypeMappingInfo();
	   	    tmi4.setElementScope(ElementScope.Global);
	   	    tmi4.setXmlTagName(new QName("collectionTag4"));
	   	    tmi4.setType(getClass().getField("collection4").getGenericType());

	   	    TypeMappingInfo tmi5 = new TypeMappingInfo();
	   	    tmi5.setElementScope(ElementScope.Global);
	   	    tmi5.setXmlTagName(new QName("collectionTag5"));
	   	    tmi5.setType(getClass().getField("collection5").getGenericType());
	   	  
	   	    TypeMappingInfo tmi6 = new TypeMappingInfo();
	   	    tmi6.setElementScope(ElementScope.Global);
	   	    tmi6.setXmlTagName(new QName("collectionTag6"));
	   	    tmi6.setType(getClass().getField("collection6").getGenericType());
	   	  	   	  
	   	    typeMappingInfos[0] = tmi1;
	   	    typeMappingInfos[1] = tmi2;
	   	    typeMappingInfos[2] = tmi3;
	   	    typeMappingInfos[3] = tmi4;
	   	    typeMappingInfos[4] = tmi5;
	   	    typeMappingInfos[5] = tmi6;	    		       	
	    }
		return typeMappingInfos;		
	}

	
	protected Object getControlObject() {
		
		Collection<Object> objects = new ArrayList<Object>();
		
		QName qname = new QName("", "testTagName");
		JAXBElement jaxbElement = new JAXBElement(qname, Collection.class, null);
		
		objects.add("one");
		objects.add(2);		
		
		jaxbElement.setValue(objects);

		return jaxbElement;
	}

    public Map<String, InputStream> getControlSchemaFiles(){			 		   
	    InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/collisions/conflictingCollections.xsd");
		
		Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
		controlSchema.put("", instream);
		
		return controlSchema;
	}

	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE;
	}

	public void testDescriptorsSize(){
		List descriptors = ((org.eclipse.persistence.jaxb.JAXBContext)jaxbContext).getXMLContext().getSession(0).getProject().getOrderedDescriptors();
		assertEquals(1, descriptors.size());
	}

	public void testTypeMappingInfoToSchemaTypeMapSize() throws Exception{
		Map <TypeMappingInfo, QName> names = ((org.eclipse.persistence.jaxb.JAXBContext)jaxbContext).getTypeMappingInfoToSchemaType();
		assertEquals(6, names.size());
		assertNotNull(names.get(getTypeMappingInfos()[0]));
		assertNotNull(names.get(getTypeMappingInfos()[1]));
		assertNotNull(names.get(getTypeMappingInfos()[2]));
		assertNotNull(names.get(getTypeMappingInfos()[3]));
		assertNotNull(names.get(getTypeMappingInfos()[4]));
		assertNotNull(names.get(getTypeMappingInfos()[5]));		
	}

}

