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
// dmccann - November 24/2010 - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementrefs.collectiontype;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

//@XmlRegistry
public class ObjectFactory {
    private final static QName _RootTime_QNAME = new QName("", "time");
    private final static QName _RootDate_QNAME = new QName("", "date");

    public ObjectFactory() {
    }

    public Root createRoot() {
        return new Root();
    }

    //@XmlElementDecl(namespace = "", name = "time", scope = Root.class)
    public JAXBElement<XMLGregorianCalendar> createRootTime(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_RootTime_QNAME, XMLGregorianCalendar.class, Root.class, value);
    }

    //@XmlElementDecl(namespace = "", name = "date", scope = Root.class)
    public JAXBElement<XMLGregorianCalendar> createRootDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_RootDate_QNAME, XMLGregorianCalendar.class, Root.class, value);
    }
}
