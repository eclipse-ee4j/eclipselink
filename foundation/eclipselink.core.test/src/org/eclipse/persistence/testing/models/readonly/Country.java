/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.readonly;

import java.util.*;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Country implements java.io.Serializable {
    public Number id;
    public String name;

    public Country() {
        super();
    }

    public static Country canada() {
        Country example = new Country();
        example.setName("Canada");
        return example;
    }

    /**
     * Return a Vector of countries used to populate the db.
     */
    public static Vector countries() {
        Vector countries = new Vector();
        countries.addElement(canada());
        countries.addElement(usa());
        countries.addElement(russia());
        countries.addElement(vietnam());
        countries.addElement(india());
        countries.addElement(czech());
        countries.addElement(uk());

        return countries;
    }

    public static Country czech() {
        Country example = new Country();
        example.setName("Czech Republic");
        return example;
    }

    // Movie descriptor
    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.testing.models.readonly.Country.class);
        descriptor.setTableName("COUNTRY");
        descriptor.addPrimaryKeyFieldName("COUNTRY_ID");
        descriptor.setSequenceNumberName("COUNTRY_SEQ");
        descriptor.setSequenceNumberFieldName("COUNTRY_ID");

        descriptor.addDirectMapping("id", "COUNTRY_ID");
        descriptor.addDirectMapping("name", "NAME");

        return descriptor;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        Country other = (Country)obj;
        return (getName().equals(other.getName()));
    }

    public String getName() {
        return name;
    }

    public static Country india() {
        Country example = new Country();
        example.setName("India");
        return example;
    }

    public static Country russia() {
        Country example = new Country();
        example.setName("Russia");
        return example;
    }

    public void setName(String newValue) {
        this.name = newValue;
    }

    // Country table definition
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("COUNTRY");

        definition.addIdentityField("COUNTRY_ID", java.math.BigDecimal.class, 15);
        definition.addField("NAME", String.class, 50);

        return definition;
    }

    public String toString() {
        return org.eclipse.persistence.internal.helper.Helper.getShortClassName(getClass()) + "(" + getName() + ") ";

    }

    public static Country uk() {
        Country example = new Country();
        example.setName("United Kingdom");
        return example;
    }

    public static Country usa() {
        Country example = new Country();
        example.setName("USA");
        return example;
    }

    public static Country vietnam() {
        Country example = new Country();
        example.setName("Vietnam");
        return example;
    }
}
