/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     10/18/2010-2.2 Guy Pelletier
//       - 326973: TABLE_PER_CLASS with EmbeddedId results in DescriptorException EclipseLink-74
//                 "The primary key fields are not set for this descriptor"
package org.eclipse.persistence.testing.models.jpa.inheritance;

import static jakarta.persistence.InheritanceType.TABLE_PER_CLASS;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.Table;

/**
 * Model class added to test metadata processing error for bug 326973.
 * Model not added to the InheritanceTableCreator.
 */
@Entity
@Inheritance(strategy=TABLE_PER_CLASS)
@Table(name="TPC_HOTEL")
public class Hotel {
    @EmbeddedId
    HotelId hotelId;

    public HotelId getHotelId() {
        return hotelId;
    }

    public void setHotelId(HotelId hotelId) {
        this.hotelId = hotelId;
    }
}
