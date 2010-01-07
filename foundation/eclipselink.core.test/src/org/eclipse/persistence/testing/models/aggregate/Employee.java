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
package org.eclipse.persistence.testing.models.aggregate;

import java.io.*;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Employee implements Serializable {
    public String firstName;
    public String lastName;

    // changed attribute name from "addressDescription" to "address" to reproduce bug 3566341,
    // which requires the same name for attribute mapped as an aggregate and an attribute
    // on the aggregate mapped 1 to 1.
    public AddressDescription address;
    public ProjectDescription projectDescription;

    public static Employee example1() {
        Employee example = new Employee();

        example.setFirstName("Robert");
        example.setLastName("Martin");
        example.setProjectDescription(ProjectDescription.example1(example));
        example.setAddressDescription(AddressDescription.example4());
        return example;
    }

    public static Employee example2() {
        Employee example = new Employee();

        example.setFirstName("Boby");
        example.setLastName("Simpson");
        example.setProjectDescription(ProjectDescription.example2(example));
        example.setAddressDescription(AddressDescription.example5());
        return example;
    }

    public static Employee example3() {
        Employee example = new Employee();

        example.setFirstName("Anthony");
        example.setLastName("Slater");
        example.setProjectDescription(ProjectDescription.example3(example));
        example.setAddressDescription(AddressDescription.example6());
        return example;
    }

    public AddressDescription getAddressDescription() {
        return address;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public ProjectDescription getProjectDescription() {
        return projectDescription;
    }

    public void setAddressDescription(AddressDescription aDescription) {
        address = aDescription;
    }

    public void setFirstName(String aName) {
        firstName = aName;
    }

    public void setLastName(String aName) {
        lastName = aName;
    }

    public void setProjectDescription(ProjectDescription aDescription) {
        projectDescription = aDescription;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("AGG_EMP");

        definition.addIdentityField("EM_ID", java.math.BigDecimal.class, 15);
        definition.addField("EM_FNAME", String.class, 20);
        definition.addField("EM_LNAME", String.class, 20);
        definition.addField("EM_ADD", java.math.BigDecimal.class, 15);
        definition.addField("EM_SDATE", java.sql.Date.class);
        definition.addField("EM_EDATE", java.sql.Date.class);
        definition.addField("EM_PDESC", String.class, 100);
        definition.addField("COMP_ID", java.math.BigDecimal.class, 15);
        definition.addField("EM_ESDATE", java.sql.Date.class);
        definition.addField("EM_EEDATE", java.sql.Date.class);
        return definition;
    }
}
