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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.mappings.OneToOneMapping;

/**
 * Bug 5200555
 * A partial attribute query where the partial attribute is also joined should
 * not result in a null pointer exception.
 */
@SuppressWarnings("deprecation")
public class PartialAttributeTestWithJoinAttribute extends TestCase {
    protected int joinFetch;

    public PartialAttributeTestWithJoinAttribute() {
        setDescription("A partial attribute query where the partial attribute is also joined should not result in a null pointer exception.");
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        ClassDescriptor descriptor = getSession().getClassDescriptor(Employee.class);
        OneToOneMapping mapping = (OneToOneMapping)descriptor.getMappingForAttributeName("address");
        joinFetch = mapping.getJoinFetch();
        mapping.useInnerJoinFetch();
        descriptor.reInitializeJoinedAttributes();
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        ClassDescriptor descriptor = getSession().getClassDescriptor(Employee.class);
        OneToOneMapping mapping = (OneToOneMapping)descriptor.getMappingForAttributeName("address");
        mapping.setJoinFetch(joinFetch);
        descriptor.reInitializeJoinedAttributes();
    }

    public void test() {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.dontMaintainCache();
        query.addPartialAttribute("address");
        try {
            getSession().executeQuery(query);
        } catch (RuntimeException exception) {

            throw exception;
        }
    }
}
