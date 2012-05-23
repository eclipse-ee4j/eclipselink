/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Rick Barkhouse - 2.3.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.substitution;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class SubstitutionBindingsTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/substitution/instance.xml";
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