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
 * dmccann - December 30/2010 - 2.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.list;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlAdapterListTestCases extends JAXBWithJSONTestCases{
  
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/list/singlebar.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/list/singlebar.json";
    
    public XmlAdapterListTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{FooWithBar.class, Bar.class});
        setControlDocument(XML_RESOURCE);   
        setControlJSON(JSON_RESOURCE);
    }

	protected Object getControlObject() {
        FooWithBar fooWithBar = new FooWithBar();
	    List<String> itemlist = new ArrayList<String>();
	    itemlist = new ArrayList<String>();
	    itemlist.add(MyAdapter.VAL0);
	    itemlist.add(MyAdapter.VAL1);
	    itemlist.add(MyAdapter.VAL2);
	    fooWithBar.items = itemlist;
	    return fooWithBar;
	}

}