/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.distributedservers;

import java.util.Enumeration;

import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;


/**
 * Test changing private parts of an object.
 *
 */
public class VerifyObjectsDeletedFromCacheTest extends VerifyDeletedObjectsTest {
    public PhoneNumber number;

    public VerifyObjectsDeletedFromCacheTest() {
        super();
    }

    public void verify() {
        org.eclipse.persistence.queries.ReadObjectQuery phoneQuery = new org.eclipse.persistence.queries.ReadObjectQuery(PhoneNumber.class);
        phoneQuery.checkCacheOnly();
        phoneQuery.setShouldPrepare(false);
        Enumeration enumtr = this.numbers.elements();
        while (enumtr.hasMoreElements()) {
            phoneQuery.setSelectionObject(enumtr.nextElement());
            if (getObjectFromDistributedSession(phoneQuery) != null) {
                throw new org.eclipse.persistence.testing.framework.TestErrorException("Failed to delete private owned objects from distributed cache");
            }
        }
    }
}
