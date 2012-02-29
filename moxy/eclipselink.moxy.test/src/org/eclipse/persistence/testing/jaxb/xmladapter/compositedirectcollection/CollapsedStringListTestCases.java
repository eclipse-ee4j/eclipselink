/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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

public class CollapsedStringListTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE_READ = "org/eclipse/persistence/testing/jaxb/xmladapter/collapsedstring_read_list.xml"; 
    private static final String XML_RESOURCE_WRITE = "org/eclipse/persistence/testing/jaxb/xmladapter/collapsedstring_write_list.xml"; 

    private static final String JSON_RESOURCE_READ = "org/eclipse/persistence/testing/jaxb/xmladapter/collapsedstring_read_list.json"; 
    private static final String JSON_RESOURCE_WRITE = "org/eclipse/persistence/testing/jaxb/xmladapter/collapsedstring_write_list.json";
    private static final String JSON_RESOURCE_WRITE_FORMATTED = "org/eclipse/persistence/testing/jaxb/xmladapter/collapsedstring_write_list_formatted.json";
    
    public CollapsedStringListTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE_READ);
        setWriteControlDocument(XML_RESOURCE_WRITE);
        setControlJSON(JSON_RESOURCE_READ);
        setWriteControlJSON(JSON_RESOURCE_WRITE);
        setWriteControlFormattedJSON(JSON_RESOURCE_WRITE_FORMATTED);
        setClasses(new Class[] {CollapsedStringListRoot.class});
    }

    public boolean shouldRemoveEmptyTextNodesFromControlDoc() {
        return false;
    }
    
    @Override
    protected Object getControlObject() {
        CollapsedStringListRoot root = new CollapsedStringListRoot();

        List<String> elementFieldList = new ArrayList<String>(1);
        elementFieldList.add("C c");
        root.elementField = elementFieldList;

        List<String> elementPropertyList = new ArrayList<String>(1);
        elementPropertyList.add("D d");
        root.setElementProperty(elementPropertyList);

        return root;
    }


}
