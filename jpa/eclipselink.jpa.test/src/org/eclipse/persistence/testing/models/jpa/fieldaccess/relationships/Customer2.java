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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.fieldaccess.relationships;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;
import java.util.Map;

import javax.persistence.*;

import static javax.persistence.GenerationType.*;
import static javax.persistence.CascadeType.*;

@Entity(name="FieldAccessCustomer2")
@Table(name="CMP3_FIELDACCESS_CUSTOMER2")
public class Customer2 implements java.io.Serializable {
    @Id
    @GeneratedValue
    @Column(name="CUST_ID")
    private Integer customerId;

    @Version
    @Column(name="CUST_VERSION")
    private int version;
   
    private String name;
       
    public Customer2() {}

    public Integer getCustomerId() {
        return customerId;
    }
   
    public void setCustomerId(Integer id) {
        this.customerId = id;
    }

    public int getVersion() {
        return version;
    }
   
    protected void setVersion(int version) {
            this.version = version;
    }

    public String getName() {
        return name;
    }
   
    public void setName(String aName) {
        this.name = aName;
    }
}
