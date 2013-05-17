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
package org.eclipse.persistence.testing.jaxb.xmlvariablenode.method;

import java.util.ArrayList;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlVariableNodeMethodGetOnlyTestCases extends JAXBWithJSONTestCases{
	 protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/root.xml";
	    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/root.json";

	public XmlVariableNodeMethodGetOnlyTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
	    setControlJSON(JSON_RESOURCE);
	    setClasses(new Class[]{RootGetOnly.class});
	}

	@Override
	protected Object getControlObject() {
		RootGetOnly r = new RootGetOnly();
		r.name = "theRootName";
		r.things = new ArrayList<ThingGetOnly>();
		ThingGetOnly thing1 = new ThingGetOnly("thinga");
		thing1.setThingValue("thingavalue");
		
		ThingGetOnly thing2 = new ThingGetOnly("thingb");		
		thing2.setThingValue("thingbvalue");
		
		ThingGetOnly thing3 = new ThingGetOnly("thingc");	
		thing3.setThingValue("thingcvalue");
		r.things.add(thing1);
		r.things.add(thing2);
		r.things.add(thing3);
		return r;
	}
	
	@Override
	public Object getReadControlObject() {
		RootGetOnly r = new RootGetOnly();
		r.name = "theRootName";
		r.things = new ArrayList<ThingGetOnly>();
		ThingGetOnly thing1 = new ThingGetOnly();
		thing1.setThingValue("thingavalue");
		
		ThingGetOnly thing2 = new ThingGetOnly();		
		thing2.setThingValue("thingbvalue");
		
		ThingGetOnly thing3 = new ThingGetOnly();	
		thing3.setThingValue("thingcvalue");
		r.things.add(thing1);
		r.things.add(thing2);
		r.things.add(thing3);
		return r;
	}

	public void testRoundTrip(){
		
	}
}
