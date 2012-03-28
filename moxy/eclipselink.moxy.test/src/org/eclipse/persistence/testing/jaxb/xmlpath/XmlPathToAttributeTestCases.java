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
 * Denise Smith - 2.4
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlpath;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlPathToAttributeTestCases extends JAXBWithJSONTestCases {
	   private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlpath/xmlpath.xml";
	   private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlpath/xmlpath.json";

		public XmlPathToAttributeTestCases(String name) throws Exception {
			super(name);
			setControlDocument(XML_RESOURCE);
			setControlJSON(JSON_RESOURCE);
			setClasses(new Class[]{TestObjectToAttribute.class});
		}

		@Override
		protected Object getControlObject() {
			TestObjectToAttribute obj = new TestObjectToAttribute();
			obj.theFlag = true;
	  		return obj;
		}
		
		public void testSchemaGen() throws Exception{
		   	List<InputStream> controlSchemas = new ArrayList<InputStream>();
		   	InputStream is = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/xmlpath/xmlpath.xsd");
		   	controlSchemas.add(is);
		   	super.testSchemaGen(controlSchemas);
		}
}
