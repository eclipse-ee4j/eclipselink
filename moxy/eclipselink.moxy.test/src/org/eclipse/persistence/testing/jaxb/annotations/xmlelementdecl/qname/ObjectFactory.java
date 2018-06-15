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
//     Denise Smith - May 2013
package org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.qname;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

 private final static QName _theTest_QNAME = new QName("", "theTest");


 public ObjectFactory() {
 }


 @XmlElementDecl(namespace = "", name = "theTest")
 public JAXBElement<QName> createTheTest(QName value) {
     return new JAXBElement<QName>(_theTest_QNAME, QName.class, null, value);
 }

}
