/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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

public class NestedBaseTypesTestCases extends SDOClassGenTestCases {

    public NestedBaseTypesTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.classgen.NestedBaseTypesTestCases" };
        TestRunner.main(arguments);
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/schemas/NestedBaseTypes.xsd";
    }

    protected String getSourceFolder() {
        return "./nestedBaseTypes";
    }

    protected String getControlSourceFolder() {
        return "./org/eclipse/persistence/testing/sdo/helper/classgen/nestedBaseTypes";
    }

    /* public void testClassGenCommand() throws Exception {
         StringReader reader = new StringReader(xsdString);
         String[] args = new String[4];
         args[0] = "";
         SDOClassGenerator.main(args);


         compareFiles(getControlFiles(), getGeneratedFiles(classGenerator.getGeneratedBuffers()));
     }*/
    protected List getControlFileNames() {
        ArrayList list = new ArrayList();
        list.add("Root.java");
        list.add("RootImpl.java");
        list.add("Sub1.java");
        list.add("Sub1Impl.java");
        list.add("Sub2.java");
        list.add("Sub2Impl.java");
        list.add("Sub2Sibling.java");
        list.add("Sub2SiblingImpl.java");
        list.add("Sub3.java");
        list.add("Sub3Impl.java");
        list.add("Sub4.java");
        list.add("Sub4Impl.java");
        return list;
    }
}
