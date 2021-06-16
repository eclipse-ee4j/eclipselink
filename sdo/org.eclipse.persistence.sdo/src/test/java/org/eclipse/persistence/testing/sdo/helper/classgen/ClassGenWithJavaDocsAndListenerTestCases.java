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

public class ClassGenWithJavaDocsAndListenerTestCases extends SDOClassGenTestCases {

    public ClassGenWithJavaDocsAndListenerTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.classgen.ClassGenWithJavaDocsAndListenerTestCases" };
        TestRunner.main(arguments);
    }

    public void setUp() {
        super.setUp();
        classGenerator.setSDOClassGeneratorListener(new TestSDOClassGeneratorListener());
    }

    public void tearDown() throws Exception {
        super.tearDown();
        classGenerator.setSDOClassGeneratorListener(null);
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderWithJavaDocs.xsd";
    }

    protected String getSourceFolder() {
        return "./poJavadocsListener";
    }

    protected String getControlSourceFolder() {
        return "./org/eclipse/persistence/testing/sdo/helper/classgen/poJavadocsListener";
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
