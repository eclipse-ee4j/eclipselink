/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *              James Sutherland - initial example
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.performance2;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.eclipse.persistence.annotations.ChangeTracking;
import org.eclipse.persistence.annotations.ChangeTrackingType;

/**
 * Represents the email address of an employee.
 */
@Embeddable
// TODO, this is currently required to workaround an issue with weaving and ElementCollection mappings.
@ChangeTracking(ChangeTrackingType.DEFERRED)
public class EmailAddress {

    @Column(name = "EMAIL_ADDRESS")
    private String address;

    public EmailAddress() {        
    }
    
    public EmailAddress(String address) {
        setAddress(address);
    }
    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
