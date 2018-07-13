/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     bdoughan - July 7/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.oxm.xmlroot.nil;

import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class XMLRootNilForceWrapTestCases extends XMLMappingTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlroot/nil/nil.xml";

    public void setUp() throws Exception {
        super.setUp();
        this.xmlUnmarshaller.setResultAlwaysXMLRoot(true);
    }

    public XMLRootNilForceWrapTestCases(String name) throws Exception {
        super(name);
        if (null == System.getProperty(OXTestCase.PLATFORM_KEY)) {
            System.setProperty(OXTestCase.PLATFORM_KEY, OXTestCase.PLATFORM_DOM);
        }
        setControlDocument(XML_RESOURCE);
        setProject(new NilProject());
    }

    public Object getControlObject() {
        XMLRoot xmlRoot = new XMLRoot();
        xmlRoot.setLocalName("bar");
        xmlRoot.setNamespaceURI("urn:foo");
        return xmlRoot;
    }

}
