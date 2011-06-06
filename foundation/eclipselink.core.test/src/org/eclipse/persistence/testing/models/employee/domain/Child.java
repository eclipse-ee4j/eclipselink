    /*******************************************************************************
     * Copyright (c) 1998, 2011 Oracle. All rights reserved.
     * This program and the accompanying materials are made available under the 
     * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
     * which accompanies this distribution. 
     * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
     * and the Eclipse Distribution License is available at 
     * http://www.eclipse.org/org/documents/edl-v10.php.
     *
     * Contributors:
     *     08/15/2008-1.0.1 Chris Delahunt 
     *       - 237545: List attribute types on OneToMany using @OrderBy does not work with attribute change tracking
     ******************************************************************************/ 

package org.eclipse.persistence.testing.models.employee.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class Child implements Serializable{
    public BigDecimal id;
    /** Direct-to-field mapping, String -> VARCHAR. */
    public String firstName;

    /** Direct-to-field mapping, String -> VARCHAR. */
    public String lastName;

    /** Object-type mapping, maps "Male" -> "M", "Female" -> "F". */
    public String gender;
    
    /** Direct-to-field mapping */
    public java.util.Date birthday;
    
    /** One-to-one mapping, child referencing its parent*/
    public Employee parent;
    
    
    public String getFirstName(){
        return firstName;
    }
    
    public void setFirstName(String firstName){
        this.firstName = firstName;
    }
    
    public String getLastName(){
        return lastName;
    }
    
    public void setLastName(String lastName){
        this.lastName = lastName;
    }
    
    public String getGender(){
        return gender;
    }
    
    public void setGender(String gender){
        this.gender = gender;
    }
    
    public java.util.Date getBirthday(){
        return birthday;
    }
    
    public void setBirthday(java.util.Date birthday){
        this.birthday = birthday;
    }
    
    public Employee getParent(){
        return parent;
    }
    
    public void setParent(Employee parent){
        this.parent = parent;
    }
    
    public boolean equals(java.lang.Object arg0){
        if ( !(arg0 instanceof Child) ){
            return false;
        }
        Child c2 = (Child)arg0;
        if ((id == c2.id) || ( (id !=null) && (c2.id !=null) && id.equals(c2.id) ) ){
            return true;
        }
        return false;
    }
    
}
