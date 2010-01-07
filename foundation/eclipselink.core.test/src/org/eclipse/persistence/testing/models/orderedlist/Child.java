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
 *     05/05/2009 Andrei Ilitchev 
 *       - JPA 2.0 - OrderedList support.
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.orderedlist;

import java.io.StringWriter;
import java.math.BigDecimal;

public class Child {
    public BigDecimal id;
    /** Direct-to-field mapping, String -> VARCHAR. */
    public String firstName;
    public String lastName;
    public int allowance;
    
    public Child() {
        super();
    }
    
    public Child(String firstName) {
        this();
        this.firstName = firstName;
        this.lastName = "";
    }
    
    public Child(String firstName, String lastName) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    public BigDecimal getId() {
        return this.id;
    }
    
    public void setId(BigDecimal id) {
        this.id = id;
    }

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

    public int getAllowance() {
        return allowance;
    }

    public void setAllowance(int allowance) {
        this.allowance = allowance;
    }

    /**
     * Print the first & last name
     */
    public String toString() {
        StringWriter writer = new StringWriter();

        writer.write("Child: ");
        writer.write(getFirstName());
        writer.write(" ");
        writer.write(getLastName());
        return writer.toString();
    }
}
