/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.tests.junit.failover;

import java.math.BigDecimal;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.ArrayRecord;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.sessions.DatabaseRecord;

public final class Address {

    public BigDecimal id;
    public String street;
    public String city;
    public String province;
    public String postalCode;
    public String country;

    public Address() {
        this.city = "";
        this.province = "";
        this.postalCode = "";
        this.street = "";
        this.country = "";
    }

    static ClassDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Address.class);
        descriptor.addTableName("ADDRESS");
        descriptor.addPrimaryKeyFieldName("ADDRESS.ADDRESS_ID");

        // Descriptor Properties.
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("ADDRESS.ADDRESS_ID");
        descriptor.setSequenceNumberName("ADDRESS_SEQ");
        descriptor.setAlias("Address");

        // Cache Invalidation Policy
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Named Queries.
        // Event Manager.
        // Mappings.
        DirectToFieldMapping cityMapping = new DirectToFieldMapping();
        cityMapping.setAttributeName("city");
        cityMapping.setFieldName("ADDRESS.CITY");
        descriptor.addMapping(cityMapping);

        DirectToFieldMapping countryMapping = new DirectToFieldMapping();
        countryMapping.setAttributeName("country");
        countryMapping.setFieldName("ADDRESS.COUNTRY");
        descriptor.addMapping(countryMapping);

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("ADDRESS.ADDRESS_ID");
        descriptor.addMapping(idMapping);

        DirectToFieldMapping postalCodeMapping = new DirectToFieldMapping();
        postalCodeMapping.setAttributeName("postalCode");
        postalCodeMapping.setFieldName("ADDRESS.P_CODE");
        descriptor.addMapping(postalCodeMapping);

        DirectToFieldMapping provinceMapping = new DirectToFieldMapping();
        provinceMapping.setAttributeName("province");
        provinceMapping.setFieldName("ADDRESS.PROVINCE");
        descriptor.addMapping(provinceMapping);

        DirectToFieldMapping streetMapping = new DirectToFieldMapping();
        streetMapping.setAttributeName("street");
        streetMapping.setFieldName("ADDRESS.STREET");
        descriptor.addMapping(streetMapping);

        return descriptor;
    }

    static String getSQL() {
        return "SELECT ADDRESS_ID, CITY, COUNTRY, P_CODE, PROVINCE, STREET FROM ADDRESS";
    }

    static Vector<DatabaseRecord> getData(ClassDescriptor desc) {
        Vector<DatabaseRecord> rows = new Vector<>();
        Vector<DatabaseField> fields = desc.getAllFields();
        DatabaseField[] fieldsArray = fields.toArray(new DatabaseField[0]);
        rows.add(new ArrayRecord(fields, fieldsArray, new Object[] { Integer.valueOf(51), "Calgary", "Canada", "J5J2B5", "ALB", "1111 Moose Rd." }));
        rows.add(new ArrayRecord(fields, fieldsArray, new Object[] { Integer.valueOf(52), "Metcalfe", "Canada", "Y4F7V6", "ONT", "2 Anderson Rd." }));
        rows.add(new ArrayRecord(fields, fieldsArray, new Object[] { Integer.valueOf(53), "Montreal", "Canada", "Q2S5Z5", "QUE", "1 Habs Place" }));
        return rows;
    }
}
