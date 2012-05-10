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
 * dmccann - December 15/2009 - 2.0.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;

public class DuplicateListOfStringsTestCasesWithXML extends DuplicateListOfStringsTestCases {
    @XmlElement
    public List myList;

    public DuplicateListOfStringsTestCasesWithXML(String name) throws Exception {
        super(name);
        //useLogging = true;
    }

    // override this method such that XML metadata can be used instead of annotations
    protected TypeMappingInfo[] getTypeMappingInfos()throws Exception {
        if (typeMappingInfos == null){
            typeMappingInfos = new TypeMappingInfo[4];
        
            TypeMappingInfo tpi = new TypeMappingInfo();
            tpi.setXmlTagName(new QName("someUri","testTagname"));      
            tpi.setElementScope(ElementScope.Global);
            // set annotations - should be ignored since XML wins
            Annotation[] annotations = new Annotation[1];               
            annotations[0] = getClass().getField("myList").getAnnotations()[0];             
            tpi.setAnnotations(annotations);
            tpi.setXmlElement(getXmlElement("<xml-element xml-list=\"true\" />"));
            tpi.setType(List.class);        
            typeMappingInfos[0] = tpi;
        
            TypeMappingInfo tpi2 = new TypeMappingInfo();
            tpi2.setXmlTagName(new QName("someUri","testTagname2"));        
            tpi2.setElementScope(ElementScope.Global);      
            tpi2.setType(List.class);       
            typeMappingInfos[1] = tpi2;
        
            TypeMappingInfo tpi3 = new TypeMappingInfo();
            tpi3.setXmlTagName(new QName("someUri","testTagname3"));        
            tpi3.setElementScope(ElementScope.Global);                  
            tpi3.setType(getClass().getField("myListOfStrings").getGenericType());      
            typeMappingInfos[2] = tpi3;
            
            TypeMappingInfo tpi4 = new TypeMappingInfo();
            tpi4.setXmlTagName(new QName("someUri","testTagname4"));        
            tpi4.setElementScope(ElementScope.Global);      
            tpi4.setType(List.class);       
            typeMappingInfos[3] = tpi4;
        }
        
        return typeMappingInfos;
    }

    @Override
    public TypeMappingInfo getTypeMappingInfo() {
        return typeMappingInfos[1];
    }

}
