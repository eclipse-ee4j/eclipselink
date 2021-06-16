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

import jakarta.xml.bind.JAXBElement;
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
