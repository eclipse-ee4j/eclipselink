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
package org.eclipse.persistence.testing.oxm.mappings.anyobject.withgroupingelement;

/**
 *  @version $Header: AnyObjectNoChildTestCases.java 07-oct-2005.21:46:03 pkrogh Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */


import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class AnyObjectNoChildTestCases extends XMLWithJSONMappingTestCases {
    public AnyObjectNoChildTestCases(String name) throws Exception {
        super(name);
        setProject(new AnyObjectWithGroupingElementProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anyobject/withgroupingelement/no_child.xml");
        setControlJSON("org/eclipse/persistence/testing/oxm/mappings/anyobject/withgroupingelement/no_child.json");
    }

    public Object getControlObject() {
        Root root = new Root();

        return root;
    }
}
