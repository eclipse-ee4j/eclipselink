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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;

public class JavaTypeAdapterListToStringTestCases extends TypeMappingInfoWithJSONTestCases {
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/listtostring.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/listtostring.json";
    
    @XmlJavaTypeAdapter(ListToStringAdapter.class)
    public Object javaTypeAdapterField;
    
    public JavaTypeAdapterListToStringTestCases(String name) throws Exception {
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
            TypeMappingInfo tmi = new TypeMappingInfo();
            tmi.setXmlTagName(new QName("someUri","testTagname"));      
            tmi.setElementScope(ElementScope.Global);       
            tmi.setType(List.class);
            Annotation[] annotations = new Annotation[1];
            
            annotations[0] = getClass().getField("javaTypeAdapterField").getAnnotations()[0];
            tmi.setAnnotations(annotations);
            typeMappingInfos[0] = tmi;          
        }
        return typeMappingInfos;        
    }
    
    protected Object getControlObject() {
        List<String> list = new ArrayList<String>();
        list.add("String1");
        list.add("String2");
        QName qname = new QName("someUri", "testTagName");
        JAXBElement jaxbElement = new JAXBElement(qname, List.class, null);
        jaxbElement.setValue(list);

        return jaxbElement;
    }
    
    public Map<String, InputStream> getControlSchemaFiles(){                       
        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/listtostring.xsd");
        
        Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
        controlSchema.put("someUri", instream);
        return controlSchema;
    }

    protected String getNoXsiTypeControlResourceName() {
        return XML_RESOURCE;
    }    
}
