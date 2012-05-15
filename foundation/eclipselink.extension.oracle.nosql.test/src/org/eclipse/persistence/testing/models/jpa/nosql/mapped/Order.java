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
package org.eclipse.persistence.testing.models.jpa.nosql.mapped;

import java.util.*;

import javax.persistence.Basic;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.eclipse.persistence.annotations.DataFormatType;
import org.eclipse.persistence.annotations.NoSql;

/**
 * Model order class, maps to ORDER record.
 */
@Entity
@NoSql(dataFormat=DataFormatType.MAPPED, dataType="Order-mapped")
public class Order {
    @Id
    public long id;
    @Basic
    public String orderedBy;
    @Embedded
    public Address address;
    @OneToOne
    public Customer customer;
    @Basic
    public List<LineItem> lineItems = new ArrayList();
    @Basic
    public List<String> comments = new ArrayList();

    public String toString() {
        return "Order(" + id + ", " + orderedBy + ", " + address + ", " + lineItems + ")";
    }
}
