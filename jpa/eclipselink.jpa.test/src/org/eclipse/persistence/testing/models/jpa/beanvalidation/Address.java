/*
 * Copyright (c) 2009, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:

package org.eclipse.persistence.testing.models.jpa.beanvalidation;

import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

@Embeddable
public class Address {
    String street;
    String city;
    @Size(max = 2)
    String state;

    public Address() {}

    public Address(String street, String city, String state) {
         this.street = street;
         this.city = city;
         this.state = state;
    }
}
