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
// Denise Smith - 2.4 - April 2012
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.xmlelementrefs2;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlAnyElementWithEltRefsNonGlobalTestCases extends JAXBWithJSONTestCases{

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/xmlelementrefs/nonglobal.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/xmlelementrefs/nonglobal.json";


    public XmlAnyElementWithEltRefsNonGlobalTestCases(String name) throws Exception{
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{Customer.class, ObjectFactory2.class });
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
    }

      public Map getProperties(){
            InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/xmlelementrefs/foo-oxm.xml");

            HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
            metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.xmlelementrefs", new StreamSource(inputStream));
            Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
            properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

            return properties;
        }

    public Object getControlObject(){
        Customer c = new Customer();
        c.id = 1221;
        c.name = "Dan Savage";
        ObjectFactory2 factory = new ObjectFactory2();
        c.contacts.add(factory.createPhone(new Phone(771, "5552328828")));
        c.contacts.add(factory.createPhone(new Phone(772, "5552322112")));
        c.contacts.add(factory.createPhone(new Phone(773, "5552329919")));
        c.contacts.add("Mixed Content 1");
        c.contacts.add(factory.createEmail(new Email(552, "dan@xpress.ca")));
        c.contacts.add("Mixed Content 2");
        c.contacts.add(factory.createEmail(new Email(553, "d.savage@hotmail.com")));

        return c;
    }

    public Object getJSONReadControlObject(){
        Customer c = new Customer();
        c.id = 1221;
        c.name = "Dan Savage";
        ObjectFactory2 factory = new ObjectFactory2();
        c.contacts.add(factory.createPhone(new Phone(771, "5552328828")));
        c.contacts.add(factory.createPhone(new Phone(772, "5552322112")));
        c.contacts.add(factory.createPhone(new Phone(773, "5552329919")));
        c.contacts.add(factory.createEmail(new Email(552, "dan@xpress.ca")));
        c.contacts.add(factory.createEmail(new Email(553, "d.savage@hotmail.com")));
        c.contacts.add("Mixed Content 1");
        c.contacts.add("Mixed Content 2");
        return c;
    }
}
