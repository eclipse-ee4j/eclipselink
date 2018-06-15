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
package org.eclipse.persistence.testing.sdo.helper.classgen;

import java.util.ArrayList;
import java.util.List;

import junit.textui.TestRunner;

public class ListTestCases extends SDOClassGenTestCases {

    public ListTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        //TODO: do we need to define types before generating classes???
        xsdHelper.define(xsdString);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.classgen.ListTestCases" };
        TestRunner.main(arguments);
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/simpletypes/SimpleTypeWithListItem.xsd";
    }

    protected String getSourceFolder() {
        return "./list";
    }

    protected String getControlSourceFolder() {
        return "./org/eclipse/persistence/testing/sdo/helper/classgen/list";
    }

    protected List getControlFileNames() {
        ArrayList list = new ArrayList();
        list.add("TestComplexType.java");
        list.add("TestComplexTypeImpl.java");
        return list;
    }
}
