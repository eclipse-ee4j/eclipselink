/*
 * Copyright (c) 2014, 2020 Oracle and/or its affiliates. All rights reserved.
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
//      Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.jpars.test.model.basket;

import org.eclipse.persistence.jpa.rs.annotations.RestPageableQueries;
import org.eclipse.persistence.jpa.rs.annotations.RestPageableQuery;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@Table(name = "JPARS_BASKET_ITEM")
@NamedQueries({
        @NamedQuery(
                name = "BasketItem.findAll",
                query = "SELECT bi FROM BasketItem bi ORDER BY bi.id"),
        @NamedQuery(
                name = "BasketItem.findAllPageable",
                query = "SELECT bi FROM BasketItem bi ORDER BY bi.id"),
        @NamedQuery(
                name = "BasketItem.deleteAll",
                query = "DELETE FROM BasketItem bi")
})
@RestPageableQueries({
        @RestPageableQuery(queryName = "BasketItem.findAllPageable", limit = 20)
})
public class BasketItem {

    @Id
    @Column(name = "ITEM_ID")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "BASKET_ID")
    private Basket basket;

    @Column(name = "ITEM_NAME")
    private String name;

    @Column(name = "ITEM_QTY")
    private Integer qty;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Basket getBasket() {
        return basket;
    }

    public void setBasket(Basket basket) {
        this.basket = basket;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    @Override
    public String toString() {
        return "BasketItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", qty=" + qty +
                '}';
    }
}
