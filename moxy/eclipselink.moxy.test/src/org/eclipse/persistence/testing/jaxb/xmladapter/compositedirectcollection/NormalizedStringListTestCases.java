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
 *     Blaise Doughan - 2.3.2 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmladapter.compositedirectcollection;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class NormalizedStringListTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE_READ = "org/eclipse/persistence/testing/jaxb/xmladapter/normalizedstring_read_list.xml"; 
    private static final String XML_RESOURCE_WRITE = "org/eclipse/persistence/testing/jaxb/xmladapter/normalizedstring_write_list.xml"; 
    private static final String JSON_RESOURCE_READ = "org/eclipse/persistence/testing/jaxb/xmladapter/normalizedstring_read_list.json"; 
    private static final String JSON_RESOURCE_WRITE = "org/eclipse/persistence/testing/jaxb/xmladapter/normalizedstring_write_list.json";
    public NormalizedStringListTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE_READ);
        setControlJSON(JSON_RESOURCE_READ);
        setWriteControlDocument(XML_RESOURCE_WRITE);
        setWriteControlJSON(JSON_RESOURCE_WRITE);
        setClasses(new Class[] {NormalizedStringListRoot.class});
    }

    @Override
    protected Object getControlObject() {
        NormalizedStringListRoot root = new NormalizedStringListRoot();

        List<String> elementFieldList = new ArrayList<String>(1);
        elementFieldList.add("   C   c   ");
        root.elementField = elementFieldList;

        List<String> elementPropertyList = new ArrayList<String>(1);
        elementPropertyList.add("   D   d   ");
        root.setElementProperty(elementPropertyList);

        return root;
    }

}
