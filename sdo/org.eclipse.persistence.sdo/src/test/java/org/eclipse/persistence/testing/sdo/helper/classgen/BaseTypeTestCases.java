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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.sdo.helper.classgen;


import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;

public class BaseTypeTestCases extends SDOClassGenTestCases {

    public BaseTypeTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.classgen.BaseTypeTestCases" };
        TestRunner.main(arguments);
    }

    @Override
    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/CustomerWithExtension.xsd";
    }

    @Override
    protected String getSourceFolder() {
        return "./baseTypes";
    }

    @Override
    protected String getControlSourceFolder() {
        return "./org/eclipse/persistence/testing/sdo/helper/classgen/baseTypes";
    }

    @Override
    protected List getControlFileNames() {
        ArrayList list = new ArrayList();
        list.add("CustomerType.java");
        list.add("CustomerTypeImpl.java");
        list.add("CdnAddressType.java");
        list.add("CdnAddressTypeImpl.java");
        list.add("UsAddressType.java");
        list.add("UsAddressTypeImpl.java");
        list.add("AddressType.java");
        list.add("AddressTypeImpl.java");
        list.add("TestType.java");
        list.add("TestTypeImpl.java");
        return list;
    }
}
