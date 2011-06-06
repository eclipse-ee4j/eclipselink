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
 *     Matt MacIvor - 2.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;

public class ByteArrayTestCases extends TypeMappingInfoTestCases {
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/BigByteArray.xml";

    public Byte[] byteArrayField;
    
    public ByteArrayTestCases(String name) throws Exception {
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
            tmi.setXmlTagName(new QName("http://jaxb.dev.java.net/array","testTagName"));       
            tmi.setElementScope(ElementScope.Global);       
            tmi.setType(getClass().getField("byteArrayField").getGenericType());
            typeMappingInfos[0] = tmi;          
        }
        return typeMappingInfos;        
    }

    
    protected Object getControlObject() {
        
        QName qname = new QName("http://jaxb.dev.java.net/array", "testTagName");
        JAXBElement jaxbElement = new JAXBElement(qname, Byte[].class, null);
        Byte[] value = new Byte[2];
        value[0] = 10;
        value[1] = 20;
        jaxbElement.setValue(value);

        return jaxbElement;
    }

    public Map<String, InputStream> getControlSchemaFiles(){                       
        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/BigByteArray.xsd");
        
        Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
        controlSchema.put("http://jaxb.dev.java.net/array", instream);
        return controlSchema;
    }

    protected String getNoXsiTypeControlResourceName() {
        return XML_RESOURCE;
    }
    
    public void testTypeMappingInfoToSchemaType() throws Exception{
        Map theMap =((org.eclipse.persistence.jaxb.JAXBContext)jaxbContext).getTypeMappingInfoToSchemaType();
        assertNotNull(theMap);
        assertEquals(1, theMap.size());
        assertNotNull(theMap.get(getTypeMappingInfos()[0]));
        
    }
}
