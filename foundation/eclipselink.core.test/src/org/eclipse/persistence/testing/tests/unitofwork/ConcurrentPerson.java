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
package org.eclipse.persistence.testing.tests.unitofwork;

import java.math.BigDecimal;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.mappings.OneToOneMapping;
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
        descriptor.addDirectMapping("name", "NAME");

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

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("CONCURRENT_EMP");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("NAME", String.class, 20);
        definition.addField("ADDR_ID", java.math.BigDecimal.class, 15);

        return definition;
    }
}
