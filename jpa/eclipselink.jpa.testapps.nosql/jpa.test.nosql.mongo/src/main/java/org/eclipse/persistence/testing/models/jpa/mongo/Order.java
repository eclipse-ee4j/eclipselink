/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.mongo;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKey;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.Field;
import org.eclipse.persistence.nosql.annotations.JoinField;
import org.eclipse.persistence.nosql.annotations.JoinFields;
import org.eclipse.persistence.nosql.annotations.NoSql;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Model order class, maps to ORDER record.
 */
@Entity
@NoSql(dataFormat=DataFormatType.MAPPED)
@NamedQuery(name="Order.findAll", query="Select o from Order o")
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
    public Date orderDate = new Date(Helper.timeWithRoundMiliseconds());
    public java.sql.Time orderTime = Helper.timeFromDate(new Date());
    public java.sql.Timestamp orderTimestamp = new Timestamp(Helper.timeWithRoundMiliseconds());
    public java.sql.Date orderDateDate = Helper.dateFromCalendar(Calendar.getInstance());
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar orderCal = Helper.calendarFromUtilDate(new Date(Helper.timeWithRoundMiliseconds()));
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
    @ManyToOne(optional=false)
    @JoinFields({
        @JoinField(name="buyerId1", referencedFieldName="ID1"),
        @JoinField(name="buyerId2", referencedFieldName="ID2")
    })
    @Field(name="buyer")
    public Buyer buyer;
    @OneToMany
    @JoinField(name="customerIds", referencedFieldName="_id")
    public List<Customer> customers = new ArrayList<>();
    @ManyToMany
    @JoinFields({
        @JoinField(name="buyerId1", referencedFieldName="ID1"),
        @JoinField(name="buyerId2", referencedFieldName="ID2")
    })
    @Field(name="buyers")
    public Set<Buyer> buyers = new HashSet<>();
    @ElementCollection
    @Field(name="items")
    public List<LineItem> lineItems = new ArrayList<>();
    @ElementCollection
    @MapKey(name="lineNumber")
    @Field(name="lineItemsByNumber")
    public Map<Long, LineItem> lineItemsByNumber = new HashMap<>();
    @ElementCollection
    @Field(name="comments")
    public List<String> comments = new ArrayList<>();

    public String toString() {
        return "Order(" + id + ", " + orderedBy + ", " + address + ", " + lineItems + ", " + comments + ")";
    }
}
