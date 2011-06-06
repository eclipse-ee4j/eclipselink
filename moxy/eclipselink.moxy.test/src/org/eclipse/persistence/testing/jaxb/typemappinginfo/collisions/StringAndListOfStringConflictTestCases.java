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
 * Oracle = 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.typemappinginfo.collisions;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.TypeMappingInfoTestCases;

public class StringAndListOfStringConflictTestCases extends TypeMappingInfoTestCases {
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/collisions/nonConflictingLists.xml";
    
    @XmlElement(type=String.class)
    public List testField;

    public StringAndListOfStringConflictTestCases(String name) throws Exception {
        super(name);
        init();
    }
    
    public void init() throws Exception {
        setControlDocument(XML_RESOURCE);   
        setTypeMappingInfos(getTypeMappingInfos()); 
    }
    
    protected TypeMappingInfo[] getTypeMappingInfos()throws Exception {
        if(typeMappingInfos == null) {
            typeMappingInfos = new TypeMappingInfo[2];
            
            TypeMappingInfo tmi = new TypeMappingInfo();
            tmi.setXmlTagName(new QName("","testTagName1"));        
            tmi.setElementScope(ElementScope.Global);       
            tmi.setType(List.class);
            tmi.setAnnotations(getClass().getField("testField").getAnnotations());
            typeMappingInfos[0] = tmi;          
            
            TypeMappingInfo tmi2 = new TypeMappingInfo();
            tmi2.setXmlTagName(new QName("","testTagName2"));       
            tmi2.setElementScope(ElementScope.Global);      
            tmi2.setType(String.class);
            typeMappingInfos[1] = tmi2;
        }
        return typeMappingInfos;        
    }

    @Override
    protected Object getControlObject() {
        QName qname = new QName("", "testTagName");
        JAXBElement jaxbElement = new JAXBElement(qname, List.class, null);
        List<String> theList = new ArrayList<String>();
        theList.add("one");
        theList.add("two");
        theList.add("three");
        jaxbElement.setValue(theList);
        return jaxbElement;
    }

    @Override
    public Map<String, InputStream> getControlSchemaFiles() {
        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/collisions/stringandlistofstring.xsd");
        
        Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
        controlSchema.put("", instream);
            
        return controlSchema;
    }
    
    public void testSchemaGen() {
    }

}
