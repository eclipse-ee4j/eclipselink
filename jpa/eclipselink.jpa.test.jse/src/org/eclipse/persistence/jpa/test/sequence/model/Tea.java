/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/18/2022
 *       - Bug 579409: Add support for accurate IDENTITY generation when the database contains separate TRIGGER objects
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.sequence.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;

@Entity
@SecondaryTable(name = "TEA_DETAILS", pkJoinColumns = @PrimaryKeyJoinColumn(name = "SECONDTABLE_ID", referencedColumnName = "ID"))
public class Tea implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    protected String name;
    protected Double price;

    @Column(table = "TEA_DETAILS")
    protected String manufacturer;
    @Column(table = "TEA_DETAILS")
    protected Double cost;

    private Set<TeaShop> teaShops;

    public Tea() { }

    public Long getId() {
        return id;
    }

    public Tea(String name, Double price, String manufacturer, Double cost) {
        this.name = name;
        this.price = price;
        this.manufacturer = manufacturer;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    @ManyToMany(mappedBy = "teas", targetEntity = TeaShop.class)
    public Set<TeaShop> getTeaShops() {
        return this.teaShops;
    }

    public void setCaves(Set<TeaShop> teaShops) {
        this.teaShops = teaShops;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tea) {
            Tea other = (Tea) obj;
            return other.name.equals(name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (name == null) {
            return 0;
        }
        return name.hashCode();
    }

}
