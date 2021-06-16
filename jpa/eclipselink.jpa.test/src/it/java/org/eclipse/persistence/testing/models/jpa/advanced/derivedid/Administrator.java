/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     02/02/2009-2.0 Chris delahunt
//       - 241765: JPA 2.0 Derived identities
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import org.eclipse.persistence.testing.models.jpa.advanced.Employee;

/**
 * @author cdelahun
 *
 */
@Entity
@Table(name="CMP3_ADMIN")
@SecondaryTable(name="CMP3_ADMIN_CONTRACT")
public class Administrator {

    private Employee emp;
    private Integer version;
    private String contractCompany;
    private Date endDate;

    @Column(name="CONTRACT_COMPANY")
    public String getContractCompany() {
        return contractCompany;
    }

    @Id
    public Employee getEmployee() {
        return emp;
    }

    @Column(name="END_DATE", table="CMP3_ADMIN_CONTRACT")
    public Date getEndDate(){
        return endDate;
    }

    @Version
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
