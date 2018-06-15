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
package org.eclipse.persistence.testing.models.aggregate;

import java.io.*;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Address implements Serializable {
    public Number id;
    public String address;

    public static Address example1() {
        Address example = new Address();

        example.setAddress("1-1129 Meadowlands, Ottawa");
        ;
        return example;
    }

    public static Address example2() {
        Address example = new Address();

        example.setAddress("2-1120 Meadowlands, Ottawa");
        ;
        return example;
    }

    public static Address example3() {
        Address example = new Address();

        example.setAddress("3-1130 Meadowlands, Ottawa");
        ;
        return example;
    }

    public static Address example4() {
        Address example = new Address();

        example.setAddress("4-1130 Meadowlands, Ottawa");
        ;
        return example;
    }

    public static Address example5() {
        Address example = new Address();

        example.setAddress("5-1130 Meadowlands, Ottawa");
        ;
        return example;
    }

    public static Address example6() {
        Address example = new Address();

        example.setAddress("6-1130 Meadowlands, Ottawa");
        ;
        return example;
    }

    public static Address example7() {
        Address example = new Address();

        example.setAddress("Address Changed");
        ;
        return example;
    }

    public void setAddress(String anAddress) {
        address = anAddress;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("AGG_ADD");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("ADDRESS", String.class, 30);

        return definition;
    }
}
