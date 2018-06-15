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
package org.eclipse.persistence.testing.models.interfaces;

import org.eclipse.persistence.tools.schemaframework.*;

public class ProductDeveloper extends DevelopmentJob {
    public String product;

    public static ProductDeveloper example1() {
        ProductDeveloper example = new ProductDeveloper();

        example.setProduct("Quake 2 Maps");
        example.setSalary(new Float(145000.00));

        return example;
    }

    public static ProductDeveloper example2() {
        ProductDeveloper example = new ProductDeveloper();

        example.setProduct("Trinity Operating System");
        example.setSalary(new Float(85000.00));

        return example;
    }

    public static ProductDeveloper example3() {
        ProductDeveloper example = new ProductDeveloper();

        example.setProduct("Ada For Dummies");
        example.setSalary(new Float(80000.00));

        return example;
    }

    public String getProduct() {
        return product;
    }

    public static TableDefinition productDeveloperTable() {
        TableDefinition table = new TableDefinition();

        table.setName("PRD_DEV");
        table.addField("CODE", java.math.BigDecimal.class, 15);
        table.addField("PRODUCT", String.class, 30);

        return table;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String toString() {
        return new String("Product Developer: " + getJobCode());
    }
}
