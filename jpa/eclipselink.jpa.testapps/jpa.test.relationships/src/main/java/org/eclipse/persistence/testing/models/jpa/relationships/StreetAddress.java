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
import jakarta.persistence.Embeddable;

// Issue #2414 test model.
/**
 * Street address.
 * {@link Embeddable} class with {@link ElementCollection} attribute.
 */
@Embeddable
public class StreetAddress {

    private int houseNumber;

    private String streetName;

    @ElementCollection
    private List<String> recipientInfo;

    public StreetAddress() {
        this(-1, null, List.of());
    }

    public StreetAddress(int houseNumber, String streetName, List<String> recipientInfo) {
        this.houseNumber = houseNumber;
        this.streetName = streetName;
        this.recipientInfo = recipientInfo;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public List<String> getRecipientInfo() {
        return recipientInfo;
    }

    public void setRecipientInfo(List<String> recipientInfo) {
        this.recipientInfo = recipientInfo;
    }

}
