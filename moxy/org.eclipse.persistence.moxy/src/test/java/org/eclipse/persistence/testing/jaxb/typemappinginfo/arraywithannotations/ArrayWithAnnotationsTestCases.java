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
// dmccann - 2.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.typemappinginfo.arraywithannotations;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.annotation.XmlList;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.TypeMappingInfoWithJSONTestCases;

public class ArrayWithAnnotationsTestCases extends TypeMappingInfoWithJSONTestCases {
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/arraywithannotations/instance.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/arraywithannotations/instance.json";
    protected final static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/arraywithannotations/schema.xsd";
    protected final static QName XML_TAG_NAME = new QName("http://jaxb.dev.java.net/array", "FloatListType");

    @XmlList
    public Float[] f = new Float[] { 3.14f, 3.25f };

    public ArrayWithAnnotationsTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setTypeMappingInfos(getTypeMappingInfos());
    }

    protected TypeMappingInfo[] getTypeMappingInfos() throws Exception {
        if (typeMappingInfos == null) {
            typeMappingInfos = new TypeMappingInfo[1];

            TypeMappingInfo ti = new TypeMappingInfo();
            ti.setXmlTagName(XML_TAG_NAME);
            ti.setType(Float[].class);
            ti.setElementScope(ElementScope.Global);

            Field fld = getClass().getField("f");
            ti.setAnnotations(fld.getAnnotations());
            typeMappingInfos[0] = ti;
        }
        return typeMappingInfos;
    }

    protected Object getControlObject() {
        return new JAXBElement<Float[]>(XML_TAG_NAME, Float[].class, f);
    }

    public Map<String, InputStream> getControlSchemaFiles() {
        InputStream instream = ClassLoader.getSystemResourceAsStream(XSD_RESOURCE);
        Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
        controlSchema.put(XML_TAG_NAME.getNamespaceURI(), instream);
        return controlSchema;
    }

    public void testMarshalWithoutTMIExceptionHandling() {
        try {
            jaxbContext.createMarshaller().marshal(getControlObject(), System.out);
        } catch (MarshalException e) {
            return;
        } catch (JAXBException j) {
            fail("An unexpected javax.xml.bind.JAXBException was thrown.");
        }
        fail("The expected javax.xml.bind.MarshalException was not thrown.");
    }
}
