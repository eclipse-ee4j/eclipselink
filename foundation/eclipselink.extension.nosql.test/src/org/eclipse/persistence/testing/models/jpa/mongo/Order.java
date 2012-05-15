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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.mongo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.Field;
import org.eclipse.persistence.nosql.annotations.JoinField;
import org.eclipse.persistence.nosql.annotations.JoinFields;
import org.eclipse.persistence.nosql.annotations.NoSql;
import org.eclipse.persistence.internal.helper.Helper;

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
    public String id;
    @Version
    public long version;
    public String orderedBy;
    @Temporal(TemporalType.TIMESTAMP)
    public Date orderDate = new Date();
    public java.sql.Time orderTime = Helper.timeFromDate(new Date());
    public java.sql.Timestamp orderTimestamp = new java.sql.Timestamp(new Date().getTime());
    public java.sql.Date orderDateDate = Helper.dateFromCalendar(Calendar.getInstance());
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar orderCal = Calendar.getInstance();
    public byte[] image = "bytes".getBytes();
    public BigDecimal amount = BigDecimal.valueOf(0);
    public BigInteger amountBigInt = BigInteger.valueOf(0);
    public int amountInt = 0;
    public long amountLong = 0;
    public double amountDouble = 0;
    public float amountFloat = 0;
    public short amountShort = 0;
    @Field(name="address")
    public Address address;
    @OneToOne
    @JoinField(name="customerId", referencedFieldName="_id")
    public Customer customer;
    @ManyToOne
    @JoinFields({
        @JoinField(name="buyerId1", referencedFieldName="ID1"),
        @JoinField(name="buyerId2", referencedFieldName="ID2")
    })
    @Field(name="buyer")
    public Buyer buyer;
    @OneToMany
    @JoinField(name="customerIds", referencedFieldName="_id")
    public List<Customer> customers = new ArrayList();
    @ManyToMany
    @JoinFields({
        @JoinField(name="buyerId1", referencedFieldName="ID1"),
        @JoinField(name="buyerId2", referencedFieldName="ID2")
    })
    @Field(name="buyers")
    public Set<Buyer> buyers = new HashSet();
    @ElementCollection
    @Field(name="items")
    public List<LineItem> lineItems = new ArrayList();
    @ElementCollection
    @MapKey(name="lineNumber")
    @Field(name="lineItemsByNumber")
    public Map<Long, LineItem> lineItemsByNumber = new HashMap();
    @ElementCollection
    @Field(name="comments")
    public List<String> comments = new ArrayList();

    public String toString() {
        return "Order(" + id + ", " + orderedBy + ", " + address + ", " + lineItems + ", " + comments + ")";
    }
}
