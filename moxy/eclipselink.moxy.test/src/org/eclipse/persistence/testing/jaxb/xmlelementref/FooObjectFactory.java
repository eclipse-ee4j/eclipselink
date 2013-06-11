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
*    Denise Smith - June 2013
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelementref;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class FooObjectFactory {
	
	private final static QName _Things_QNAME = new QName("", "things");

	public FooObjectFactory() {
	}
	
	@XmlElementDecl(namespace = "", name = "things")
	public JAXBElement<List<byte[]>> createThings(List<byte[]> value) {
	   return new JAXBElement<List<byte[]>>(_Things_QNAME, ((Class) List.class),((List<byte[]> ) value));
	}
}
