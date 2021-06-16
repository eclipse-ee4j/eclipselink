/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
// dmccann - December 30/2010 - 2.3 - Initial implementation
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
