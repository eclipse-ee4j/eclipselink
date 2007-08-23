/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.complexaggregate;

import javax.persistence.*;

@Embeddable
public class Name  {
	private String firstName;
	private String lastName;
    
    public Name() {}

    @Column(name="FNAME")
    public String getFirstName() {
        return firstName;
    }
    
    @Column(name="LNAME")
    public String getLastName() {
        return lastName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String toString() {
        return firstName + " " + lastName;
    }
}
