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
package org.eclipse.persistence.testing.tests.types;

import java.util.*;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.exceptions.*;

/**
 *  Tests both boolean and Boolean access to the database.
 */
public class BooleanTester extends TypeTester {
    public boolean booleanValue;
    public Boolean booleanClassValue;

    public BooleanTester() {
        this(true);
    }

    public BooleanTester(boolean testValue) {
        super(new Boolean(testValue).toString());
        booleanValue = testValue;
        booleanClassValue = new Boolean(testValue);
    }

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(BooleanTester.class);
        descriptor.setTableName("BOOLEANS");
        descriptor.setPrimaryKeyFieldName("NAME");

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("testName", "getTestName", "setTestName", "NAME");
        descriptor.addDirectMapping("booleanValue", "BOOLEANV");
        descriptor.addDirectMapping("booleanClassValue", "BOOLEANC");
        return descriptor;
    }

    public static RelationalDescriptor descriptorWithAccessors() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(BooleanTester.class);
        descriptor.setTableName("BOOLEANS");
        descriptor.setPrimaryKeyFieldName("NAME");

        /* Next define the attribute mappings. */
        try {
            descriptor.addDirectMapping("testName", "getTestName", "setTestName", "NAME");
            descriptor.addDirectMapping("booleanValue", "getBooleanValue", "setBooleanValue", "BOOLEANV");
            descriptor.addDirectMapping("booleanClassValue", "getBooleanClassValue", "setBooleanClassValue", "BOOLEANC");
        } catch (DescriptorException exception) {
        }
        return descriptor;
    }

    public Boolean getBooleanClassValue() {
        return booleanClassValue;
    }

    public boolean getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanClassValue(Boolean boolValue) {
        booleanClassValue = boolValue;
    }

    public void setBooleanClassValue(boolean boolValue) {
        booleanClassValue = new Boolean(boolValue);
    }

    public void setBooleanValue(Boolean boolValue) {
        booleanValue = boolValue.booleanValue();
    }

    public void setBooleanValue(boolean boolValue) {
        booleanValue = boolValue;
    }

    /**
     *Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition(Session session) {
        TableDefinition definition = TypeTester.tableDefinition();
        FieldDefinition fieldDef;

        definition.setName("BOOLEANS");
        fieldDef = new FieldDefinition("BOOLEANV", Boolean.class);
        fieldDef.setShouldAllowNull(false);
        definition.addField(fieldDef);

        fieldDef = new FieldDefinition("BOOLEANC", Boolean.class);
        fieldDef.setShouldAllowNull(false);
        definition.addField(fieldDef);

        return definition;
    }

    public static Vector testInstances() {
        Vector tests = new Vector(2);

        tests.addElement(new BooleanTester(true));
        tests.addElement(new BooleanTester(false));
        return tests;
    }

    public String toString() {
        return "BooleanTester(" + getBooleanValue() + ")";
    }
}
