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
package org.eclipse.persistence.testing.tests.collections;

import org.eclipse.persistence.testing.models.collections.Restaurant;

public class CollectionInsertObjectTest extends org.eclipse.persistence.testing.framework.InsertObjectTest {
    public CollectionInsertObjectTest() {
        super();
    }

    public CollectionInsertObjectTest(Object originalObject) {
        super(originalObject);
    }

    public static Object buildInstanceToInsert() {
        Restaurant instance = new Restaurant();
        instance.setName("ToGos");
        return instance;
    }
}
