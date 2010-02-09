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
 * dmccann - November 17/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;

public class JavaTypeAdapterMapTypeTestCases extends TypeMappingInfoTestCases {
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/maptypeoverride.xml";
    
    @XmlJavaTypeAdapter(StringStringToIntegerIntegerMapAdapter.class)
    public Object javaTypeAdapterField;
    
    public Map<String, String> theMapField;
    
    public JavaTypeAdapterMapTypeTestCases(String name) throws Exception {
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
            tmi.setXmlTagName(new QName("","testTagname"));      
            tmi.setElementScope(ElementScope.Global);       
            tmi.setType(getClass().getField("theMapField").getGenericType());
            Annotation[] annotations = new Annotation[1];
            
            annotations[0] = getClass().getField("javaTypeAdapterField").getAnnotations()[0];
            tmi.setAnnotations(annotations);
            typeMappingInfos[0] = tmi;          
        }
        return typeMappingInfos;        
    }
    
    protected Object getControlObject() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("one", "two");
        map.put("three", "four");
        QName qname = new QName("", "testTagName");
        JAXBElement jaxbElement = new JAXBElement(qname,Object.class, null);
        jaxbElement.setValue(map);

        return jaxbElement;
    }
    
    public Map<String, InputStream> getControlSchemaFiles(){                       
        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/maptypeoverride.xsd");
        
        Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
        controlSchema.put("", instream);
        return controlSchema;
    }

    protected String getNoXsiTypeControlResourceName() {
        return XML_RESOURCE;
    }    

}
