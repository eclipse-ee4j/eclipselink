/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Radek Felcman - May 2018
package org.eclipse.persistence.testing.jaxb.xmlvariablenode;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

import java.util.ArrayList;

public class XmlVariableNodeCollectionNullValueTestCases extends JAXBWithJSONTestCases{

    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootCollectionNull.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootCollectionNull.json";

    public XmlVariableNodeCollectionNullValueTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{RootNull.class, ChildNull.class});
    }

    @Override
    protected Object getControlObject() {
        RootNull rootNull = new RootNull();
        rootNull.childNulls = new ArrayList<ChildNull>();

        rootNull.childNulls.add(new ChildNull("key1", "value1"));
        rootNull.childNulls.add(new ChildNull("key2", ""));
        rootNull.childNulls.add(new ChildNull("key3", null));

        return rootNull;
    }

}
