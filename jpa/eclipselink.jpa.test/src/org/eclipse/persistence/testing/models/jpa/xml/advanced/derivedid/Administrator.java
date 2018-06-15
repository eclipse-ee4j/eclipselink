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
//     02/02/2009-2.0 Chris delahunt
//       - 241765: JPA 2.0 Derived identities
package org.eclipse.persistence.testing.models.jpa.xml.advanced.derivedid;

import java.sql.Date;

import org.eclipse.persistence.testing.models.jpa.xml.advanced.Employee;

/**
 * @author cdelahun
 *
 */
public class Administrator {

    private Employee emp;
    private Integer version;
    private String contractCompany;
    private Date endDate;

    public String getContractCompany() {
        return contractCompany;
    }

    public Employee getEmployee() {
        return emp;
    }

    public Date getEndDate(){
        return endDate;
    }

    public Integer getVersion() {
        return version;
    }

    public void setContractCompany(String contractCompany) {
        this.contractCompany = contractCompany;
    }

    public void setEmployee(Employee emp) {
        this.emp = emp;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
