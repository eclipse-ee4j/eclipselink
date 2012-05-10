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
import org.eclipse.persistence.internal.queries.MapContainerPolicy;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.sessions.DatabaseSession;


/**
 * Bug 2618982
 * Ensure an appropriate exception is thrown when bidirectional relationshiop maintenance is used
 * with a map class
 */
public class BidirectionWithHashtableTest extends ExceptionTest {
    public BidirectionWithHashtableTest() {
        super();
        setDescription("This test tests to ensure an appropriate exception is thrown when a Map class is used" + "with bidirectional relationship maintenance.");
    }

    public RelationalDescriptor descriptor() {
        RelationalDescriptor employeeDescriptor = new RelationalDescriptor();
        employeeDescriptor.setJavaClass(Employee.class);
        employeeDescriptor.setTableName("VAL_EMP");
        employeeDescriptor.setPrimaryKeyFieldName("ID");

        OneToManyMapping phoneNumbersMapping = new OneToManyMapping();
        phoneNumbersMapping.setAttributeName("phoneNumbers");
        phoneNumbersMapping.setReferenceClass(org.eclipse.persistence.testing.models.employee.domain.PhoneNumber.class);
        phoneNumbersMapping.useTransparentCollection();
        phoneNumbersMapping.useMapClass(org.eclipse.persistence.indirection.IndirectMap.class, "getNumber");
        phoneNumbersMapping.privateOwnedRelationship();
        phoneNumbersMapping.addTargetForeignKeyFieldName("PHONE.EMP_ID", "EMPLOYEE.EMP_ID");
        phoneNumbersMapping.setRelationshipPartnerAttributeName("employee");
        employeeDescriptor.addMapping(phoneNumbersMapping);

        return employeeDescriptor;
    }

    protected void setup() {
        expectedException = DescriptorException.unsupportedTypeForBidirectionalRelationshipMaintenance(new OneToManyMapping(), new MapContainerPolicy());
    }

    public void test() {
        try {
            getSession().setIntegrityChecker(new IntegrityChecker());
            getSession().getIntegrityChecker().dontCatchExceptions();
            ((DatabaseSession)getSession()).addDescriptor(descriptor());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }
}
