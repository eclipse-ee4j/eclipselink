/*
 * Copyright (c) 2012, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     01/06/2011-2.3 Guy Pelletier
//       - 371453: JPA Multi-Tenancy in Bidirectional OneToOne Relation throws ArrayIndexOutOfBoundsException
package org.eclipse.persistence.testing.models.jpa.advanced.multitenant;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.TenantDiscriminatorColumn;

import static org.eclipse.persistence.annotations.MultitenantType.SINGLE_TABLE;

@Entity
@Table(name="JPA_ENVELOPE")
@Multitenant(SINGLE_TABLE)
@TenantDiscriminatorColumn(name="TENANT_ID", contextProperty="tenant.id")
public class Envelope {
    @Id
    @GeneratedValue
    @Column(name="ID")
    public int id;

    @Column(name="COLOR")
    public String color;

    @OneToOne(mappedBy="envelope", cascade={CascadeType.ALL})
    public Card card;

    public Card getCard() {
        return card;
    }

    public String getColor() {
        return color;
    }

    public int getId() {
        return id;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setId(int id) {
        this.id = id;
    }
}
