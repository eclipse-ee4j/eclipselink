/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class ObjectFactory2 {

    private final static QName _Nom_QNAME = new QName("myNamespace", "nom");
    private final static QName _Person_QNAME = new QName("myNamespace", "person");
    private final static QName _Name_QNAME = new QName("myNamespace", "name");
    private final static QName _Personne_QNAME = new QName("myNamespace", "personne");

    public ObjectFactory2() {
    }

    public Person createPerson() {
        return new Person();
    }

    public JAXBElement<String> createNom(String value) {
        return new JAXBElement<String>(_Nom_QNAME, String.class, null, value);
    }

    public JAXBElement<Person> createPerson(Person value) {
        return new JAXBElement<Person>(_Person_QNAME, Person.class, null, value);
    }

    public JAXBElement<String> createName(String value) {
        return new JAXBElement<String>(_Name_QNAME, String.class, null, value);
    }

    public JAXBElement<Person> createPersonne(Person value) {
        return new JAXBElement<Person>(_Personne_QNAME, Person.class, null, value);
    }

}