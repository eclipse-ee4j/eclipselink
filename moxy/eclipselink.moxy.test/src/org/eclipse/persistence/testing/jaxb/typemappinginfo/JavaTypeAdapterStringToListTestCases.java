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
 * mmacivor - December 15/2009 - 2.0.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;

public class JavaTypeAdapterStringToListTestCases extends TypeMappingInfoTestCases {
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/stringtolist.xml";
    
    @XmlJavaTypeAdapter(StringToListAdapter.class)
    @XmlList
    public Object javaTypeAdapterField;
    
    public JavaTypeAdapterStringToListTestCases(String name) throws Exception {
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
            tmi.setXmlTagName(new QName("", "testTagname"));      
            tmi.setElementScope(ElementScope.Global);       
            tmi.setType(String.class);
            
            Annotation[] annotations = getClass().getField("javaTypeAdapterField").getAnnotations();
            tmi.setAnnotations(annotations);
            typeMappingInfos[0] = tmi;          
        }
        return typeMappingInfos;        
    }
    
    protected Object getControlObject() {
        QName qname = new QName("", "testTagName");
        JAXBElement jaxbElement = new JAXBElement(qname, String.class, null);
        jaxbElement.setValue("unmarshalled string");

        return jaxbElement;
    }
    
    public Map<String, InputStream> getControlSchemaFiles(){                       
        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/stringtolist.xsd");
        
        Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
        controlSchema.put("", instream);
        return controlSchema;
    }

    protected String getNoXsiTypeControlResourceName() {
        return XML_RESOURCE;
    }    
}

