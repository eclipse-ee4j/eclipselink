/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelement.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement
public class Order {

    @XmlTransient
    private int id;
    @XmlPath(value="items/item")
    private List<Item> items;
    private Customer customer;
    @XmlPath(value="customerInput/comments/text()")
    private String[] comments;
    public Class someClass;

    public Order(){
        items = new ArrayList<Item>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String[] getComments() {
        return comments;
    }

    public void setComments(String[] comments) {
        this.comments = comments;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

     public boolean equals(Object obj) {
            Order o;
            try {
                o = (Order) obj;
            } catch (ClassCastException cce) {
                return false;
            }

            if(customer == null){
                if(o.customer != null){
                    return false;
                }
            } else if(!customer.equals(o.customer)){
                return false;
            }

            if(items == null){
                if(o.items != null){
                    return false;
                }
            } else if(items.size() != o.items.size()){
                return false;
            }else if(!items.containsAll(o.items)){
                return false;
            }
            return id == o.id && Arrays.equals(comments, o.comments);
     }

    @Override
    public String toString() {
        return "Order [id=" + id + ", customer=" + customer + ", comments=" + Arrays.toString(comments) + "]";
    }

}
