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
 *     Matt MacIvor - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.schemagen.anonymoustype.inheritance;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;



@XmlRegistry
public class ObjectFactory {

    public ObjectFactory() {
    }

    public Bar createBar() {
        return new Bar();
    }

    @XmlElementDecl(namespace = "", name = "foo")
    public JAXBElement<Foo> createFoo(Foo value) {
        return new JAXBElement<Foo>(new QName("", "foo"), Foo.class, null, value);
    }

    @XmlElementDecl(namespace = "", name = "bar", substitutionHeadNamespace = "", substitutionHeadName = "foo")
    public JAXBElement<Bar> createBar(Bar value) {
        return new JAXBElement<Bar>(new QName("", "bar"), Bar.class, null, value);
    }

}
