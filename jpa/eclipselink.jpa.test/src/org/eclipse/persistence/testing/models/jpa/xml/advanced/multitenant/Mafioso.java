/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     03/23/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant;

import java.io.Serializable;

public abstract class Mafioso {
    public enum Gender { Female, Male }
    
    private int id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private MafiaFamily family;

    public Mafioso() {}

    public MafiaFamily getFamily() { 
        return family; 
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public Gender getGender() {
        return gender;
    }
    
    public int getId() { 
        return id; 
    }

    public String getLastName() {
        return lastName;
    }

    public void setFamily(MafiaFamily family) {
        this.family = family;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
    
    public void setId(int id) { 
        this.id = id; 
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
