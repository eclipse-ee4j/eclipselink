/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.anyattribute.usemaptests;

import java.util.Hashtable;
import javax.xml.namespace.QName;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

/**
 *
 */
public class AnyAttributeUsingMapTestCases extends XMLMappingTestCases {
    public AnyAttributeUsingMapTestCases(String name) throws Exception {
        super(name);
        setProject(new AnyAttributeUsingMapProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anyattribute/usemaptests/multiple_attributes.xml");
    }

    public Object getControlObject() {
        Root root = new Root();
        Hashtable any = new Hashtable();
        QName name = new QName("", "first-name");
        any.put(name, "Matt");
        name = new QName("", "last-name");
        any.put(name, "MacIvor");
        root.setAny(any);
        return root;
    }
}
