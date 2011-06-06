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
package org.eclipse.persistence.testing.tests.stress;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.tools.schemaframework.*;

/**
 * Simple object used for stressing.
 */
public class Address {
    public String street;
    public String city;
    public String province;
    public String postalCode;
    public String country;
    public long id;

    public Address() {
        city = "";
        province = "";
        postalCode = "";
        street = "";
        country = "";
    }

    /**
     * Create and return a Descriptor which describes the mapping between
     * the Java class Address and the relational table ADDRESS.
     */
    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor;

        descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(org.eclipse.persistence.testing.tests.stress.Address.class);
        descriptor.setTableName("STRESS_ADDRESS");
        descriptor.setPrimaryKeyFieldName("ADDRESS_ID");
        descriptor.setSequenceNumberName("STRESS_SEQ");
        descriptor.setSequenceNumberFieldName("ADDRESS_ID");
        descriptor.useVersionLocking("VERSION");

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("id", "ADDRESS_ID");
        descriptor.addDirectMapping("city", "CITY");
        descriptor.addDirectMapping("country", "COUNTRY");
        descriptor.addDirectMapping("postalCode", "P_CODE");
        descriptor.addDirectMapping("province", "PROVINCE");
        descriptor.addDirectMapping("street", "STREET");
        return descriptor;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    /**
     * Return the persistent identifier of the receiver.
     */
    public long getId() {
        return id;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getProvince() {
        return province;
    }

    public String getStreet() {
        return street;
    }

    public void setCity(String theCity) {
        city = theCity;
    }

    public void setCountry(String theCountry) {
        country = theCountry;
    }

    /**
     * Set the persistent identifier of the receiver.
     */
    public void setId(long theId) {
        id = theId;
    }

    public void setPostalCode(String thePostalCode) {
        postalCode = thePostalCode;
    }

    public void setProvince(String theProvince) {
        province = theProvince;
    }

    public void setStreet(String theStreet) {
        street = theStreet;
    }

    /**
    *    Return a platform independant definition of the database table.
    */
    public static TableDefinition tableDefinition() {
        TableDefinition definition;

        definition = new TableDefinition();

        definition.setName("STRESS_ADDRESS");
        definition.addIdentityField("ADDRESS_ID", Long.class);
        definition.addField("VERSION", Long.class);
        definition.addField("P_CODE", String.class, 20);
        definition.addField("CITY", String.class, 80);
        definition.addField("PROVINCE", String.class, 80);
        definition.addField("STREET", String.class, 80);
        definition.addField("COUNTRY", String.class, 80);
        return definition;
    }

    /**
     * Print the address city and province.
     */
    public String toString() {
        java.io.StringWriter writer = new java.io.StringWriter();

        writer.write("Address: ");
        writer.write(getStreet());
        writer.write(", ");
        writer.write(getCity());
        writer.write(", ");
        writer.write(getProvince());
        writer.write(", ");
        writer.write(getCountry());
        return writer.toString();
    }
}
