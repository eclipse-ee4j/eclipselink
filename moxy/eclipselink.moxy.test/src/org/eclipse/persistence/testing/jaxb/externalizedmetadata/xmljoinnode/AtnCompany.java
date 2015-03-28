/*******************************************************************************
 * Copyright (c) 2014, 2015  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 11 October 2012 - 2.4 - Initial implementation
 ******************************************************************************/
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
