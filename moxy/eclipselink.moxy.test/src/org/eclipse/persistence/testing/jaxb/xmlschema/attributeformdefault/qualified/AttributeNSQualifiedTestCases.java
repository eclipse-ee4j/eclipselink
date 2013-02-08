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
 * Denise Smith - February 8, 2013
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlschema.attributeformdefault.qualified;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class AttributeNSQualifiedTestCases extends JAXBWithJSONTestCases{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlschema/attributeformdefault/withAttributeNSQualified.xml";
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlschema/attributeformdefault/withAttributeNSQualified.json";

	public AttributeNSQualifiedTestCases(String name) throws Exception {
	    super(name);
		setClasses(new Class[]{RootWithAttributeNS.class});
		setControlDocument(XML_RESOURCE); 
		setControlJSON(JSON_RESOURCE);
    }
	
	protected Object getControlObject() {
		RootWithAttributeNS root = new RootWithAttributeNS();
		root.child = "abc";
		return root;
	}	
}
