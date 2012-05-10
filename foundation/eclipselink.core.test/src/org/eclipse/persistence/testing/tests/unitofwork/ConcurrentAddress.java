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
package org.eclipse.persistence.testing.tests.unitofwork;

import java.io.Serializable;
import java.io.StringWriter;

import java.math.BigDecimal;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.testing.tests.unitofwork.ConcurrentReadOnUpdateWithEarlyTransTest;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;


/**
 * <p><b>Purpose</b>: Represents the mailing address on an Employee
 * <p><b>Description</b>: Held in a private 1:1 relationship from Employee
 * @see Employee
 */
public class ConcurrentAddress implements Serializable, Cloneable {
    public BigDecimal id;
    public String street;
    public String city;
    public String province;
    public String postalCode;
    public String country;

    //USED TO DETERMINE WHICH TEST IS RUNNING
    public static int RUNNING_TEST;

    //tests
    public static final int NONE = Integer.MIN_VALUE;
    public static final int LOCK_ON_CLONE_TEST = 55;
    public static final int LOCK_ON_CLONE_DEADLOCK = 56;
    public static final int READ_ON_UPDATE_EARLY_TRANS = 57;
    public static boolean isForBackup = false;

    public ConcurrentAddress() {
        this.city = "";
        this.province = "";
        this.postalCode = "";
        this.street = "";
        this.country = "";
    }

    public Object clone() {
        ConcurrentAddress result = new ConcurrentAddress();
        result.id = this.id;
        result.city = this.city;
        result.province = this.province;
        result.postalCode = this.postalCode;
        result.street = this.street;
        result.country = this.country;
        return result;

    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        try {
            if (ConcurrentRefreshOnCloneTest.lock != null) {
                synchronized (ConcurrentRefreshOnCloneTest.lock) {
                    if (!ConcurrentRefreshOnCloneTest.waited) {
                        if (ConcurrentRefreshOnCloneTest.readerWaiting) {
                            ConcurrentRefreshOnCloneTest.lock.notifyAll();
                            Thread.yield();
                        }

                        // verify that we are not currently holding an active lock on the system.  Which would result in a deadlock
                        if (((AbstractSession)ConcurrentRefreshOnCloneTest.session).getIdentityMapAccessorInstance().getIdentityMapManager().getCacheMutex().getActiveThread() != 
                            Thread.currentThread()) {
                            ConcurrentRefreshOnCloneTest.cloneWaiting = true;
                            ConcurrentRefreshOnCloneTest.lock.wait(30000);
                            ConcurrentRefreshOnCloneTest.cloneWaiting = false;
                        } else {
                            //can't lock but let's sleep to simulate a window for refresh
                            Thread.sleep(4000);
                        }
                        ConcurrentRefreshOnCloneTest.waited = true;
                    }
                }
            }
        } catch (Exception ex) {
        }
        return country;
    }

    /**
     * Return the persistent identifier of the receiver.
     */
    public BigDecimal getId() {
        return id;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getProvince() {
        //used in the Lock on clone test
        if ((ConcurrentAddress.RUNNING_TEST == ConcurrentAddress.LOCK_ON_CLONE_TEST) || 
            (ConcurrentAddress.RUNNING_TEST == ConcurrentAddress.LOCK_ON_CLONE_DEADLOCK)) {
            if (!isForBackup) {
                synchronized (this) {
                    isForBackup = true; //the next call to this method will be for backup
                    this.notifyAll(); // let refresh update the fields
                    try {
                        this.wait(10000); //wait for refresh to update the fields
                    } catch (InterruptedException ex) {
                    }
                }
            } else {
                isForBackup = false;
            }
        }
        return province;
    }

    public String getStreet() {
        return street;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Set the persistent identifier of the receiver.
     */
    public void setId(BigDecimal id) {
        try {
            if (ConcurrentAddress.RUNNING_TEST == ConcurrentAddress.READ_ON_UPDATE_EARLY_TRANS) {
                if (ConcurrentReadOnUpdateWithEarlyTransTest.threadId.get().equals("Writer")) {
                    synchronized (ConcurrentReadOnUpdateWithEarlyTransTest.readAddress) {
                        ConcurrentReadOnUpdateWithEarlyTransTest.readAddress.notify(); // wake up reader.
                        ConcurrentReadOnUpdateWithEarlyTransTest.whosWaiting = 
                                ConcurrentReadOnUpdateWithEarlyTransTest.WRITER;
                        ConcurrentReadOnUpdateWithEarlyTransTest.readAddress.wait(30000);
                    }
                }
            } else {
                if (ConcurrentReadOnInsertTest.readAddress != null) {
                    synchronized (ConcurrentReadOnInsertTest.readAddress) {
                        if (ConcurrentReadOnInsertTest.depth <= 0) {
                            ConcurrentReadOnInsertTest.readAddress.notifyAll();
                        } else {
                            --ConcurrentReadOnInsertTest.depth;
                        }
                    }

                    // make an extra buffer to ensure that the read has time to run
                    Thread.sleep(2000);
                }
            }
        } catch (Exception ex) {
        }
        this.id = id;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Print the address city and province.
     */
    public String toString() {
        StringWriter writer = new StringWriter();

        writer.write("Address: ");
        writer.write(this.street);
        writer.write(", ");
        writer.write(this.city);
        writer.write(", ");
        writer.write(this.province);
        writer.write(", ");
        writer.write(this.country);
        return writer.toString();
    }

    public static TableDefinition tableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition tabledefinition = 
            new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("CONCURRENT_ADDRESS");

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field = 
            new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field.setName("ADDRESS_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        tabledefinition.addField(field);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field1 = 
            new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field1.setName("P_CODE");
        field1.setTypeName("VARCHAR");
        field1.setSize(20);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        tabledefinition.addField(field1);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field2 = 
            new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field2.setName("CITY");
        field2.setTypeName("VARCHAR");
        field2.setSize(80);
        field2.setShouldAllowNull(true);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        tabledefinition.addField(field2);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field3 = 
            new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field3.setName("PROVINCE");
        field3.setTypeName("VARCHAR");
        field3.setSize(80);
        field3.setShouldAllowNull(true);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        tabledefinition.addField(field3);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field4 = 
            new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field4.setName("STREET");
        field4.setTypeName("VARCHAR");
        field4.setSize(80);
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        tabledefinition.addField(field4);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field5 = 
            new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field5.setName("COUNTRY");
        field5.setTypeName("VARCHAR");
        field5.setSize(80);
        field5.setShouldAllowNull(true);
        field5.setIsPrimaryKey(false);
        field5.setUnique(false);
        field5.setIsIdentity(false);
        tabledefinition.addField(field5);

        return tabledefinition;
    }

    public static RelationalDescriptor descriptor() {
        // NOTE: weights are set on the mapping to ensure the order in which they are
        // accessed by toplink for timing purposes for test ConcurrentRefreshOnCloneTest
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(ConcurrentAddress.class);
        descriptor.addTableName("CONCURRENT_ADDRESS");
        descriptor.addPrimaryKeyFieldName("CONCURRENT_ADDRESS.ADDRESS_ID");

        // RelationalDescriptor properties.
        descriptor.useFullIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("ADDRESS_ID");
        descriptor.setSequenceNumberName("ADDRESS_SEQ");

        // Query manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Event manager.
        // Mappings.
        DirectToFieldMapping cityMapping = new DirectToFieldMapping();
        cityMapping.setAttributeName("city");
        cityMapping.setFieldName("CONCURRENT_ADDRESS.CITY");
        cityMapping.setWeight(new Integer(10));
        descriptor.addMapping(cityMapping);

        DirectToFieldMapping countryMapping = new DirectToFieldMapping();
        countryMapping.setAttributeName("country");
        countryMapping.setFieldName("CONCURRENT_ADDRESS.COUNTRY");
        countryMapping.setSetMethodName("setCountry");
        countryMapping.setGetMethodName("getCountry");
        countryMapping.setWeight(new Integer(9));
        descriptor.addMapping(countryMapping);

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("CONCURRENT_ADDRESS.ADDRESS_ID");
        idMapping.setSetMethodName("setId");
        idMapping.setGetMethodName("getId");
        idMapping.setWeight(new Integer(8));
        descriptor.addMapping(idMapping);

        DirectToFieldMapping postalCodeMapping = new DirectToFieldMapping();
        postalCodeMapping.setAttributeName("postalCode");
        postalCodeMapping.setFieldName("CONCURRENT_ADDRESS.P_CODE");
        postalCodeMapping.setWeight(new Integer(7));
        descriptor.addMapping(postalCodeMapping);

        DirectToFieldMapping provinceMapping = new DirectToFieldMapping();
        provinceMapping.setAttributeName("province");
        provinceMapping.setFieldName("CONCURRENT_ADDRESS.PROVINCE");
        provinceMapping.setSetMethodName("setProvince");
        provinceMapping.setGetMethodName("getProvince");
        provinceMapping.setWeight(new Integer(6));
        descriptor.addMapping(provinceMapping);

        DirectToFieldMapping streetMapping = new DirectToFieldMapping();
        streetMapping.setAttributeName("street");
        streetMapping.setFieldName("CONCURRENT_ADDRESS.STREET");
        streetMapping.setWeight(new Integer(5));
        descriptor.addMapping(streetMapping);

        return descriptor;
    }
}
