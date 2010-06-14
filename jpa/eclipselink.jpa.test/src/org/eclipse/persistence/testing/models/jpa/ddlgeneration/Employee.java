/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     06/14/2010-2.2 Karen Moore 
 *       - 264417:  Table generation is incorrect for JoinTables in AssociationOverrides
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import javax.persistence.AssociationOverride;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;

@Entity(name="DDL_EMP")
public class Employee {
    @Id
    @GeneratedValue
    public int id;
    
    public Employee() {}
   
    @AssociationOverride(
            name="phoneNumbers",
            joinTable=@JoinTable(
                    name="EMPPHONES",
                    joinColumns=@JoinColumn(name="EMP"),
                    inverseJoinColumns=@JoinColumn(name="PHONE")
            )
    )
    @Embedded 
    public ContactInfo contactInfo;

    public void addPhoneNumber(PhoneNumber phoneNumber) {
        phoneNumber.addEmployee(this);
        getContactInfo().addPhoneNumber(phoneNumber);
    }
    
    public ContactInfo getContactInfo() {
        if (contactInfo == null) {
            contactInfo = new ContactInfo();
        }
        
        return contactInfo;
    }
    
    public int getId() {
        return id;
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }
    
    public void setId(int id) {
        this.id = id;
    }
}
