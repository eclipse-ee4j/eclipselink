/*******************************************************************************
* Copyright (c) 1998, 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - Feb 23/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.transformation.element;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class ElementNSTestCases extends XMLMappingTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/transformation/element/ElementNS.xml";
    private final static String CONTROL_ELEMENT = "INTERMEDIATE";

    public ElementNSTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new ElementNSProject());
    }

    protected Object getControlObject() {
        Root root = new Root();
        root.setElement(CONTROL_ELEMENT);
        return root;
    }

}
