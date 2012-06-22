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
 *     Denise Smith - 2.4
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmlelementref.notincontext;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlElementRefNotGivenToContextTestCases extends JAXBTestCases{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/root.xml";

	public XmlElementRefNotGivenToContextTestCases(String name)throws Exception {
		super(name);
		setClasses(new Class[]{Root.class});
		setControlDocument(XML_RESOURCE);	
	}

	protected Object getControlObject() {
        Root root = new Root();
        List contents = new ArrayList();
        contents.add(new Foo());
        contents.add(new SubFoo());
        contents.add(new Bar());
        contents.add(new SubBar());
        root.content = contents;
        return root;
	}

}
