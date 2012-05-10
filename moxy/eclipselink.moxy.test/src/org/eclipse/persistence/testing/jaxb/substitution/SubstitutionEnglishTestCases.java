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
 *     rbarkhouse - 2010-05-07 16:53:48 - EclipseLink 2.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.substitution;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class SubstitutionEnglishTestCases extends JAXBWithJSONTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/substitution/instance-en.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/substitution/instance-en.json";
    
    public SubstitutionEnglishTestCases(String name) throws Exception {
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

        QName personneQName = new QName("myNamespace", "person");
        JAXBElement personElement = new JAXBElement(personneQName, Person.class, personneObj);

        QName nomQName = new QName("myNamespace", "name");
        JAXBElement<String> nomElement = new JAXBElement<String>(nomQName, String.class, "Bob Smith");

        personneObj.setName(nomElement);

        return personElement;
    }

}