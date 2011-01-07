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

public class MultipleTableTestProject extends org.eclipse.persistence.sessions.Project {

    public MultipleTableTestProject() {
        setName("MultipleTableTestCase");
        buildEmployee2Descriptor();
    }

    protected void buildEmployee2Descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(org.eclipse.persistence.testing.models.mapping.Employee2.class);
        Vector vector = new Vector();
        vector.addElement("MUL2_EMP");
        vector.addElement("MUL2_EMP_INFO");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("MUL2_EMP.EMP_ID");
        descriptor.addPrimaryKeyFieldName("MUL2_EMP.EMP_NUM");

        // SECTION: PROPERTIES
        descriptor.setIdentityMapClass(FullIdentityMap.class);
        descriptor.setExistenceChecking("Check cache");
        descriptor.setIdentityMapSize(100);

        // SECTION: COPY POLICY
        descriptor.createCopyPolicy("constructor");

        // SECTION: INSTANTIATION POLICY
        descriptor.createInstantiationPolicy("constructor");

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping5 = new DirectToFieldMapping();
        directtofieldmapping5.setAttributeName("id");
        directtofieldmapping5.setIsReadOnly(false);
        directtofieldmapping5.setGetMethodName("getId");
        directtofieldmapping5.setSetMethodName("setId");
        directtofieldmapping5.setFieldName("MUL2_EMP.EMP_ID");
        descriptor.addMapping(directtofieldmapping5);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping6 = new DirectToFieldMapping();
        directtofieldmapping6.setAttributeName("employeeNumber");
        directtofieldmapping6.setIsReadOnly(false);
        directtofieldmapping6.setGetMethodName("getEmployeeNumber");
        directtofieldmapping6.setSetMethodName("setEmployeeNumber");
        directtofieldmapping6.setFieldName("MUL2_EMP.EMP_NUM");
        descriptor.addMapping(directtofieldmapping6);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping7 = new DirectToFieldMapping();
        directtofieldmapping7.setAttributeName("extraInfo");
        directtofieldmapping7.setIsReadOnly(false);
        directtofieldmapping7.setGetMethodName("getExtraInfo");
        directtofieldmapping7.setSetMethodName("setExtraInfo");
        directtofieldmapping7.setFieldName("MUL2_EMP_INFO.INFO");
        descriptor.addMapping(directtofieldmapping7);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping8 = new DirectToFieldMapping();
        directtofieldmapping8.setAttributeName("firstName");
        directtofieldmapping8.setIsReadOnly(false);
        directtofieldmapping8.setGetMethodName("getFirstName");
        directtofieldmapping8.setSetMethodName("setFirstName");
        directtofieldmapping8.setFieldName("MUL2_EMP.FNAME");
        descriptor.addMapping(directtofieldmapping8);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping9 = new DirectToFieldMapping();
        directtofieldmapping9.setAttributeName("employeeNumberB");
        directtofieldmapping9.setIsReadOnly(false);
        directtofieldmapping9.setGetMethodName("getEmployeeNumberB");
        directtofieldmapping9.setSetMethodName("setEmployeeNumberB");
        directtofieldmapping9.setFieldName("MUL2_EMP_INFO.EMP_NUM");
        descriptor.addMapping(directtofieldmapping9);

        descriptor.addForeignKeyFieldNameForMultipleTable("MUL2_EMP_INFO.EMP_NUM", "MUL2_EMP.EMP_NUM");

        addDescriptor(descriptor);
    }
}
