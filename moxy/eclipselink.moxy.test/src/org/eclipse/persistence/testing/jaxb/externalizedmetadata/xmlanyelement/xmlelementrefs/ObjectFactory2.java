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
 *     Matt MacIvor - 2.3.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.xmlelementrefs;

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
