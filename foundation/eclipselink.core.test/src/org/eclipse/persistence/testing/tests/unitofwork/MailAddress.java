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
package org.eclipse.persistence.testing.tests.unitofwork;

import java.math.BigDecimal;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;


public class MailAddress implements java.io.Serializable {
    public BigDecimal id;
    public String mailAddress;

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(MailAddress.class);
        descriptor.setTableName("U_ADD");
        descriptor.setPrimaryKeyFieldName("ID");
        descriptor.setSequenceNumberName("SEQ");
        descriptor.setSequenceNumberFieldName("ID");

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("id", "ID");
        descriptor.addDirectMapping("mailAddress", "DESCRIP");

        return descriptor;
    }

    public static MailAddress example1() {
        MailAddress example = new MailAddress();

        example.mailAddress = "401- 1800 Herbert Street";
        return example;
    }

    public static MailAddress example2() {
        MailAddress example = new MailAddress();

        example.mailAddress = "21-1130 Spark Street";
        return example;
    }

    public static MailAddress example3() {
        MailAddress example = new MailAddress();

        example.mailAddress = "three@object.com";
        return example;
    }

    public static MailAddress example4() {
        MailAddress example = new MailAddress();

        example.mailAddress = "601-1129 Meadowlands Dr.";
        return example;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("U_ADD");
        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("DESCRIP", String.class, 50);
        return definition;
    }
}
