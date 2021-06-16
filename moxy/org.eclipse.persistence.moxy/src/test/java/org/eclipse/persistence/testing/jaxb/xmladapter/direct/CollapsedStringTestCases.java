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
//     Blaise Doughan - 2.3.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmladapter.direct;


import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
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
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
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
