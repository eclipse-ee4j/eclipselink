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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.tests.validation.ExceptionTest;


//Created by Ian Reid
//Date: Feb 6, 2k3

public class DescriptorIsMissingTest extends ExceptionTest {


    public DescriptorIsMissingTest() {
        super();
        setDescription("This tests Parent Descriptor Not Specified (TL-ERROR 110) " + "");
    }

    protected void setup() {
        expectedException = DescriptorException.descriptorIsMissing(null, null);
        orgDescriptor = ((DatabaseSession)getSession()).getDescriptor(org.eclipse.persistence.testing.models.insurance.Address.class);
        orgIntegrityChecker = getSession().getIntegrityChecker();
    }
    ClassDescriptor orgDescriptor;
    IntegrityChecker orgIntegrityChecker;

    public void reset() {
        ((DatabaseSession)getSession()).getDescriptors().remove(org.eclipse.persistence.testing.models.insurance.Address.class);
        if (orgDescriptor != null)
            ((DatabaseSession)getSession()).addDescriptor(orgDescriptor);
        if (orgIntegrityChecker != null)
            getSession().setIntegrityChecker(orgIntegrityChecker);
    }


    public void test() {
        try {
            getSession().setIntegrityChecker(new IntegrityChecker());
            getSession().getIntegrityChecker().dontCatchExceptions();
            ((DatabaseSession)getSession()).addDescriptor(buildAddressDescriptor());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    public RelationalDescriptor buildAddressDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.models.insurance.Address.class);
        descriptor.addTableName("INS_ADDR");
        descriptor.addPrimaryKeyFieldName("INS_ADDR.SSN");

        // Descriptor properties.
        descriptor.useFullIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.useRemoteFullIdentityMap();
        descriptor.setRemoteIdentityMapSize(100);

        // Query manager.
        descriptor.getQueryManager().checkDatabaseForDoesExist();

        // Event manager.

        // Mappings.
        OneToOneMapping policyHolderMapping = new OneToOneMapping();
        policyHolderMapping.setAttributeName("policyHolder");
        policyHolderMapping.setGetMethodName("getPolicyHolder");
        policyHolderMapping.setSetMethodName("setPolicyHolder");
        policyHolderMapping.setReferenceClass(org.eclipse.persistence.testing.models.insurance.PolicyHolder.class);
        policyHolderMapping.dontUseIndirection();
        //the following causes the correct error to occure.   
        policyHolderMapping.addForeignKeyFieldName("INS_ADDR.SSN", "HOLDER.SSN");

        descriptor.addMapping(policyHolderMapping);

        return descriptor;
    }


}
