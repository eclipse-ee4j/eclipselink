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
 * dmccann - December 04/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementrefs;

@javax.xml.bind.annotation.XmlRegistry
public class ObjectFactory {
	@javax.xml.bind.annotation.XmlElementDecl(name="root")
	public javax.xml.bind.JAXBElement<String> createRoot() {
		return new javax.xml.bind.JAXBElement<String>(new javax.xml.namespace.QName("root"), String.class, "");
	}
	
	@javax.xml.bind.annotation.XmlElementDecl(namespace="myns", name="integer-root")
	public javax.xml.bind.JAXBElement<Integer> createIntegerRoot() {
		return new javax.xml.bind.JAXBElement<Integer>(new javax.xml.namespace.QName("myns", "integer-root"), Integer.class, new Integer(0));
	}

    @javax.xml.bind.annotation.XmlElementDecl(name="a")
    public javax.xml.bind.JAXBElement<String> createA() {
        return new javax.xml.bind.JAXBElement<String>(new javax.xml.namespace.QName("a"), String.class, "");
    }

    @javax.xml.bind.annotation.XmlElementDecl(name="b")
    public javax.xml.bind.JAXBElement<Integer> createB() {
        return new javax.xml.bind.JAXBElement<Integer>(new javax.xml.namespace.QName("b"), java.lang.Integer.class, 0);
    }
}
