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

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlVariableNodeArrayTestCases extends JAXBWithJSONTestCases{
	 protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/root.xml";
	 protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/root.json";

	public XmlVariableNodeArrayTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
	    setControlJSON(JSON_RESOURCE);
	    setClasses(new Class[]{RootArray.class});
	}

	@Override
	protected Object getControlObject() {
		RootArray r = new RootArray();
		r.name = "theRootName";
		r.things = new Thing[3];
		Thing thing1 = new Thing();
		thing1.thingName = "thinga";
		thing1.thingValue = "thingavalue";
		
		Thing thing2 = new Thing();
		thing2.thingName = "thingb";
		thing2.thingValue = "thingbvalue";
		
		Thing thing3 = new Thing();
		thing3.thingName = "thingc";
		thing3.thingValue = "thingcvalue";
		r.things[0] = thing1;
		r.things[1] = thing2;
		r.things[2] = thing3;
		return r;
	}

}
