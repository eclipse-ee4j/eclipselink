/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Radek Felcman - May 2018
 ******************************************************************************/
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
