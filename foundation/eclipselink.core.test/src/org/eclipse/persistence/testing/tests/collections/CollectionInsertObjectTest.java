/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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