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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;

public class ClassGenWithImportsTestCases extends SDOClassGenTestCases {

    public ClassGenWithImportsTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.classgen.ClassGenWithImportsTestCases" };
        TestRunner.main(arguments);
    }

    public void setUp() {
        super.setUp();
        try {
            // File rootDir = new File("./org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/");
            //URL rootURL = rootDir.toURL();
            //TODO: do we need to define types before generating classes???
            String schemaLocation = FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/";
            xsdHelper.define(new StringReader(xsdString), schemaLocation);
        } catch (Exception e) {
            e.printStackTrace();
            fail("failed during setup");

        }
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/ImportsWithNamespaces.xsd";
    }

    protected String getSourceFolder() {
        return "./srcImports";
    }

    /**
     * Override the default package dir.
     * There are two packages [my] and [uri2.my]
     */
    // Override package generation based on the JAXB 2.0 algorithm in SDOUtil.java
    protected List<String> getPackages() {
        List<String> packages = new ArrayList<String>();
        packages.add("uri/my");
        packages.add("uri/my");
        packages.add("uri2/my");
        packages.add("uri2/my");
        return packages;
    }

    public void testClassGen() throws Exception {
        StringReader reader = new StringReader(xsdString);
        org.eclipse.persistence.sdo.helper.DefaultSchemaResolver schemaResolver = new org.eclipse.persistence.sdo.helper.DefaultSchemaResolver();

        String schemaLocation = FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/";
        schemaResolver.setBaseSchemaLocation(schemaLocation);
        classGenerator.generate(reader, getSourceFolder(), schemaResolver);

        int numGenerated = classGenerator.getGeneratedBuffers().size();
        assertEquals(2, numGenerated);
        compareFiles(getControlFiles(), getGeneratedFiles(classGenerator.getGeneratedBuffers()));
    }

    protected String getControlSourceFolder() {
        return "./org/eclipse/persistence/testing/sdo/helper/classgen/srcImports";
    }

    protected List<String> getControlFileNames() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("PurchaseOrder.java");
        list.add("PurchaseOrderImpl.java");
        list.add("USAddress.java");
        list.add("USAddressImpl.java");
        return list;
    }
}
