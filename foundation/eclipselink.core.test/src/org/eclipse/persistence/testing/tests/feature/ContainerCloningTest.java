/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Chris Delahunt - initial API and implementation
package org.eclipse.persistence.testing.tests.feature;

import java.util.Collection;
import java.util.Map;

import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.queries.CollectionContainerPolicy;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.MapContainerPolicy;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/*
 * Added for bug 241322 - JPA Collection mapping assumes clone is implemented/supported
 * tests java.util.Arrays$ArrayList from java.util.Arrays.asList()
 * and java.util.WeakHashMap() neither of which implement cloneable.
 */
public class ContainerCloningTest extends AutoVerifyTestCase {
    public ContainerCloningTest() {
        setDescription("Test the cloneFor method on collections that are not cloneable.");
    }

    public void test() {
        ContainerPolicy cp = new CollectionContainerPolicy();
        cp.setContainerClass(ClassConstants.ArrayList_class);
        Collection originalC = java.util.Arrays.asList(new Employee());
        Collection cloneC = (Collection)cp.cloneFor(originalC);

        cp = new MapContainerPolicy();
        cp.setContainerClass(java.util.WeakHashMap.class);
        Map originalM = new java.util.WeakHashMap();
        originalM.put(1, 2);
        Map cloneM = (Map)cp.cloneFor(originalM);

        if ((originalC == cloneC) || (originalC.size() != cloneC.size())) {
            throw new TestErrorException("Cloned Collections are not copies.");
        }
        if ((originalM == cloneM) || (originalM.size() != cloneM.size())) {
            throw new TestErrorException("Cloned Maps are not copies.");
        }

        if (!originalC.iterator().next().equals(cloneC.iterator().next()))  {
            throw new TestErrorException("Cloned Collections are not the same.");
        }
        if (!originalM.get(1).equals(cloneM.get(1)))  {
            throw new TestErrorException("Cloned Maps are not the same.");
        }
    }
}
