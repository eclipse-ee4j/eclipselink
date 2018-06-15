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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpql;

import org.eclipse.persistence.testing.models.employee.domain.*;

public class NullDomainObjectComparer extends org.eclipse.persistence.testing.tests.jpql.DomainObjectComparer {
    public boolean compareObjects(Employee emp1, Employee emp2) {
        if (doesObjectContainNulls(emp1) && doesObjectContainNulls(emp2)) {
            return true;
        } else {
            return super.compareObjects(emp1, emp2);
        }
    }

    public boolean doesObjectContainNulls(Employee employee) {
        if (employee.getFirstName() == null) {
            return true;
        } else if (employee.getLastName() == null) {
            return true;
        }
        return false;
    }
}
