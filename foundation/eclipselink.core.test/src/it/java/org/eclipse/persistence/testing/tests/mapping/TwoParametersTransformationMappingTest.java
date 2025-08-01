/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.mapping;

import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.WriteObjectTest;
import org.eclipse.persistence.testing.models.mapping.Address;

public class TwoParametersTransformationMappingTest extends WriteObjectTest {
    public Address address;

    public TwoParametersTransformationMappingTest(Object originalObject) {
        super(originalObject);
    }

    public Address getAddress() {
        return address;
    }

    @Override
    protected void verify() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        this.objectFromDatabase = getSession().executeQuery(this.query);
        this.address = (Address) this.objectFromDatabase;
        if (getAddress().employeeRows == null) {
            throw new TestErrorException("The 2 parameter transformation mapping has failed");
        }
    }
}
