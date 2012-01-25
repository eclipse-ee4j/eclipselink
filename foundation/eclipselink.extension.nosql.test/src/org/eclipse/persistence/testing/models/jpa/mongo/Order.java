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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.mongo;

import java.util.*;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.DataFormatType;
import org.eclipse.persistence.annotations.Field;
import org.eclipse.persistence.annotations.JoinField;
import org.eclipse.persistence.annotations.NoSql;

/**
 * Model order class, maps to ORDER record.
 */
@Entity
@NoSql(dataFormat=DataFormatType.MAPPED)
public class Order {
    @Id
    @GeneratedValue
    // Test that column works too.
    @Column(name="_id")
    //@TypeConverter(name="hex", dataType=byte[].class)
    //@Convert("hex")
    public String id;
    @Version
    public long version;
    public String orderedBy;
    @Field(name="address")
    public Address address;
    @OneToOne
    @JoinField(name="customerId", referencedFieldName="ID")
    public Customer customer;
    @ElementCollection
    @Field(name="items")
    public List<LineItem> lineItems = new ArrayList();
    @ElementCollection
    @Field(name="comments")
    public List<String> comments = new ArrayList();

    public String toString() {
        return "Order(" + id + ", " + orderedBy + ", " + address + ", " + lineItems + ", " + comments + ")";
    }
}
