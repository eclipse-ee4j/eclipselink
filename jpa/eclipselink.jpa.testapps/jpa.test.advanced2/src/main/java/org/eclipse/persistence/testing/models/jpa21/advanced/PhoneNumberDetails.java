/*
 * Copyright (c) 2015, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.models.jpa21.advanced;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name="JPA21_PHONE_NUMBER_DETAILS")
@IdClass(PhoneNumberPK.class)
public class PhoneNumberDetails {
    private Integer id;
    private String type;

    @Id
    @Column(name="PHONE_NUMBER_ID")
    public Integer getId() {
        return id;
    }

    @Id
    @Column(name="PHONE_NUMBER_TYPE")
    public String getType() {
        return type;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }
}
