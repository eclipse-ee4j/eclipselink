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
 *     rbarkhouse - 2009-10-06 10:01:09 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.anyattribute.reuse;

import java.net.URL;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class AnyAttributeReuseTestCases extends XMLWithJSONMappingTestCases {

    public AnyAttributeReuseTestCases(String name) throws Exception {
        super(name);
        setProject(new AnyAttributeReuseProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anyattribute/reuse/reuse.xml");
        setControlJSON("org/eclipse/persistence/testing/oxm/mappings/anyattribute/reuse/reuse.json");
    }

    public Object getControlObject() {
        Root root = new Root();
        Properties any = new Properties();
        QName name = new QName("", "first-name");
        any.put(name, "Matt");
        name = new QName("", "last-name");
        any.put(name, "MacIvor");
        root.setAny(any);
        return root;
    }

    public void testContainerReused() throws Exception {
        URL url = ClassLoader.getSystemResource(resourceName);
        Root testObject = (Root) xmlUnmarshaller.unmarshal(url);

        assertEquals("This mapping's container was not reused.", Properties.class, testObject.getAny().getClass());
    }

}