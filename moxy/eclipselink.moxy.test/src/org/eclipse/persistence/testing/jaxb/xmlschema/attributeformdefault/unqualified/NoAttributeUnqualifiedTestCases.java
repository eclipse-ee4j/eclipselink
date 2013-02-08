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
package org.eclipse.persistence.testing.jaxb.xmlschema.attributeformdefault.unqualified;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class NoAttributeUnqualifiedTestCases extends JAXBWithJSONTestCases{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlschema/attributeformdefault/noAttributes.xml";
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlschema/attributeformdefault/noAttributes.json";

	public NoAttributeUnqualifiedTestCases(String name) throws Exception {
	    super(name);
		setClasses(new Class[]{RootNoAttributes.class});
		setControlDocument(XML_RESOURCE); 
		setControlJSON(JSON_RESOURCE);
    }
	
	protected Object getControlObject() {
	    RootNoAttributes root = new RootNoAttributes();
		root.child = "abc";
		return root;
	}
}
