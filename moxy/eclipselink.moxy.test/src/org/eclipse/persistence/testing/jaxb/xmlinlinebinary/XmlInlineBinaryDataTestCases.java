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
 * Denise Smith- February 2010 - 2.1 
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlinlinebinary;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlInlineBinaryDataTestCases extends JAXBTestCases{

	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlinlinebinary/xmlinlinebinary.xml";
	
	public XmlInlineBinaryDataTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);        
	    Class[] classes = new Class[1];
	    classes[0] = MyDataPropertyAnnotation.class;
	    setClasses(classes);
	}
	
	protected Object getControlObject() {
		MyDataPropertyAnnotation data = new MyDataPropertyAnnotation();		
		data.bytes = new byte[] { 0, 1, 2, 3 };
		data.bytesAttr = new byte[] { 0, 1, 2, 3 };
		return data;
	}
	
	public void testSchemaGen() throws Exception{
		
		List<InputStream> controlSchemas = new ArrayList<InputStream>();
		InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/xmlinlinebinary/xmlinlinebinary.xsd");
		controlSchemas.add(instream);
		super.testSchemaGen(controlSchemas);
	}

}
