/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.unitofwork;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class ConcurrentPerson implements java.io.Serializable {
    public String name;
    public ConcurrentAddress address;
    public BigDecimal id;
    public BigDecimal luckyNumber;
    public static boolean isForBackup = false;
    public ConcurrentProject hobby = null;
    public List<ConcurrentPhoneNumber> phoneNumbers = null;

    public static int RUNNING_TEST;
    public static final int NONE = Integer.MIN_VALUE;
    public static int ONE_TO_ONE_INHERITANCE = 1;
    public static int READ_FETCH_JOIN = 1;

    public ConcurrentPerson(){
        phoneNumbers = new ArrayList();
    }

    public BigDecimal calculateLuckyNumber(Record row, Session session) {
        Number code = (Number)row.get("ID");
        return new BigDecimal(code.doubleValue() * 2.435);
    }

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(ConcurrentPerson.class);
        descriptor.setTableName("CONCURRENT_EMP");
        descriptor.setPrimaryKeyFieldName("ID");
        descriptor.setSequenceNumberName("SEQ");
        descriptor.setSequenceNumberFieldName("ID");

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("id", "ID");

        DirectToFieldMapping nameMapping = new DirectToFieldMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setFieldName("NAME");
        nameMapping.setGetMethodName("getName");
        nameMapping.setSetMethodName("setName");
        descriptor.addMapping(nameMapping);

        OneToOneMapping addressMapping = new OneToOneMapping();
        addressMapping.setAttributeName("address");
        addressMapping.setReferenceClass(ConcurrentAddress.class);
        addressMapping.setGetMethodName("getAddress");
        addressMapping.setSetMethodName("setAddress");
        addressMapping.dontUseIndirection();
        addressMapping.addForeignKeyFieldName("CONCURRENT_EMP.ADDR_ID", "CONCURRENT_ADDRESS.ADDRESS_ID");
        descriptor.addMapping(addressMapping);

        TransformationMapping trans = new TransformationMapping();
        trans.setIsReadOnly(true);
        trans.dontUseIndirection();
        trans.setAttributeName("luckyNumber");
        trans.setAttributeTransformation("calculateLuckyNumber");
        descriptor.addMapping(trans);

        OneToOneMapping hobbyMapping = new OneToOneMapping();
        hobbyMapping.setAttributeName("hobby");
        hobbyMapping.setReferenceClass(ConcurrentProject.class);
        hobbyMapping.setGetMethodName("getHobby");
        hobbyMapping.setSetMethodName("setHobby");
        hobbyMapping.dontUseIndirection();
        hobbyMapping.addForeignKeyFieldName("CONCURRENT_EMP.PROJ_ID", "CONCURRENT_PROJECT.ID");
        descriptor.addMapping(hobbyMapping);

        OneToManyMapping phoneNumbersMapping = new OneToManyMapping();
        phoneNumbersMapping.setAttributeName("phoneNumbers");
        phoneNumbersMapping.setReferenceClass(ConcurrentPhoneNumber.class);
        phoneNumbersMapping.dontUseIndirection();
        phoneNumbersMapping.addTargetForeignKeyFieldName("CONCURRENT_PHONE.EMP_ID", "CONCURRENT_EMP.ID");
        descriptor.addMapping(phoneNumbersMapping);

        return descriptor;
    }

    public static ConcurrentPerson example() {
        ConcurrentPerson example = new ConcurrentPerson();
        example.name = "Andrew";
        example.address = new ConcurrentAddress();
        example.address.city = "QuayTown";
        example.address.country = "North Quary";
        example.address.postalCode = "Z1Z 9A9";
        example.address.province = "NewProvince";
        example.address.street = "OldStreet";

        return example;
    }

    public static ConcurrentPerson example1() {
        ConcurrentPerson example = new ConcurrentPerson();
        example.name = "Dave";
        example.address = new ConcurrentAddress();
        example.address.city = "Bay City";
        example.address.country = "North Country";
        example.address.postalCode = "H0H0H0";
        example.address.province = "Manitoba";
        example.address.street = "Start";

        return example;
    }

    public ConcurrentAddress getAddress() {
        if (ConcurrentAddress.RUNNING_TEST == ConcurrentAddress.LOCK_ON_CLONE_DEADLOCK) {
            if (!isForBackup) {
                synchronized (address) {
                    try {
                        isForBackup = true; //the next call to this methd will be for backup
                        address.wait(10000); // let the refresh start refreshing
                    } catch (InterruptedException ex) {
                    }
                }
            } else {
                isForBackup = false;
            }
        }
        return address;
    }

    public void setAddress(ConcurrentAddress address) {
        this.address = address;
    }

    public ConcurrentProject getHobby() {
        return hobby;
    }

    public void setHobby(ConcurrentProject hobby) {
        if (ConcurrentPerson.RUNNING_TEST == ConcurrentPerson.ONE_TO_ONE_INHERITANCE) {
            if (!isForBackup) {
                try{
                    Thread.sleep(10000); // let the read go
                } catch (InterruptedException ex) {}
                isForBackup = true; //the next call to this methd will be for backup
            } else {
                isForBackup = false;
            }
        }
        this.hobby = hobby;
    }

    public List<ConcurrentPhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<ConcurrentPhoneNumber> phoneNumbers) {
        if (ConcurrentPerson.RUNNING_TEST == ConcurrentPerson.READ_FETCH_JOIN) {
            if (!isForBackup) {
                try{
                    Thread.sleep(10000); // let the read go
                } catch (InterruptedException ex) {}
                isForBackup = true; //the next call to this methd will be for backup
            } else {
                isForBackup = false;
            }
        }
        this.phoneNumbers = phoneNumbers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return a platform independent definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("CONCURRENT_EMP");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("NAME", String.class, 20);
        definition.addField("ADDR_ID", java.math.BigDecimal.class, 15);
        definition.addField("PROJ_ID", java.math.BigDecimal.class, 15);

        return definition;
    }
}
