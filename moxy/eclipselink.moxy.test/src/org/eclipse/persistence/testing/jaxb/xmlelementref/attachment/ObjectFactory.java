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
 * Oracle = 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelementref.attachment;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
    
    @XmlElementDecl(name="fooA")
    public JAXBElement<Byte[]> createFooA() {
        return new JAXBElement(new QName("fooA"), Byte[].class, new Byte[]{1, 2, 3, 4, 5});
    }

    @XmlElementDecl(name="fooB")
    public JAXBElement<byte[]> createFooB() {
        return new JAXBElement(new QName("fooB"), Byte[].class, new byte[]{1, 2, 3, 4, 5});
    }

    @XmlElementDecl(name="fooC")
    public JAXBElement<String> createFooC() {
        return new JAXBElement(new QName("fooC"), String.class, "StringValue");
    }
}
