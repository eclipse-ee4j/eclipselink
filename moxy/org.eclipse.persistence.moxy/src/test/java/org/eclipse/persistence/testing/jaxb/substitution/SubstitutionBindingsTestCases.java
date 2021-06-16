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
//     Rick Barkhouse - 2.3.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.substitution;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class SubstitutionBindingsTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/substitution/instance-bindings.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/substitution/instance.json";
    private final static String XML_BINDINGS = "org/eclipse/persistence/testing/jaxb/substitution/xml-bindings.xml";

    public SubstitutionBindingsTestCases(String name) throws Exception {
        super(name);
        Class[] classes = new Class[2];
        classes[0] = Person2.class;
        classes[1] = ObjectFactory2.class;
        setClasses(classes);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    @Override
    public Object getControlObject() {
        Person personneObj = new Person();

        QName personneQName = new QName("myNamespace", "personne");
        JAXBElement personElement = new JAXBElement(personneQName, Person.class, personneObj);

        QName nomQName = new QName("myNamespace", "nom");
        JAXBElement<String> nomElement = new JAXBElement<String>(nomQName, String.class, "Bob Smith");

        personneObj.setName(nomElement);

        return personElement;
    }

    @Override
    protected Map getProperties() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XML_BINDINGS);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        return properties;
    }

}
