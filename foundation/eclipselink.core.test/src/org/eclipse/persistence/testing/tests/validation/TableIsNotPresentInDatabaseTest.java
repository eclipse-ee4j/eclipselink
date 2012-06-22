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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;


//Created by Ian Reid
//Date: Feb 26, 2k3

public class TableIsNotPresentInDatabaseTest extends ExceptionTest {
    public TableIsNotPresentInDatabaseTest() {
        super();
        setDescription("This tests Table Is Not Present In Database (TL-ERROR 142) " + "");
    }

    protected void setup() {
        expectedException = DescriptorException.tableIsNotPresentInDatabase(new RelationalDescriptor());

        orgIntegrityChecker = getSession().getIntegrityChecker();
    }

    IntegrityChecker orgIntegrityChecker;

    public void reset() {
        if (orgIntegrityChecker != null)
            getSession().setIntegrityChecker(orgIntegrityChecker);
    }

    public void test() {
        try {
            getSession().setIntegrityChecker(new IntegrityChecker());
            getSession().getIntegrityChecker().dontCatchExceptions();
            getSession().getIntegrityChecker().checkDatabase();

            RelationalDescriptor descriptor = descriptor();
            DatabaseTable table = descriptor.getTables().lastElement(); //retrieving address table

            //the following causes the correct error to occure. 
            table.setName("Bad_Table"); //change name of table to cause error
            getSession().getIntegrityChecker().getTables().remove(table); //ensure table does not exist
            //   descriptor.getTables().remove(table);//if you remove it from this vector, it will not check it.
            descriptor.initialize((AbstractSession)getSession());//primary keys need to be initialized for postInit to work
            descriptor.postInitialize((AbstractSession)getSession());
            //    descriptor.checkDatabase(getSession()); //error thrown inside this method

        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    public RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems.class);
        descriptor.addTableName("EMPLOYEE");
        descriptor.addTableName("ADDRESS");

        descriptor.addPrimaryKeyFieldName("EMPLOYEE.EMP_ID");

        // Descriptor properties.
        descriptor.useFullIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.useRemoteFullIdentityMap();
        descriptor.setRemoteIdentityMapSize(100);
        //	descriptor.setSequenceNumberFieldName("EMP_ID");
        //	descriptor.setSequenceNumberName("EMP_SEQ");

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("EMPLOYEE.EMP_ID");
        descriptor.addMapping(idMapping);

        // Mappings.
        DirectToFieldMapping firstNameMapping = new DirectToFieldMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setFieldName("EMPLOYEE.F_NAME");
        firstNameMapping.setNullValue("");
        descriptor.addMapping(firstNameMapping);

        OneToOneMapping addressMapping = new OneToOneMapping();
        addressMapping.setAttributeName("address");
        addressMapping.setReferenceClass(org.eclipse.persistence.testing.models.employee.domain.Address.class);
        addressMapping.useBasicIndirection();
        addressMapping.privateOwnedRelationship();
        addressMapping.addForeignKeyFieldName("EMPLOYEE.ADDR_ID", "ADDRESS.ADDRESS_ID");
        descriptor.addMapping(addressMapping);

        return descriptor;
    }
}
