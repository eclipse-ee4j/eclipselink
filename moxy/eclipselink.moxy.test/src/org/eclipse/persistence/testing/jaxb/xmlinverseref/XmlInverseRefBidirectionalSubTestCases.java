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
 *     Denise Smith, February 2013
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlinverseref;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlInverseRefBidirectionalSubTestCases extends JAXBWithJSONTestCases{
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlinverseref/bidirectionalSub.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlinverseref/bidirectionalSub.json";
    private final static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlinverseref/bidirectionalSub.xsd";
    
	public XmlInverseRefBidirectionalSubTestCases(String name) throws Exception {
		super(name);
		setControlJSON(JSON_RESOURCE);
		setControlDocument(XML_RESOURCE);
		setClasses(new Class[]{Child.class});
	}

	@Override
	protected Object getControlObject() {
		Child p = new Child();
		p.name = "theName";
		Address addr = new Address();
		addr.street = "theStreet";
		addr.owner = p;
		p.addr = addr;
		p.age =10;
		return p;
	}

	
	public void testSchemaGen() throws Exception{
		List<InputStream> controlSchemas = new ArrayList<InputStream>();
		controlSchemas.add(getClass().getClassLoader().getResourceAsStream(XSD_RESOURCE));
		super.testSchemaGen(controlSchemas);
	}
	
}
