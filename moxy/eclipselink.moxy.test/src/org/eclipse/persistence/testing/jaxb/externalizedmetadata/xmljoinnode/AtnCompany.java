/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 11 October 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmljoinnode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AtnCompany {

    public List<AtnEmployee> employees;

    public AtnCompany() {
        employees = new ArrayList<AtnEmployee>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AtnCompany that = (AtnCompany) o;

        if (employees != null ? !Arrays.equals(employees.toArray(), that.employees.toArray()) : that.employees != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return employees != null ? Arrays.hashCode(employees.toArray()) : 0;
    }

    @Override
    public String toString() {
        return "AtnCompany{" +
                "employees=" + employees +
                '}';
    }
}
