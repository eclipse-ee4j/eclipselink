/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.descriptors.MethodAttributeAccessor;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;


//Created by Vesna
//Feb 2k3
//uses class org.eclipse.persistence.testing.tests.validation.PersonWithValueHolder

public class ParameterAndMappingWithIndirectionMismatchTest extends ExceptionTest {
    IntegrityChecker orgIntegrityChecker;

    public ParameterAndMappingWithIndirectionMismatchTest() {

        super();
        setDescription("This tests Parameter And Mapping With Indirection Mismatch (TL-ERROR 129)");
    }

    protected void setup() {
        expectedException = DescriptorException.parameterAndMappingWithIndirectionMismatch(new OneToOneMapping());
        orgIntegrityChecker = getSession().getIntegrityChecker();
        getSession().setIntegrityChecker(new IntegrityChecker());
        getSession().getIntegrityChecker().dontCatchExceptions();
    }

    public void reset() {
        if (orgIntegrityChecker != null) {
            getSession().setIntegrityChecker(orgIntegrityChecker);
        }
    }

    public void test() {
        ForeignReferenceMapping addressMapping = (ForeignReferenceMapping)descriptor().getMappingForAttributeName("address");
        try {
            addressMapping.getIndirectionPolicy().validateSetMethodParameterType(org.eclipse.persistence.testing.models.employee.domain.Address.class, getSession().getIntegrityChecker());
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
        idMapping.setGetMethodName("getId");
        idMapping.setSetMethodName("setId");
        ((MethodAttributeAccessor)idMapping.getAttributeAccessor()).initializeAttributes(PersonWithValueHolder.class);
        descriptor.addMapping(idMapping);

        OneToOneMapping addressMapping = new OneToOneMapping();
        addressMapping.setAttributeName("address");
        addressMapping.setReferenceClass(org.eclipse.persistence.testing.models.employee.domain.Address.class);
        addressMapping.useBasicIndirection();
        addressMapping.setGetMethodName("getAddress");
        addressMapping.setSetMethodName("setAddress");
        addressMapping.addForeignKeyFieldName("EMPLOYEE.ADDR_ID", "ADDRESS.ADDRESS_ID");
        descriptor.addMapping(addressMapping);

        //    expectedException = DescriptorException.parameterAndMappingWithIndirectionMismatch(addressMapping);
        return descriptor;
    }
}

