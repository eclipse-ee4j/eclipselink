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
package org.eclipse.persistence.testing.models.mapping;

import java.util.Vector;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.identitymaps.FullIdentityMap;
import org.eclipse.persistence.mappings.DirectToFieldMapping;

public class LegacyTestProject extends org.eclipse.persistence.sessions.Project {
    public LegacyTestProject() {
        setName("LegacyTestCase");
        buildEmployee1Descriptor();
    }

    protected void buildEmployee1Descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(Employee1.class);
        Vector vector = new Vector();
        vector.addElement("MUL_EMP");
        vector.addElement("MUL_ADDR");
        vector.addElement("MUL_CTRY");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("MUL_EMP.EMP_ID");
        
        descriptor.addForeignKeyFieldNameForMultipleTable("MUL_EMP.ADDR_ID", "MUL_ADDR.ADDR_ID");
        descriptor.addForeignKeyFieldNameForMultipleTable("MUL_ADDR.CNTRY_ID", "MUL_CTRY.CNTRY_ID");

        // SECTION: PROPERTIES
        descriptor.setIdentityMapClass(FullIdentityMap.class);
        descriptor.setExistenceChecking("Check cache");
        descriptor.setIdentityMapSize(100);

        // SECTION: COPY POLICY
        descriptor.createCopyPolicy("constructor");

        // SECTION: INSTANTIATION POLICY
        descriptor.createInstantiationPolicy("constructor");

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping = new DirectToFieldMapping();
        directtofieldmapping.setAttributeName("addressId_A");
        directtofieldmapping.setIsReadOnly(false);
        directtofieldmapping.setGetMethodName("getAddressId_A");
        directtofieldmapping.setSetMethodName("setAddressId_A");
        directtofieldmapping.setFieldName("MUL_ADDR.ADDR_ID");
        descriptor.addMapping(directtofieldmapping);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping1 = new DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("addressId_E");
        directtofieldmapping1.setIsReadOnly(false);
        directtofieldmapping1.setGetMethodName("getAddressId_E");
        directtofieldmapping1.setSetMethodName("setAddressId_E");
        directtofieldmapping1.setFieldName("MUL_EMP.ADDR_ID");
        descriptor.addMapping(directtofieldmapping1);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping2 = new DirectToFieldMapping();
        directtofieldmapping2.setAttributeName("city");
        directtofieldmapping2.setIsReadOnly(false);
        directtofieldmapping2.setGetMethodName("getCity");
        directtofieldmapping2.setSetMethodName("setCity");
        directtofieldmapping2.setFieldName("MUL_ADDR.CITY");
        descriptor.addMapping(directtofieldmapping2);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping3 = new DirectToFieldMapping();
        directtofieldmapping3.setAttributeName("country");
        directtofieldmapping3.setIsReadOnly(false);
        directtofieldmapping3.setGetMethodName("getCountry");
        directtofieldmapping3.setSetMethodName("setCountry");
        directtofieldmapping3.setFieldName("MUL_CTRY.COUNTRY");
        descriptor.addMapping(directtofieldmapping3);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping4 = new DirectToFieldMapping();
        directtofieldmapping4.setAttributeName("firstName");
        directtofieldmapping4.setIsReadOnly(false);
        directtofieldmapping4.setGetMethodName("getFirstName");
        directtofieldmapping4.setSetMethodName("setFirstName");
        directtofieldmapping4.setFieldName("MUL_EMP.FIRST_NAME");
        descriptor.addMapping(directtofieldmapping4);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping5 = new DirectToFieldMapping();
        directtofieldmapping5.setAttributeName("id");
        directtofieldmapping5.setIsReadOnly(false);
        directtofieldmapping5.setGetMethodName("getId");
        directtofieldmapping5.setSetMethodName("setId");
        directtofieldmapping5.setFieldName("MUL_EMP.EMP_ID");
        descriptor.addMapping(directtofieldmapping5);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping6 = new DirectToFieldMapping();
        directtofieldmapping6.setAttributeName("lastName");
        directtofieldmapping6.setIsReadOnly(false);
        directtofieldmapping6.setGetMethodName("getLastName");
        directtofieldmapping6.setSetMethodName("setLastName");
        directtofieldmapping6.setFieldName("MUL_EMP.LAST_NAME");
        descriptor.addMapping(directtofieldmapping6);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping7 = new DirectToFieldMapping();
        directtofieldmapping7.setAttributeName("province");
        directtofieldmapping7.setIsReadOnly(false);
        directtofieldmapping7.setGetMethodName("getProvince");
        directtofieldmapping7.setSetMethodName("setProvince");
        directtofieldmapping7.setFieldName("MUL_ADDR.PROVINCE");
        descriptor.addMapping(directtofieldmapping7);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping8 = new DirectToFieldMapping();
        directtofieldmapping8.setAttributeName("sex");
        directtofieldmapping8.setIsReadOnly(false);
        directtofieldmapping8.setGetMethodName("getSex");
        directtofieldmapping8.setSetMethodName("setSex");
        directtofieldmapping8.setFieldName("MUL_EMP.SEX");
        descriptor.addMapping(directtofieldmapping8);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping9 = new DirectToFieldMapping();
        directtofieldmapping9.setAttributeName("countryId_A");
        directtofieldmapping9.setIsReadOnly(false);
        directtofieldmapping9.setGetMethodName("getCountryId_A");
        directtofieldmapping9.setSetMethodName("setCountryId_A");
        directtofieldmapping9.setFieldName("MUL_ADDR.CNTRY_ID");
        descriptor.addMapping(directtofieldmapping9);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping10 = new DirectToFieldMapping();
        directtofieldmapping10.setAttributeName("countryId_C");
        directtofieldmapping10.setIsReadOnly(false);
        directtofieldmapping10.setGetMethodName("getCountryId_C");
        directtofieldmapping10.setSetMethodName("setCountryId_C");
        directtofieldmapping10.setFieldName("MUL_CTRY.CNTRY_ID");
        descriptor.addMapping(directtofieldmapping10);

        descriptor.addForeignKeyFieldNameForMultipleTable("MUL_EMP.ADDR_ID", "MUL_ADDR.ADDR_ID");
        descriptor.addForeignKeyFieldNameForMultipleTable("MUL_ADDR.CNTRY_ID", "MUL_CTRY.CNTRY_ID");

        addDescriptor(descriptor);
    }
}
