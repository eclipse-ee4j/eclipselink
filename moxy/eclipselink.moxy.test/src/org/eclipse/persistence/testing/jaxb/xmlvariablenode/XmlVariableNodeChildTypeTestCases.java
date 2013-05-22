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
 *     Denise Smith - May 2013
 ******************************************************************************/ 
package org.eclipse.persistence.testing.jaxb.xmlvariablenode;

import java.util.ArrayList;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlVariableNodeChildTypeTestCases extends JAXBWithJSONTestCases{
	 protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootChildtype.xml";
	    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootChildtype.json";

	public XmlVariableNodeChildTypeTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
	    setControlJSON(JSON_RESOURCE);
	    setClasses(new Class[]{RootChildType.class, Thing.class, Child.class});
	}

	@Override
	protected Object getControlObject() {
		RootChildType r = new RootChildType();
		r.name = "theRootName";
		r.things = new ArrayList<Thing>();
		Child thing1 = new Child();
		thing1.thingName = "thinga";
		thing1.thingValue = "thingavalue";
		
		Child thing2 = new Child();
		thing2.thingName = "thingb";
		thing2.thingValue = "thingbvalue";
		
		Child thing3 = new Child();
		thing3.childAttribute = "childAttr";
		thing3.thingName = "thingc";
		thing3.thingValue = "thingcvalue";
		r.things.add(thing1);
		r.things.add(thing2);
		r.things.add(thing3);
		return r;
	}

}
