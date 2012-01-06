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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class XMLDeclarationTestCases extends MarshallerFragmentTestCases{

	public XMLDeclarationTestCases(String name) {
		super(name);
	}

	 public void setUp() throws Exception {
	        //set up XMLMarshaller
	        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	        builderFactory.setNamespaceAware(true);
	        builderFactory.setIgnoringElementContentWhitespace(true);
	        DocumentBuilder parser = builderFactory.newDocumentBuilder();

	        String contextPath = System.getProperty("jaxb.test.contextpath", JAXBSAXTestSuite.CONTEXT_PATH);
	        JAXBContext jaxbContext = JAXBContext.newInstance(contextPath, getClass().getClassLoader());
	        marshaller = jaxbContext.createMarshaller();

	        originalSetting = true;
	        marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);

	        //set up controlObject
	        controlObject = new Employee();
	        controlObject.setName(CONTROL_EMPLOYEE_NAME);

	        //set up control Document
	        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
	        controlDocument = parser.parse(inputStream);
	        removeEmptyTextNodes(controlDocument);
	    }
}
