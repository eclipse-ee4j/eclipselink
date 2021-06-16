/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     bdoughan - Feb 23/2009 - 2.0 - Initial implementation
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
