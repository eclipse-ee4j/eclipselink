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
package org.eclipse.persistence.testing.tests.manual;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.testing.models.insurance.Claim;
import org.eclipse.persistence.testing.models.insurance.Address;
import org.eclipse.persistence.testing.models.insurance.PolicyHolder;
import org.eclipse.persistence.testing.models.insurance.Policy;

public class DeleteOrderUnitOfWorkTest extends ManualVerifyTestCase {
    public DeleteOrderUnitOfWorkTest() {
        setDescription("The delete order should match the following: CLAIM, POLICY, ADDRESS, HOLDER");
    }

    public void reset() {
        ClassDescriptor policyHolderDescriptor = getSession().getClassDescriptor(org.eclipse.persistence.testing.models.insurance.PolicyHolder.class);
        ClassDescriptor policyDescriptor = getSession().getClassDescriptor(org.eclipse.persistence.testing.models.insurance.Policy.class);

        OneToOneMapping addressMapping = (OneToOneMapping)policyHolderDescriptor.getMappingForAttributeName("address");
        addressMapping.setIsPrivateOwned(true);
        addressMapping.initialize(getAbstractSession());

        OneToManyMapping policiesMapping = (OneToManyMapping)policyHolderDescriptor.getMappingForAttributeName("policies");
        policiesMapping.setIsPrivateOwned(true);
        policiesMapping.initialize(getAbstractSession());

        OneToManyMapping claimsMapping = (OneToManyMapping)policyDescriptor.getMappingForAttributeName("claims");
        claimsMapping.setIsPrivateOwned(true);
        claimsMapping.initialize(getAbstractSession());
        rollbackTransaction();
    }

    protected void setup() {
        beginTransaction();
        // Setup complex mapping employee as well.
        ClassDescriptor policyHolderDescriptor = getSession().getClassDescriptor(org.eclipse.persistence.testing.models.insurance.PolicyHolder.class);
        ClassDescriptor policyDescriptor = getSession().getClassDescriptor(org.eclipse.persistence.testing.models.insurance.Policy.class);

        OneToOneMapping addressMapping = (OneToOneMapping)policyHolderDescriptor.getMappingForAttributeName("address");
        addressMapping.setIsPrivateOwned(false);
        addressMapping.initialize(getAbstractSession());

        OneToManyMapping policiesMapping = (OneToManyMapping)policyHolderDescriptor.getMappingForAttributeName("policies");
        policiesMapping.setIsPrivateOwned(false);
        policiesMapping.initialize(getAbstractSession());

        OneToManyMapping claimsMapping = (OneToManyMapping)policyDescriptor.getMappingForAttributeName("claims");
        claimsMapping.setIsPrivateOwned(false);
        claimsMapping.initialize(getAbstractSession());
    }

    protected void test() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("id").equal(303);

        UnitOfWork uow = getSession().acquireUnitOfWork();
        Claim claim = (Claim)uow.readObject(org.eclipse.persistence.testing.models.insurance.Claim.class, expression);
        Policy policy = claim.getPolicy();
        PolicyHolder holder = policy.getPolicyHolder();
        Address address = holder.getAddress();

        uow.deleteObject(claim);
        uow.deleteObject(address);
        uow.deleteObject(policy);
        uow.deleteObject(holder);

        uow.commit();
    }
}
