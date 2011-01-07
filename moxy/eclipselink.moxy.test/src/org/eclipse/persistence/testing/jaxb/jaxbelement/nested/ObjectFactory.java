/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - July 15, 2009
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.jaxbelement.nested;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

 private final static QName _Elem3_QNAME = new QName("someuri", "elem3");
 private final static QName _Elem1_QNAME = new QName("someuri", "elem1");
 private final static QName _Elem2_QNAME = new QName("someuri", "elem2");

 public ObjectFactory() {
 }

 public Root createRoot() {
     return new Root();
 }

 @XmlElementDecl(namespace = "someuri", name = "elem3")
 public JAXBElement<Object> createElem3(Object value) {
     return new JAXBElement<Object>(_Elem3_QNAME, Object.class, null, value);
 }

 @XmlElementDecl(namespace = "someuri", name = "elem1")
 public JAXBElement<Object> createElem1(Object value) {
     return new JAXBElement<Object>(_Elem1_QNAME, Object.class, null, value);
 }


 @XmlElementDecl(namespace = "someuri", name = "elem2", substitutionHeadNamespace = "someuri", substitutionHeadName = "elem1")
 public JAXBElement<Object> createElem2(Object value) {
     return new JAXBElement<Object>(_Elem2_QNAME, Object.class, null, value);
 }

}
