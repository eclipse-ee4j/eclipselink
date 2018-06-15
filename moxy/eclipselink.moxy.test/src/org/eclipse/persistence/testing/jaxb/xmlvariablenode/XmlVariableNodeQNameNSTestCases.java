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
//     Denise Smith - May 2013
package org.eclipse.persistence.testing.jaxb.xmlvariablenode;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;

public class XmlVariableNodeQNameNSTestCases extends JAXBWithJSONTestCases{
     protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootqnameNS.xml";
     protected final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootqnameNSWrite.xml";
        protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootqnameNS.json";

    public XmlVariableNodeQNameNSTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setClasses(new Class[]{RootQName.class});
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("uri1", "xxx");
        namespaces.put("uri2", "yyy");
        namespaces.put("uri3", "zzz");

        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, namespaces);

    }

    protected Marshaller getJSONMarshaller() throws Exception{
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("uri1", "xxx");
        namespaces.put("uri2", "yyy");
        namespaces.put("uri3", "zzz");

        Marshaller m = jaxbContext.createMarshaller();
        m.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
        m.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, namespaces);

        return m;

    }

    protected Map getProperties() {
         Map overrides = new HashMap();
            String overridesString =
            "<?xml version='1.0' encoding='UTF-8'?>" +
            "<xml-bindings xmlns='http://www.eclipse.org/eclipselink/xsds/persistence/oxm'>" +
            "<xml-schema>" +
            "<xml-ns namespace-uri='uri2' prefix='yyy'/>" +
            "</xml-schema>" +
            "<java-types/>" +
            "</xml-bindings>";

            DOMSource src = null;
            try {
                Document doc = parser.parse(new ByteArrayInputStream(overridesString.getBytes()));
                src = new DOMSource(doc.getDocumentElement());
            } catch (Exception e) {
                e.printStackTrace();
                fail("An error occurred during setup");
            }

            overrides.put("org.eclipse.persistence.testing.jaxb.xmlvariablenode", src);

            Map props = new HashMap();
            props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, overrides);
            return props;
    }

    @Override
    protected Object getControlObject() {
        RootQName r = new RootQName();
        r.name = "theRootName";
        r.things = new ArrayList<Thing>();
        Thing thing1 = new Thing();
        thing1.thingName = "thinga";
        thing1.thingValue = "thingavalue";
        thing1.thingQName = new QName("uri1","thingQNamea");

        Thing thing2 = new Thing();
        thing2.thingName = "thingb";
        thing2.thingValue = "thingbvalue";
        thing2.thingQName = new QName("uri2","thingQNameb");


        Thing thing3 = new Thing();
        thing3.thingName = "thingc";
        thing3.thingValue = "thingcvalue";
        thing3.thingQName = new QName("uri3","thingQNamec");

        r.things.add(thing1);
        r.things.add(thing2);
        r.things.add(thing3);
        return r;
    }

}
