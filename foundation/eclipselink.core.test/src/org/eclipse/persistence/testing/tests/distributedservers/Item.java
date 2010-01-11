/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.distributedservers;

import java.math.BigDecimal;

import org.eclipse.persistence.tools.schemaframework.TableDefinition;


public class Item {
    public BigDecimal id;
    public String description;
    public Company companyOwner;
    public Dist_Employee employeeHolder;

    public static Item example1() {
        Item item = new Item();
        item.description = "Cell Phone 555-5898";
        return item;
    }

    public static Item example2() {
        Item item = new Item();
        item.description = "Cell Phone 555-5898";
        return item;
    }

    public static Item example3() {
        Item item = new Item();
        item.description = "Cell Phone 555-5898";
        return item;
    }

    public static Item example4() {
        Item item = new Item();
        item.description = "Cell Phone 555-5898";
        return item;
    }


    /**
     * Return a platform independant definition of the database table.
     */
    public static

    TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("DIST_ITEM");

        definition.addIdentityField("ID", BigDecimal.class);
        definition.addField("DESCRIPTION", String.class, 255);
        definition.addIdentityField("COMPANYOWNER", BigDecimal.class);
        definition.addIdentityField("EMPLOYEEHOLDER", BigDecimal.class);
        return definition;
    }

}
