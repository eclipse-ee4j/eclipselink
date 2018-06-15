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
//     Matt MacIvor - 2.3.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.xmlelementrefs2;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory2 {

    @XmlElementDecl(name = "p", scope = Customer.class)
    public JAXBElement<Phone> createPhone(Phone p) {
        return new JAXBElement<Phone>(new QName("p"), Phone.class, p);
    }

    @XmlElementDecl(name = "e", scope = Customer.class)
    public JAXBElement<Email> createEmail(Email e) {
        return new JAXBElement<Email>(new QName("e"), Email.class, e);
    }

}
