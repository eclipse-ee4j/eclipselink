/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
// dmccann - 2.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.typemappinginfo.arraywithannotations;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.MarshalException;
import jakarta.xml.bind.annotation.XmlList;
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
            fail("An unexpected jakarta.xml.bind.JAXBException was thrown.");
        }
        fail("The expected jakarta.xml.bind.MarshalException was not thrown.");
    }
}
