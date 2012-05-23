/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.relationshipmaintenance;

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

public class Dept implements Serializable {

    /**
     * Map empCollection <-> model.Emp
     * @associates <{model.Emp}>
     */
    public Collection empCollection;
    public Double deptno;
    public String dname;

    public Dept() {
        super();
        this.empCollection = new Vector();
    }

    public void addToEmpCollection(Emp anEmp) {
        this.empCollection.add(anEmp);
    }

    public Double getDeptno() {
        return this.deptno;
    }

    public String getDname() {
        return this.dname;
    }

    public Collection getEmpCollection() {
        return this.empCollection;
    }

    public void removeFromEmpCollection(Emp anEmp) {
        this.empCollection.remove(anEmp);
    }

    public void setDeptno(Double deptno) {
        this.deptno = deptno;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public void setEmpCollection(Collection empCollection) {
        this.empCollection = empCollection;
    }
}
