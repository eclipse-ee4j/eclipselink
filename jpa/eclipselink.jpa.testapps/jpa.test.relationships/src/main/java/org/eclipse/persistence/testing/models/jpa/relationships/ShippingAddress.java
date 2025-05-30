/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2025 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing.models.jpa.relationships;

import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

// Issue #2414 test model.
/**
 * Shipping address.
 * {@link Entity} class with {@link Embedded} attribute.
 * {@link Embedded} attribute contains {@link ElementCollection} attribute.
 */
@Entity
public class ShippingAddress {

    @Id private Long id;

    private String city;

    private String state;

    @ElementCollection
    private List<String> names;

    @Embedded
    private StreetAddress streetAddress;

    private int zipCode;

    public ShippingAddress() {
        this(-1L, null, null, -1, List.of(), null);
    }

    public ShippingAddress(Long id,
                           String city,
                           String state,
                           int zipCode,
                           List<String> names,
                           StreetAddress streetAddress) {
        this.id = id;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.names = names;
        this.streetAddress = streetAddress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public StreetAddress getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(StreetAddress streetAddress) {
        this.streetAddress = streetAddress;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

}
