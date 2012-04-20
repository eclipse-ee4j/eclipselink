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
package org.eclipse.persistence.testing.jaxb.xmladapter.direct;


import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class CollapsedStringTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE_READ = "org/eclipse/persistence/testing/jaxb/xmladapter/collapsedstring_read.xml"; 
    private static final String XML_RESOURCE_WRITE = "org/eclipse/persistence/testing/jaxb/xmladapter/collapsedstring_write.xml"; 

    private static final String JSON_RESOURCE_READ = "org/eclipse/persistence/testing/jaxb/xmladapter/collapsedstring_read.json"; 
    private static final String JSON_RESOURCE_WRITE = "org/eclipse/persistence/testing/jaxb/xmladapter/collapsedstring_write.json"; 

    public CollapsedStringTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE_READ);
        setControlJSON(JSON_RESOURCE_READ);
        setWriteControlDocument(XML_RESOURCE_WRITE);
        setWriteControlJSON(JSON_RESOURCE_WRITE);
        setClasses(new Class[] {CollapsedStringRoot.class});
        jaxbMarshaller.setProperty(JAXBMarshaller.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbUnmarshaller.setProperty(JAXBUnmarshaller.JSON_ATTRIBUTE_PREFIX, "@");
    }

    @Override
    protected Object getControlObject() {
        CollapsedStringRoot root = new CollapsedStringRoot();
        root.attributeField = "A a";
        root.setAttributeProperty("B b");
        root.elementField = "C c";
        root.setElementProperty("D d");
        return root;
    }
}
