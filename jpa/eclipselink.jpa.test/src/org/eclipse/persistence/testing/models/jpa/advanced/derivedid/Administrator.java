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
 *     02/02/2009-2.0 Chris delahunt 
 *       - 241765: JPA 2.0 Derived identities
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;
import javax.persistence.Version;

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
