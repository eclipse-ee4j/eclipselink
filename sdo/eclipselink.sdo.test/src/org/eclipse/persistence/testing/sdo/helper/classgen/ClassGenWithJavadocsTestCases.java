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

public class ClassGenWithJavadocsTestCases   extends SDOClassGenTestCases {

    public ClassGenWithJavadocsTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.classgen.ClassGenWithJavadocsTestCases" };
        TestRunner.main(arguments);
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderWithJavaDocs.xsd";
    }

    protected String getSourceFolder() {
        return "./poJavadocs";
    }

    protected String getControlSourceFolder() {
        return "./org/eclipse/persistence/testing/sdo/helper/classgen/poJavadocs";
    }

      protected List getControlFileNames() {
        ArrayList list = new ArrayList();
        list.add("LineItemType.java");
        list.add("LineItemTypeImpl.java");
        list.add("Items.java");
        list.add("ItemsImpl.java");
        list.add("PurchaseOrderType.java");
        list.add("PurchaseOrderTypeImpl.java");
        list.add("AddressType.java");
        list.add("AddressTypeImpl.java");
        return list;
    }
}
