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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.validation;

import java.io.StringWriter;


//Created by Vesna
//Feb 2k3
//Used in test: org.eclipse.persistence.testing.tests.validation.NullPointerWhileGettingValueThruInstanceVariableAccessorTest (TL-ERROR 69)
//used in test: org.eclipse.persistence.testing.tests.validation.IllegalArgumentWhileGettingValueThruInstanceVariableAccessorTest (TL-ERROR 26)
//used in test: org.eclipse.persistence.testing.tests.validation.IllegalArgumentWhileSettingValueThruInstanceVariableAccessorTest (TL-ERROR 32)

public class PersonInstanceAccess {
    //public long p_id;
    //public String p_name;
    public long p_id;
    private String p_name;
    //public ValueHolderInterface address;


    public PersonInstanceAccess() {
        this.p_name = "";
        //this.address = new ValueHolder();
    }

    public long getId() {
        return p_id;
    }

    public void setId(long id) {
        this.p_id = id;
    }

    //private String getName() {  return p_name; }
    //private void setName(String name) { this.p_name = name; }

    public String getName() {
        return p_name;
    }

    public void setName(String name) {
        this.p_name = name;
    }

    //public ValueHolderInterface getAddressHolder() { return address; }
    //public void setAddressHolder(ValueHolderInterface addressNew) { this.address = addressNew; }

    //public Address getAddress() { return (Address) address.getValue(); }
    //public void setAddress(Address addressNew) { this.address.setValue(addressNew); }

    public String toString() {
        StringWriter writer = new StringWriter();
        writer.write("Person: ");
        writer.write(getName());
        writer.write(" ");
        return writer.toString();
    }
}

