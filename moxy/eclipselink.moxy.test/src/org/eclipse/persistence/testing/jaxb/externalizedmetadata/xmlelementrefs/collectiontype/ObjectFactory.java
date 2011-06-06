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
 * dmccann - November 24/2010 - 2.2 - Initial implementation
 ******************************************************************************/
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
