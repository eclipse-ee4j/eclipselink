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

public class PurchaseOrderWithAnnotationsClassGenTestCases extends SDOClassGenTestCases {

    public PurchaseOrderWithAnnotationsClassGenTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.classgen.PurchaseOrderWithAnnotationsClassGenTestCases" };
        TestRunner.main(arguments);
    }

    // Override package generation based on the JAXB 2.0 algorithm in SDOUtil.java
    protected List<String> getPackages() {
        if(null != packageNames && packageNames.size() > 0) {
            return packageNames;
        } else {
            packageNames = new ArrayList<String>();
            for(int i = 0;i < getFileNamesToCompile().size();i++) {
                packageNames.add("com/example/myPackage");
            }
        }
        return packageNames;
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderWithAnnotations.xsd";
    }

    protected String getSourceFolder() {
        return "./srcPOAnnotations";
    }

    protected String getControlSourceFolder() {
        return "./org/eclipse/persistence/testing/sdo/helper/classgen/srcPOAnnotations";
    }

     protected List getFileNamesToCompile(){
        List returnList = getControlFileNames();
        returnList.add("SKU.java");
        return returnList;
    }

    protected List getControlFileNames() {
        ArrayList list = new ArrayList();
        list.add("PurchaseOrder.java");
        list.add("PurchaseOrderImpl.java");
        list.add("ItemSDO.java");
        list.add("ItemSDOImpl.java");
        list.add("Items.java");
        list.add("ItemsImpl.java");
        list.add("USAddress.java");
        list.add("USAddressImpl.java");
        return list;
    }
}
