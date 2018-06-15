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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.helper.SDOClassGenerator;

public class SchemaTypesTestCases extends SDOClassGenTestCases {

    public SchemaTypesTestCases(String name) {
        super(name);
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/schemas/SchemaTypes.xsd";
    }

    protected String getSourceFolder() {
        return "./schemaTypes";
    }

    protected String getControlSourceFolder() {
        return "./org/eclipse/persistence/testing/sdo/helper/classgen/schematypes";
    }

    protected List getControlFileNames() {
        ArrayList list = new ArrayList();
        list.add("MyTestType.java");
        list.add("MyTestTypeImpl.java");
        list.add("PersonType.java");
        list.add("PersonTypeImpl.java");
        return list;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.classgen.SchemaTypesTestCases" };
        TestRunner.main(arguments);
    }
}
