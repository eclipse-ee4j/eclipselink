/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.models.jpa22.advanced;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name="JPA22_PHONE_NUMBER_DETAILS")
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
