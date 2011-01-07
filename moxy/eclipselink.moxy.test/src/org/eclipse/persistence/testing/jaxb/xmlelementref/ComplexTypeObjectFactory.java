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
 *     Denise Smith - November 5, 2009
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelementref;

import java.math.BigDecimal;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

	@XmlRegistry
	public class ComplexTypeObjectFactory {

	 private final static QName _Root_QNAME = new QName("clazz/typeDef", "root");
	 private final static QName _Global_QNAME = new QName("clazz/typeDef", "Global");

	 public ComplexTypeObjectFactory() {
	 }

	 public ComplexType createComplexType() {
	     return new ComplexType();
	 }
	
	 @XmlElementDecl(namespace = "clazz/typeDef", name = "root")
	 public JAXBElement<ComplexType> createRoot(ComplexType value) {
	     return new JAXBElement<ComplexType>(_Root_QNAME, ComplexType.class, null, value);
	 }
	
	 @XmlElementDecl(namespace = "clazz/typeDef", name = "Global")
	 public JAXBElement<Boolean> createGlobal(Boolean value) {
	     return new JAXBElement<Boolean>(_Global_QNAME, Boolean.class, null, value);
	 }
	
	 @XmlElementDecl(namespace = "", name = "Local", scope = ComplexType.class)
	 public ComplexType.TestLocal createComplexTypeTestLocal(BigDecimal value) {
	     return new ComplexType.TestLocal(value);
	 }

}
