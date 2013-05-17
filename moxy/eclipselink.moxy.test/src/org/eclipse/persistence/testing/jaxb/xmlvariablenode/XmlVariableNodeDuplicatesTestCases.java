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

public class XmlVariableNodeDuplicatesTestCases extends JAXBWithJSONTestCases{
	 protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootduplicates.xml";
	    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootduplicates.json";

	public XmlVariableNodeDuplicatesTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
	    setControlJSON(JSON_RESOURCE);
	    setClasses(new Class[]{Root.class});
	}

	@Override
	protected Object getControlObject() {
		Root r = new Root();
		r.name = "theRootName";
		r.things = new ArrayList<Thing>();
		Thing thing1 = new Thing();
		thing1.thingName = "thinga";
		thing1.thingValue = "thingavalue";
		
		Thing thing2 = new Thing();
		thing2.thingName = "thingb";
		thing2.thingValue = "thingbvalue";
		
		Thing thing3 = new Thing();
		thing3.thingName = "thingc";
		thing3.thingValue = "thingcvalue";
		
		Thing thing4= new Thing();
		thing4.thingName = "thinga";
		thing4.thingValue = "thingavalueagain";
		r.things.add(thing1);
		r.things.add(thing2);
		r.things.add(thing3);
		r.things.add(thing4);
		return r;
	}
	
	@Override
	protected Object getJSONReadControlObject() {
		Root r = new Root();
		r.name = "theRootName";
		r.things = new ArrayList<Thing>();
		Thing thing1 = new Thing();
		thing1.thingName = "thinga";
		thing1.thingValue = "thingavalue";
		
		Thing thing2 = new Thing();
		thing2.thingName = "thingb";
		thing2.thingValue = "thingbvalue";
		
		Thing thing3 = new Thing();
		thing3.thingName = "thingc";
		thing3.thingValue = "thingcvalue";
		
		Thing thing4= new Thing();
		thing4.thingName = "thinga";
		thing4.thingValue = "thingavalueagain";
		r.things.add(thing1);
		r.things.add(thing4);
		r.things.add(thing2);
		r.things.add(thing3);
		return r;
	}

}
