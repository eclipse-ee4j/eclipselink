/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 28 February 2013 - 2.4.2 - Initial implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.jaxb.jaxbcontext.xlink;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

    private final static QName _Title_QNAME = new QName("myNamespace", "title");
    private final static QName _Locator_QNAME = new QName("myNamespace", "locator");

    public ObjectFactory() {
    }

    public TitleEltType createTitleEltType() {
        return new TitleEltType();
    }

    public LocatorType createLocatorType() {
        return new LocatorType();
    }

    @XmlElementDecl(namespace = "myNamespace", name = "title")
    public JAXBElement<TitleEltType> createTitle(TitleEltType value) {
        return new JAXBElement<TitleEltType>(_Title_QNAME, TitleEltType.class, null, value);
    }

    @XmlElementDecl(namespace = "myNamespace", name = "locator")
    public JAXBElement<LocatorType> createLocator(LocatorType value) {
        return new JAXBElement<LocatorType>(_Locator_QNAME, LocatorType.class, null, value);
    }

}