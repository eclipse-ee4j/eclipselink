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
 *     Praba Vijayaratnam - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.javadoc.xmlattachmentref;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlAttachmentRefExampleTest extends JAXBTestCases{

	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlattachmentref/xmlattachmentref.xml";
	
	public XmlAttachmentRefExampleTest(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		Class[] classes = new Class[1];
		classes[0] = RootObject.class;
		setClasses(classes);
	}

	protected Object getControlObject() {
       
		RootObject example = new RootObject();

		DataHandler data = new DataHandler("THISISATEXTSTRINGFORTHISDATAHANDLER", "data"); 
		DataHandler body = new DataHandler("THISISATEXTSTRINGFORTHISBODY", "body");
		example.data = data;
		example.body = body;
        return example;
	}


}
