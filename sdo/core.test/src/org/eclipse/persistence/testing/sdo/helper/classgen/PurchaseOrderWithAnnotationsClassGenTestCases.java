/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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

    protected String getPackageDir() {
        return "com/example/myPackage/";
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
