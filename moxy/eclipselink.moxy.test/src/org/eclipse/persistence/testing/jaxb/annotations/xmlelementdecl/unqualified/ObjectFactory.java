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
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.unqualified;

import java.math.BigDecimal;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

    private final static QName _Root_QNAME = new QName("urn:example", "root");
    private final static QName _Global_QNAME = new QName("urn:example", "Global");

    public ObjectFactory() {
    }

    public ComplexType createComplexType() {
        return new ComplexType();
    }

    @XmlElementDecl(namespace = "urn:example", name = "root")
    public JAXBElement<ComplexType> createRoot(ComplexType value) {
        return new JAXBElement<ComplexType>(_Root_QNAME, ComplexType.class, null, value);
    }

    @XmlElementDecl(namespace = "urn:example", name = "Global")
    public JAXBElement<Boolean> createGlobal(Boolean value) {
        return new JAXBElement<Boolean>(_Global_QNAME, Boolean.class, null, value);
    }

    @XmlElementDecl(namespace = "", name = "Local", scope = ComplexType.class)
    public ComplexType.TestLocal createComplexTypeTestLocal(String value) {
        return new ComplexType.TestLocal(value);
    }

}