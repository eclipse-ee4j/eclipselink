/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.descriptors.InstanceVariableAttributeAccessor;
import org.eclipse.persistence.internal.indirection.TransparentIndirectionPolicy;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;


//Created by Vesna
//Mar 2k3
//uses class org.eclipse.persistence.testing.tests.validation.PersonWithValueHolder

public class IndirectContainerInstantiationMismatchTest extends ExceptionTest {
    IntegrityChecker orgIntegrityChecker;

    public IndirectContainerInstantiationMismatchTest() {
        super();
        setDescription("This tests Indirect Container Instantiation Mismatch (TL-ERROR 150)");
    }

    protected void setup() {
        expectedException = DescriptorException.indirectContainerInstantiationMismatch(null, new OneToManyMapping());
        orgIntegrityChecker = getSession().getIntegrityChecker();
        getSession().setIntegrityChecker(new IntegrityChecker());
        getSession().getIntegrityChecker().dontCatchExceptions();
    }

    public void reset() {
        if (orgIntegrityChecker != null)
            getSession().setIntegrityChecker(orgIntegrityChecker);
    }

    public void test() {
        PersonWithValueHolder person = new PersonWithValueHolder();
        ForeignReferenceMapping phoneNumbersMapping = (ForeignReferenceMapping)descriptor().getMappingForAttributeName("phoneNumbers");
        try {
            ((TransparentIndirectionPolicy)phoneNumbersMapping.getIndirectionPolicy()).validateAttributeOfInstantiatedObject(person);
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    public RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.tests.validation.PersonWithValueHolder.class);
        descriptor.addTableName("EMPLOYEE");
        descriptor.addPrimaryKeyFieldName("EMPLOYEE.EMP_ID");

        // Descriptor properties.
        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("p_id");
        idMapping.setFieldName("EMPLOYEE.EMP_ID");
        ((InstanceVariableAttributeAccessor)idMapping.getAttributeAccessor()).initializeAttributes(PersonWithValueHolder.class);
        descriptor.addMapping(idMapping);

        OneToManyMapping phoneNumbersMapping = new OneToManyMapping();
        phoneNumbersMapping.setAttributeName("phoneNumbers");
        phoneNumbersMapping.setReferenceClass(org.eclipse.persistence.testing.models.employee.domain.PhoneNumber.class);
        phoneNumbersMapping.dontUseIndirection();
        phoneNumbersMapping.useTransparentCollection();
        phoneNumbersMapping.privateOwnedRelationship();
        phoneNumbersMapping.addTargetForeignKeyFieldName("PHONE.EMP_ID", "EMPLOYEE.EMP_ID");
        ((InstanceVariableAttributeAccessor)phoneNumbersMapping.getAttributeAccessor()).initializeAttributes(PersonWithValueHolder.class);
        descriptor.addMapping(phoneNumbersMapping);

        return descriptor;
    }
}
