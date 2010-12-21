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

import java.util.Vector;

import org.eclipse.persistence.tools.schemaframework.TableDefinition;


public class Company {
    public BigDecimal id;
    public String name;
    public Vector ownedItems;

    public static Company example1() {
        Company company = new Company();
        company.name = "Hoser & Hoser International";
        return company;
    }

    public static Company example2() {
        Company company = new Company();
        company.name = "Hudson Bay Company";
        return company;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static

    TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("DIST_COMPANY");

        definition.addIdentityField("ID", BigDecimal.class);
        definition.addField("NAME", String.class, 255);
        return definition;
    }

}
