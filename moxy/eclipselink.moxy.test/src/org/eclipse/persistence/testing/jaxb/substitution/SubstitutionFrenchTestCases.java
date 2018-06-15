/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     rbarkhouse - 2010-05-07 16:53:48 - EclipseLink 2.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.substitution;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class SubstitutionFrenchTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/substitution/instance-fr.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/substitution/instance-fr.json";

    public SubstitutionFrenchTestCases(String name) throws Exception {
        super(name);
        Class[] classes = new Class[2];
        classes[0] = ObjectFactory.class;
        classes[1] = Person.class;
        setClasses(classes);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    public Object getControlObject() {
        Person personneObj = new Person();

        QName personneQName = new QName("myNamespace", "personne");
        JAXBElement personElement = new JAXBElement(personneQName, Person.class, personneObj);

        QName nomQName = new QName("myNamespace", "nom");
        JAXBElement<String> nomElement = new JAXBElement<String>(nomQName, String.class, "Bob Smith");

        personneObj.setName(nomElement);

        return personElement;
    }

}
