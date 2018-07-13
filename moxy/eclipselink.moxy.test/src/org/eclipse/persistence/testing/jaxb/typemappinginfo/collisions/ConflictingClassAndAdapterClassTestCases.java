/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Denise Smith -  January, 2010 - 2.0.1
package org.eclipse.persistence.testing.jaxb.typemappinginfo.collisions;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.ListToStringAdapter;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.TypeMappingInfoWithJSONTestCases;
import org.w3c.dom.Element;

public class ConflictingClassAndAdapterClassTestCases extends TypeMappingInfoWithJSONTestCases{
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/collisions/conflictingClassAndAdapterClass.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/collisions/conflictingClassAndAdapterClass.json";

    @XmlJavaTypeAdapter(ListToStringAdapter.class)
    public Object javaTypeAdapterField;

    @XmlJavaTypeAdapter(value=ListToStringAdapter.class, type=String.class)
    public Object javaTypeAdapterField2;

    public ConflictingClassAndAdapterClassTestCases(String name) throws Exception {
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
        if(typeMappingInfos == null) {
            typeMappingInfos = new TypeMappingInfo[6];

            TypeMappingInfo tmi2 = new TypeMappingInfo();
            tmi2.setXmlTagName(new QName("someUri","testTagName2"));
            tmi2.setElementScope(ElementScope.Global);
            tmi2.setType(List.class);
            Annotation[] annotations = new Annotation[1];
            annotations[0] = getClass().getField("javaTypeAdapterField").getAnnotations()[0];
            tmi2.setAnnotations(annotations);
            typeMappingInfos[0] = tmi2;

            TypeMappingInfo tmi = new TypeMappingInfo();
            tmi.setXmlTagName(new QName("someUri","testTagName1"));
            tmi.setElementScope(ElementScope.Global);
            tmi.setType(String.class);
            typeMappingInfos[1] = tmi;

            TypeMappingInfo tmi3 = new TypeMappingInfo();
            tmi3.setXmlTagName(new QName("someUri","testTagName3"));
            tmi3.setElementScope(ElementScope.Global);
            tmi3.setType(String.class);
            typeMappingInfos[2] = tmi3;

            TypeMappingInfo tmi4 = new TypeMappingInfo();
            tmi4.setXmlTagName(new QName("someUri","testTagName4"));
            tmi4.setElementScope(ElementScope.Global);
            tmi4.setType(List.class);
            Annotation[] annotations2 = new Annotation[1];
            annotations2[0] = getClass().getField("javaTypeAdapterField").getAnnotations()[0];
            tmi4.setAnnotations(annotations2);
            typeMappingInfos[3] = tmi4;

            TypeMappingInfo tmi5= new TypeMappingInfo();
            tmi5.setXmlTagName(new QName("someUri","testTagName5"));
            tmi5.setElementScope(ElementScope.Global);
            tmi5.setType(List.class);
            Annotation[] annotations3 = new Annotation[1];
            annotations3[0] = getClass().getField("javaTypeAdapterField2").getAnnotations()[0];
            tmi5.setAnnotations(annotations3);
            typeMappingInfos[4] = tmi5;

            TypeMappingInfo tmi6 = new TypeMappingInfo();
            tmi6.setXmlTagName(new QName("someUri","testTagName6"));
            tmi6.setElementScope(ElementScope.Global);
            tmi6.setType(List.class);
            Element xmlElement = getXmlElement("<xml-element xmlns='http://www.eclipse.org/eclipselink/xsds/persistence/oxm'><xml-java-type-adapter value='org.eclipse.persistence.testing.jaxb.typemappinginfo.ListToStringAdapter'/></xml-element>");

            tmi6.setXmlElement(xmlElement);

            typeMappingInfos[5] = tmi6;

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
        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/collisions/conflictingClassAndAdapterClass.xsd");

        Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
        controlSchema.put("someUri", instream);
        return controlSchema;
    }

    protected String getNoXsiTypeControlResourceName() {
        return XML_RESOURCE;
    }

    public void testDescriptorsSize(){
        List<ClassDescriptor> descriptors = ((org.eclipse.persistence.jaxb.JAXBContext)jaxbContext).getXMLContext().getSession(0).getProject().getOrderedDescriptors();
        assertEquals(2, descriptors.size());
    }

    /**
     * Tests different descriptors for testTagName1 and testTagName2.
     */
    public void testDifferentDescriptors() {
        List<ClassDescriptor> descriptors = ((org.eclipse.persistence.jaxb.JAXBContext)jaxbContext).getXMLContext().getSession(0).getProject().getOrderedDescriptors();

        Set<String> javaClasses = new HashSet<String>();

        for (ClassDescriptor descriptor : descriptors) {
            javaClasses.add(descriptor.getJavaClassName());
        }

        assertTrue("No generated class generated0", javaClasses.contains("org.eclipse.persistence.jaxb.generated0"));
        assertTrue("No generated class generated1", javaClasses.contains("org.eclipse.persistence.jaxb.generated1"));
    }
}
