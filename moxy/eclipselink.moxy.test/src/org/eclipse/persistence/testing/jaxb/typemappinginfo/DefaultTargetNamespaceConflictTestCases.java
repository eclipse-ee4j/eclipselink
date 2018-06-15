/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith -  January 2014
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.pkg2.Thing;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.pkg3.OtherThing;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class DefaultTargetNamespaceConflictTestCases extends TypeMappingInfoWithJSONTestCases{

    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/conflict/otherthing.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/conflict/otherthing.json";

    public DefaultTargetNamespaceConflictTestCases(String name) throws Exception {
        super(name);
        init();
    }

    public void init() throws Exception {
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setTypeMappingInfos(getTypeMappingInfos());
    }

    public TypeMappingInfo getTypeMappingInfo(){
          return getTypeMappingInfos()[0];
    }

    protected TypeMappingInfo[] getTypeMappingInfos() {
        if(typeMappingInfos == null) {
            typeMappingInfos = new TypeMappingInfo[2];

            TypeMappingInfo tmi2 = new TypeMappingInfo();
            tmi2.setType(OtherThing.class);
            typeMappingInfos[0] = tmi2;

            TypeMappingInfo tmi = new TypeMappingInfo();
            tmi.setType(Thing.class);
            typeMappingInfos[1] = tmi;
        }
        return typeMappingInfos;
    }

    protected Object getControlObject() {
        OtherThing otherThing = new OtherThing();
        otherThing.someProperty = 10;
        JAXBElement<OtherThing> jbe = new JAXBElement<OtherThing>(new QName("root"), OtherThing.class, otherThing);

        return jbe;

    }

    public void testCorrectNamespace() throws Exception{
        OtherThing otherThing = new OtherThing();
        otherThing.someProperty = 10;
        StringWriter sw = new StringWriter();

        jaxbMarshaller.marshal(otherThing, sw);

        InputSource is = new InputSource(new StringReader(sw.toString()));
        Node doc = parser.parse(is);
        assertNotNull(doc);
        assertTrue(doc instanceof Document);
        Element root = ((Document)doc).getDocumentElement();
        assertEquals("namespace1",root.getNamespaceURI());
    }


    protected Map getProperties() {
        HashMap props = new HashMap();
        props.put(JAXBContextFactory.DEFAULT_TARGET_NAMESPACE_KEY, "namespace1");

        return props;
    }

    @Override
    public Map<String, InputStream> getControlSchemaFiles() {
        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/conflict/schema.xsd");
        Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
        controlSchema.put("namespace1", instream);
        return controlSchema;
    }
}

