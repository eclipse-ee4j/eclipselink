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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.sdo.helper.classgen;

import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;

public class UnionTestCases extends SDOClassGenTestCases {
    public UnionTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        //TODO: do we need to define types before generating classes???
        java.util.List types = xsdHelper.define(xsdString);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.classgen.UnionTestCases" };
        TestRunner.main(arguments);
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/simpletypes/SimpleTypeWithUnionType.xsd";
    }

    protected String getSourceFolder() {
        return "./union";
    }

    protected String getControlSourceFolder() {
        return "./org/eclipse/persistence/testing/sdo/helper/classgen/union";
    }

    protected List getControlFileNames() {
        ArrayList list = new ArrayList();
        list.add("TestComplexType.java");
        list.add("TestComplexTypeImpl.java");
        return list;
    }
}
