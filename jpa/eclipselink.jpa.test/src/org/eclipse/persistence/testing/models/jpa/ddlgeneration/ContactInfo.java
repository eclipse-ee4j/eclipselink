/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Embeddable
public class ContactInfo {
    @ManyToMany(targetEntity=PhoneNumber.class, cascade=PERSIST, fetch=EAGER)
    @JoinTable(name="SHOULD_BE_OVERRIDEN_AND_NAME_TO_LONG_FOR_DATABASE_WILL_CAUSE_ERROR_NOT_GOOD_VERY_BAD_INDEED")
    public List phoneNumbers;

    public ContactInfo() {
        phoneNumbers = new ArrayList<PhoneNumber>();
    }
    
    public void addPhoneNumber(PhoneNumber phoneNumber) {
        phoneNumbers.add(phoneNumber);
    }
    
    public List getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
}
