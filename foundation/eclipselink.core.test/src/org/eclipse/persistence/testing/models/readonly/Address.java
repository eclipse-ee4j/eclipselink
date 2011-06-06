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
package org.eclipse.persistence.testing.models.readonly;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Address implements java.io.Serializable {
    public Number id;
    public String streetAddress;
    public String city;
    public String zipCode;
    public Country country;

    // Address descriptor
    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.testing.models.readonly.Address.class);
        descriptor.setTableName("RO_ADDR");
        descriptor.addPrimaryKeyFieldName("ADD_ID");
        descriptor.setSequenceNumberName("ADD_SEQ");
        descriptor.setSequenceNumberFieldName("ADD_ID");

        descriptor.addDirectMapping("id", "ADD_ID");
        descriptor.addDirectMapping("streetAddress", "STREET");
        descriptor.addDirectMapping("city", "CITY");
        descriptor.addDirectMapping("zipCode", "ZIP");

        OneToOneMapping countryMapping = new OneToOneMapping();
        countryMapping.setAttributeName("country");
        countryMapping.setReferenceClass(Country.class);
        countryMapping.addForeignKeyFieldName("COUNTRY_ID", "COUNTRY_ID");
        countryMapping.dontUseIndirection();
        descriptor.addMapping(countryMapping);

        return descriptor;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        Address other = (Address)obj;
        return (getStreet().equals(other.getStreet()) && getCity().equals(other.getCity()) && getZipCode().equals(other.getZipCode()) && getCountry().equals(other.getCountry()));
    }

    public static Address example1() {
        Address example = new Address();

        example.setStreetAddress("14431 Skywalkwer Ave.");
        example.setCity("San Rafael, California");
        example.setZipCode("92313");
        example.setCountry(Country.canada());

        return example;
    }

    public static Address example2() {
        Address example = new Address();

        example.setStreetAddress("1431 Hollywood Blvd.");
        example.setCity("Hollywood, California");
        example.setZipCode("90212");
        example.setCountry(Country.czech());

        return example;
    }

    public static Address example3() {
        Address example = new Address();

        example.setStreetAddress("1976 Rodeo Dr.");
        example.setCity("Los Angeles, California");
        example.setZipCode("90211");
        example.setCountry(Country.india());

        return example;
    }

    public static Address example4() {
        Address example = new Address();

        example.setStreetAddress("5 Birch St");
        example.setCity("Kitchener, Ontario");
        example.setZipCode("M8K 9D4");
        example.setCountry(Country.russia());

        return example;
    }

    public static Address example5() {
        Address example = new Address();
        example.setStreetAddress("Any Street");
        example.setCity("Los Angeles, CA");
        example.setZipCode("90144");
        example.setCountry(Country.uk());

        return example;
    }

    public String getCity() {
        return city;
    }

    /**
* This method was created in VisualAge.
* @return org.eclipse.persistence.testing.ReadOnlyModel.Country
*/
    public Country getCountry() {
        return country;
    }

    public String getStreet() {
        return streetAddress;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setCity(String city) {
        this.city = city;
    }

    /**
* This method was created in VisualAge.
* @param newValue org.eclipse.persistence.testing.ReadOnlyModel.Country
*/
    public void setCountry(Country newValue) {
        this.country = newValue;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    // Address table definition
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("RO_ADDR");

        definition.addIdentityField("ADD_ID", java.math.BigDecimal.class, 15);
        definition.addField("STREET", String.class, 30);
        definition.addField("CITY", String.class, 30);
        definition.addField("ZIP", String.class, 10);
        definition.addField("COUNTRY_ID", java.math.BigDecimal.class, 15);

        return definition;
    }

    public String toString() {
        java.io.StringWriter stringWriter = new java.io.StringWriter();
        java.io.PrintWriter writer = new java.io.PrintWriter(stringWriter);
        writer.print(org.eclipse.persistence.internal.helper.Helper.getShortClassName(getClass()));
        writer.print("(");
        if (streetAddress == null) {
            writer.print("null");
        } else {
            writer.print(streetAddress);
        }
        writer.print(",");
        if (city == null) {
            writer.print("null");
        } else {
            writer.print(city);
        }
        writer.print(",");
        if (country == null) {
            writer.print("null");
        } else {
            writer.print(country);
        }
        writer.print(",");
        if (zipCode == null) {
            writer.print("null");
        } else {
            writer.print(zipCode);
        }
        writer.write(")");
        return stringWriter.toString();
    }
}
