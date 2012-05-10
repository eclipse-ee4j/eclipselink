/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith  January 26, 2010 - 2.0.1
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelementref.missingref;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class MissingRefObjectFactory {
	
    private final static QName theQName = new QName("", "arg0");

    public MissingRefObjectFactory() {
    }
    
    @XmlElementDecl(namespace = "", name = "arg0", scope = Person.class)
    public JAXBElement<byte[]> createEchoByteArrayArg0(byte[] value) {
        return new JAXBElement<byte[]>(theQName, byte[].class, Person.class, ((byte[]) value));
    }
}
