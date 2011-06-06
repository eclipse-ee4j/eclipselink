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
package org.eclipse.persistence.testing.models.aggregate;

import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Company {
    private java.lang.String name;
    private int companyId;

    public Company() {
    }

    public static Company example1() {
        Company example1 = new Company();
        example1.setName("The Object People");
        return example1;
    }

    public static Company example2() {
        Company example2 = new Company();

        example2.setName("Oracle Inc.");

        return example2;
    }

    public static Company example3() {
        Company example3 = new Company();

        example3.setName("BEA System");

        return example3;
    }

    public static Company example4() {
        Company example4 = new Company();
        example4.setName("Microsoft");
        return example4;
    }

    public static Company example5() {
        Company example5 = new Company();
        example5.setName("IBM");
        return example5;
    }

    /**
     * Insert the method's description here.
     * Creation date: (04/28/2000 1:19:07 PM)
     * @return int
     */
    public int getCompanyId() {
        return companyId;
    }

    /**
     * Insert the method's description here.
     * Creation date: (04/11/2000 11:43:54 AM)
     * @return java.lang.String
     */
    public java.lang.String getName() {
        return name;
    }

    /**
     * Insert the method's description here.
     * Creation date: (04/28/2000 1:19:07 PM)
     * @param newCompanyId int
     */
    public void setCompanyId(int newCompanyId) {
        companyId = newCompanyId;
    }

    /**
     * Insert the method's description here.
     * Creation date: (04/11/2000 11:43:54 AM)
     * @param newName java.lang.String
     */
    public void setName(java.lang.String newName) {
        name = newName;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("COMPANY_AGG");

        definition.addIdentityField("COMPANY_ID", java.math.BigDecimal.class, 15);
        definition.addField("NAME", String.class, 30);
        return definition;
    }
}
