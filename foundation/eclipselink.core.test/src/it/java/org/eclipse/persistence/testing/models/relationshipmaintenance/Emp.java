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
package org.eclipse.persistence.testing.models.relationshipmaintenance;

import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;

import java.io.Serializable;

public class Emp implements Serializable {

    /**
     * Map deptno {@literal <}-{@literal >} model.Dept
     * associates {@code <{model.Dept}>}
     */
    public ValueHolderInterface deptno;
    public Double empno;
    public String ename;

    public Emp() {
        super();
        this.deptno = new ValueHolder();
    }

    public Dept getDeptno() {
        return (Dept)this.deptno.getValue();
    }

    protected ValueHolderInterface getDeptnoHolder() {
        return this.deptno;
    }

    public Double getEmpno() {
        return this.empno;
    }

    public String getEname() {
        return this.ename;
    }

    public void setDeptno(Dept deptno) {
        this.deptno.setValue(deptno);
    }

    protected void setDeptnoHolder(ValueHolderInterface deptno) {
        this.deptno = deptno;
    }

    public void setEmpno(Double empno) {
        this.empno = empno;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }
}
