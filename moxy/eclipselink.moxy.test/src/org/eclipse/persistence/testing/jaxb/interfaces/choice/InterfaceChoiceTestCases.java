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
*     Denise Smith - February 25, 2013
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.interfaces.choice;

import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class InterfaceChoiceTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/interfaces/choice.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/interfaces/choice.json";

    public InterfaceChoiceTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Root.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }
  
	@Override
	protected Object getControlObject() {
		Root root = new Root();
		
        ArrayList<MyInterface> things = new ArrayList<MyInterface>();

		MyObject mo = new MyObject();
	    Properties p = new Properties();
	    p.put("formatted", true);
	    mo.setProperties(p);

	    MyOtherObject ro = new MyOtherObject();
	    Properties rp = new Properties();
	    rp.put("formatted", false);
	    ro.setProperties(rp);
	        
	    things.add(mo);
	    things.add(ro);
	        
	    root.setMyList(things);	
		return root;
	}

}