/*******************************************************************************
 * Copyright (c) 2009 Sun Microsystems, Inc. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 ******************************************************************************/

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