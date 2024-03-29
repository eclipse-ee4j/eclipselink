/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement;

import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class AnyObjectTextChildTestCases extends XMLWithJSONMappingTestCases {
    public AnyObjectTextChildTestCases(String name) throws Exception {
        super(name);
        setProject(new AnyObjectWithoutGroupingElementProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anyobject/withoutgroupingelement/text_child.xml");
        setControlJSON("org/eclipse/persistence/testing/oxm/mappings/anyobject/withoutgroupingelement/text_child.json");
    }

    @Override
    public Object getControlObject() {
        Root root = new Root();
        root.setAny("root's text");

        return root;
    }
}
