/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Matt MacIvor - 2.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;

public class ByteArrayTestCases extends TypeMappingInfoWithJSONTestCases {
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/BigByteArray.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/BigByteArray.json";

    public Byte[] byteArrayField;

    public ByteArrayTestCases(String name) throws Exception {
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
