/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.mongo;

import javax.persistence.Embeddable;

import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.Field;
import org.eclipse.persistence.nosql.annotations.NoSql;


/**
 * Model address class, maps to ADDRESS record.
 */
@Embeddable
@NoSql(dataFormat=DataFormatType.MAPPED)
public class Address {
    @Field(name="addressee")
    public String addressee;
    public String street;
    public String city;
    public String state;
    public String country;
    public String zipCode;
    public AddressType type;

    public enum AddressType { Home, Work }

    public String toString() {
        return "Address(" + addressee + ", " + street + ", " + city + ", " + state + ", " + country + ", " + zipCode + ")";
    }
}
