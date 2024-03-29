/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     bdoughan - Feb 23/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.transformation.element;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class ElementTestCases extends XMLMappingTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/transformation/element/Element.xml";
    private final static String CONTROL_ELEMENT = "INTERMEDIATE";

    public ElementTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new ElementProject());
    }

    @Override
    protected Object getControlObject() {
        Root root = new Root();
        root.setElement(CONTROL_ELEMENT);
        return root;
    }

}
