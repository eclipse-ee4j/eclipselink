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
 *     Oracle - December 2011
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.inheritance.ns;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBXMLComparer;
import org.eclipse.persistence.testing.jaxb.events.Address;
import org.eclipse.persistence.testing.jaxb.events.Employee;
import org.eclipse.persistence.testing.jaxb.events.PhoneNumber;
import org.w3c.dom.Document;

public class JAXBInheritanceNSTestCases extends JAXBTestCases {
	public JAXBInheritanceNSTestCases(String name) throws Exception {
		super(name);
		setClasses(new Class[] {Root.class, SubType.class});
		setControlDocument("org/eclipse/persistence/testing/jaxb/inheritance/ns/inheritanceNS.xml");
	}

	public Object getControlObject() {
		Root root = new Root();
		SubType subType = new SubType();	
		root.baseTypeThing = subType;
		return root;
	}
}
