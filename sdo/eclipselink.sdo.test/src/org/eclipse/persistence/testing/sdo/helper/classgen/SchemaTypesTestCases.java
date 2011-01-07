/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
