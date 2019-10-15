/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.tests.tableswithspacesmodel;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.mappings.DirectToFieldMapping;

import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Test that a database table with spaces is properly delimited
 * EL Bug 382420
 */
public class QuotedTableNameInitializationTest extends TestCase {

    public QuotedTableNameInitializationTest() {
        super();
        setDescription("Test that a database table with spaces is properly delimited");
    }

    public void test() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Employee.class);
        String tableName = "SPACED EMPLOYEE TABLE";
        DatabaseTable table = new DatabaseTable();
        table.setName(tableName);
        table.setUseDelimiters(true);
        descriptor.addTable(table);
        descriptor.addPrimaryKeyFieldName("EMP_ID");

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("EMP_ID");
        descriptor.addMapping(idMapping);

        descriptor.preInitialize(getAbstractSession());

        DatasourcePlatform plaf = (DatasourcePlatform)getAbstractSession().getDatasourcePlatform();

        String expectedTableName = plaf.getStartDelimiter() + tableName + plaf.getEndDelimiter();
        String newTableName = table.getNameDelimited(plaf);

        assertEquals("Table name should be between the platform delimiters", expectedTableName, newTableName);
    }

}
